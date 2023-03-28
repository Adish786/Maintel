package com.about.mantle.logging;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.about.mantle.util.MantlTestUtils.*;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.ua.DeviceCategory;
import com.about.globe.core.http.ua.UserAgentImpl;
import com.about.hippodrome.url.ValidDomainUrlDataFactory;

public class MantleAccessLogInjectorTest {
	private RequestContext requestContext = mock(RequestContext.class);
	private HttpServletResponse response = mock(HttpServletResponse.class);
	private final MantleAccessLogInjector accessLogInjector = new MantleAccessLogInjector();

	@Before
	public void before() {
		MDC.clear();
	}

	@Test
	public void writeTest() throws Exception {

		when(requestContext.getUrlData()).thenReturn(new ValidDomainUrlDataFactory(APPLICATION_NAME)
				.create("https://www.verywellhealth.com/doc-123"));
		when(requestContext.getUserAgent()).thenReturn(new UserAgentImpl(DeviceCategory.PERSONAL_COMPUTER));
		when(requestContext.getRequestId()).thenReturn("requestId");

		accessLogInjector.write(requestContext, response);

		Assert.assertEquals("requestId", MDC.get("IDSTAMP"));
		Assert.assertEquals("desktop", MDC.get("deviceType"));
		Assert.assertEquals("/doc-123", MDC.get("PATH"));
	}

}