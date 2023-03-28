package com.about.mantle.http;

import static com.about.mantle.util.MantlTestUtils.APPLICATION_NAME;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.http.HttpHeader;
import org.junit.Before;
import org.junit.Test;

import com.about.globe.core.http.RequestContextImpl;
import com.about.globe.core.http.RequestContext.Builder;
import com.about.globe.core.http.ua.DefaultUserAgentParser;
import com.about.hippodrome.url.ValidDomainUrlDataFactory;
import com.google.common.net.HttpHeaders;

public class HttpRequestContextContributorTest {

	private HttpRequestContextContributor contributor;
	private HttpServletRequest request;
	private Builder builder;

	@Before
	public void before() {
		this.request = mock(HttpServletRequest.class);
		when(request.getRequestURL()).thenReturn(new StringBuffer().append(
				"https://www.lifewire.com/troubleshooting-slow-internet-connection-818125"));
		when(request.getRequestURI()).thenReturn(
				"/troubleshooting-slow-internet-connection-818125");
		when(request.getMethod()).thenReturn("GET");
		when(request.getScheme()).thenReturn("https");
		when(request.getHeader(HttpHeaders.USER_AGENT)).thenReturn(
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.98 Safari/537.36");
		when(request.getRemoteAddr()).thenReturn("127.0.0.1");
		when(request.getDispatcherType()).thenReturn(DispatcherType.REQUEST);
		
		this.contributor = new HttpRequestContextContributor(new DefaultUserAgentParser(),
				new ValidDomainUrlDataFactory(APPLICATION_NAME), Locale.ENGLISH);

		this.builder = new RequestContextImpl.Builder();
	}

	@Test
	public void testProxyHeader_externalComponent() {
		when(request.getHeader(HttpHeader.X_FORWARDED_HOST.toString())).thenReturn(
				"www.lifewire.com");
		when(request.getHeader(HttpHeader.X_FORWARDED_PROTO.toString())).thenReturn(
				"https");
		contributor.contribute(request, builder);
		assertThat(builder.build().getOriginalRequestApplication(), is("tech"));
	}
	
	@Test
	public void testProxyHeader_fastly() {
		when(request.getHeader(HttpHeader.X_FORWARDED_HOST.toString())).thenReturn(
				"www.lifewire.com, www.lifewire.com");
		when(request.getHeader(HttpHeader.X_FORWARDED_PROTO.toString())).thenReturn(
				"https");
		contributor.contribute(request, builder);
		assertThat(builder.build().getOriginalRequestApplication(), is("tech"));
	}
	
	@Test
	public void testProxyHeader_multiHopProxy() {
		when(request.getHeader(HttpHeader.X_FORWARDED_HOST.toString())).thenReturn(
				"www.lifewire.com, www.thespruce.com");
		when(request.getHeader(HttpHeader.X_FORWARDED_PROTO.toString())).thenReturn(
				"https");
		contributor.contribute(request, builder);
		assertThat("Original Request Application should be from the last host to proxy request",builder.build().getOriginalRequestApplication(), is("lifestyle"));
	}
	
	@Test
	public void testProxyHeader_externalHost() {
		when(request.getHeader(HttpHeader.X_FORWARDED_HOST.toString())).thenReturn(
				"www.test.com");
		when(request.getHeader(HttpHeader.X_FORWARDED_PROTO.toString())).thenReturn(
				"https");
		contributor.contribute(request, builder);
		assertNull(builder.build().getOriginalRequestApplication());
	}
	
	@Test
	public void testProxyHeader_unparseableHost() {
		when(request.getHeader(HttpHeader.X_FORWARDED_HOST.toString())).thenReturn(
				"www.     test.    com");
		when(request.getHeader(HttpHeader.X_FORWARDED_PROTO.toString())).thenReturn(
				"https");
		contributor.contribute(request, builder);
		assertNull(builder.build().getOriginalRequestApplication());
	}
	
	@Test
	public void testProxyHeader_badActor() {
		when(request.getHeader(HttpHeader.X_FORWARDED_HOST.toString())).thenReturn(
				"<script>badActor.js</script>");
		when(request.getHeader(HttpHeader.X_FORWARDED_PROTO.toString())).thenReturn(
				"https");
		contributor.contribute(request, builder);
		assertNull(builder.build().getOriginalRequestApplication());
	}

}
