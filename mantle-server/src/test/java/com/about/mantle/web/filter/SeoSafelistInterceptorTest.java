package com.about.mantle.web.filter;

import static com.about.mantle.util.MantlTestUtils.APPLICATION_NAME;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.web.context.WebApplicationContext;

import com.about.globe.core.render.CoreRenderUtils;
import com.about.hippodrome.url.PlatformUrlDataFactory;
import com.about.hippodrome.url.ValidDomainUrlDataFactory;
import com.about.mantle.model.seo.QueryParamSafelist;
import com.about.mantle.spring.interceptor.SeoSafelistInterceptor;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class SeoSafelistInterceptorTest {
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private FilterChain chain;

	@Mock
	private ServletContextHandler servletContextHandler;
	@Mock
	private ContextHandler.Context serverContext;
	@Mock
	private WebApplicationContext webApplicationContext;
	@Mock
	private CoreRenderUtils renderUtils;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);

		Mockito.when(servletContextHandler.getServletContext()).thenReturn(serverContext);
		Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer().append("https://www.verywellhealth.com/abc-123"));
		Mockito.when(serverContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
				.thenReturn(webApplicationContext);
		Mockito.when(webApplicationContext.getBean(CoreRenderUtils.class)).thenReturn(renderUtils);
		Mockito.when(webApplicationContext.getBean(PlatformUrlDataFactory.class))
				.thenReturn(new ValidDomainUrlDataFactory(APPLICATION_NAME));
		QueryParamSafelist safelist = new QueryParamSafelist(ImmutableSet.of("abc", "def", "__qq", "x123"),
				ImmutableSet.of("qb-"));
		Mockito.when(webApplicationContext.getBean(QueryParamSafelist.class)).thenReturn(safelist);
		Mockito.when(renderUtils.isPresevedQueryParam(Mockito.anyString())).then(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return StringUtils.startsWith((String) invocation.getArguments()[0], "globe");
			}
		});
	}

	@Test
	public void testInterceptorIgnored() throws IOException, ServletException {
		SeoSafelistInterceptor interceptor = new SeoSafelistInterceptor(webApplicationContext.getBean(QueryParamSafelist.class), renderUtils, webApplicationContext.getBean(PlatformUrlDataFactory.class));

		Mockito.when(request.getMethod()).thenReturn("POST");
		Mockito.when(request.getParameterMap())
				.thenReturn(ImmutableMap.of("bad_qs", new String[] { "1", "2" }, "another_param", new String[] { "1" }));
		interceptor.preHandle(request, response, new Object());

		Mockito.verifyZeroInteractions(response);
	}
	
	@Test
	public void testInterceptorPassThrough() throws IOException, ServletException {
		SeoSafelistInterceptor interceptor = new SeoSafelistInterceptor(webApplicationContext.getBean(QueryParamSafelist.class), renderUtils, webApplicationContext.getBean(PlatformUrlDataFactory.class));

		Mockito.when(request.getMethod()).thenReturn("GET");
		Mockito.when(request.getParameterMap())
				.thenReturn(ImmutableMap.of("abc", new String[] { "1", "2" }, "__qq", new String[] { "1" }, "globeABC",
						new String[] { "1" }, "cr", new String[] { "1" }, "qb-12as", new String[] { "1" }));
		interceptor.preHandle(request, response, new Object());

		Mockito.verifyZeroInteractions(response);
	}

	@Test
	public void testInterceptorRedirect() throws IOException, ServletException {
		SeoSafelistInterceptor interceptor = new SeoSafelistInterceptor(webApplicationContext.getBean(QueryParamSafelist.class), renderUtils, webApplicationContext.getBean(PlatformUrlDataFactory.class));

		ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

		Mockito.when(request.getMethod()).thenReturn("GET");
		Mockito.when(request.getParameterMap())
				.thenReturn(ImmutableMap.of("abc", new String[] { "1", "2" }, "__qa", new String[] { "1" }, "globeABC",
						new String[] { "1" }, "cr", new String[] { "1" }, "qb-12as", new String[] { "1" }));
		interceptor.preHandle(request, response, new Object());

		Mockito.verifyZeroInteractions(response);

		Mockito.when(request.getParameterMap())
				.thenReturn(ImmutableMap.of("abcd", new String[] { "1", "2" }, "__qqa", new String[] { "1" }, "xglobeABC",
						new String[] { "1" }, "vcr", new String[] { "1" }, "nqa-12as", new String[] { "1" }));
		interceptor.preHandle(request, response, new Object());

		Mockito.verify(response, Mockito.atLeast(1)).setHeader(nameCaptor.capture(), valueCaptor.capture());
		Assert.assertEquals(HttpHeader.LOCATION.asString(), nameCaptor.getValue());
		Assert.assertEquals("https://www.verywellhealth.com/abc-123",
				valueCaptor.getValue());
	}
}
