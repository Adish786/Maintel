package com.about.mantle.model.tasks;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.definition.action.NewsletterSignupService;
import com.about.mantle.model.services.NewsletterValidationService;
import com.sailthru.client.SailthruClient;

public class NewsletterTaskTest {
	private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
	private RequestContext requestContext = Mockito.mock(RequestContext.class);
	private SailthruClient sailthruClient = Mockito.mock(SailthruClient.class);
	private NewsletterValidationService newsletterValidationService = Mockito.mock(NewsletterValidationService.class);
	
	private StringWriter responseContent = new StringWriter();
	
	public NewsletterTaskTest() throws IOException {
		Mockito.when(response.getWriter()).thenReturn(new PrintWriter(responseContent));
	}
	
	@Before
	public void resetResponseContent() {
		this.responseContent.getBuffer().setLength(0);
	}

	// we can't turn on this test because we don't have a non-production service we can hit currently
	@Ignore
	@Test
	public void test() throws IOException, URISyntaxException {
		Map<String, String[]> params = new HashMap<>();
		params.put("email", ArrayUtils.toArray("foo@QWERTYUIOPASDFGHJKLZXCVBNM.com"));
		Mockito.when(requestContext.getParameters()).thenReturn(params);
		Mockito.when(requestContext.getRequestUrl()).thenReturn("http://neurology.about.com/od/cat/a/article.htm");
		NewsletterSignupService newsletterSignupService = new NewsletterSignupService(sailthruClient, newsletterValidationService);
		Assert.assertTrue(newsletterSignupService.signupNewsletter(null, null, requestContext));
	}
}
