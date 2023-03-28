package com.about.mantle.web.filter;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.spring.interceptor.CookieInterceptor;

public class CookieInterceptorTest extends CookieInterceptor {
	@Mock
	private HttpServletRequest request;

	@Mock
	private RequestContext requestContext;

	public CookieInterceptorTest() {
		super("test-domain.com", false);
	}

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testIsPageView() {
		testIsPageView("/test-page-123", true);
		testIsPageView("/deferred-resource-42", false, true);
		testIsPageView("/googleCpcRefresh.html", false);
		testIsPageView("/comscore.json", false);
		testIsPageView("/serveModel/model.json", false);
		testIsPageView("/comscore-tracking", false);
		testIsPageView("/comscore-tracking-for-teh-win", true);
		testIsPageView("/g00", false);
		testIsPageView("/g00d", false);
		testIsPageView("/g00dy-two-shoes", false);
		testIsPageView("/manifest.json", false);
		testIsPageView("/MANIFEST.JSON", false);
		testIsPageView("/browserconfig.xml", false);
		testIsPageView("/sw.js", false);
		testIsPageView("/homescreen-sw.js", false);
		testIsPageView("/thmb/sdhkfgsdhfgsdjhfsdjfhg.png", false);
		testIsPageView("/thmboroonie/sdhkfgsdhfgsdjhfsdjfhg.png", true);
		testIsPageView("/embed", false);
		testIsPageView("/embed-for-dummies-123", true);
		testIsPageView("/sponsor-tracking-codes", false);
		testIsPageView("/sponsor-tracking-codes-for-dummies-123", true);
	}

	private void testIsPageView(String resourcePath, boolean expectedValue) {
		this.testIsPageView(resourcePath, expectedValue, false);
	}
	
	private void testIsPageView(String resourcePath, boolean expectedValue, boolean isDeferred) {
		Mockito.when(request.getRequestURI()).thenReturn(resourcePath);
		Mockito.when(requestContext.isDeferred()).thenReturn(isDeferred);
		Assert.assertEquals(resourcePath, expectedValue, this.isPageView(request, requestContext));
	}
}
