package com.about.mantle.commons;

import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.utils.MntlConfigurationProperties;
import com.about.venus.core.driver.proxy.VenusHarResponse;
import com.about.venus.core.driver.proxy.VenusProxy;
import com.about.venus.core.utils.HTMLUtils;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;

import java.util.HashMap;

import static com.about.venus.core.useragent.UserAgent.DESKTOP;
import static org.hamcrest.CoreMatchers.containsString;

public abstract class MntlCacheTest extends MntlVenusTest {

	protected abstract HashMap<String, Integer> urlCacheMap();

	protected String noTransformTestUrl() {
		return null;
	}

	protected WebResponse getResponse(String url) throws Exception {
		WebClient client = new WebClient();
		client.getOptions().setUseInsecureSSL(true);
		Page htmlPage = HTMLUtils.page(url, DESKTOP.userAgent(), client);
		WebResponse response = htmlPage.getWebResponse();
		return response;
	}
	protected void validateCacheHeader(VenusProxy proxy, String filter) {
		VenusHarResponse responseForResource = proxy.capture().page("noTransform").entries(filter).stream()
				.filter(item -> item.request().url().url().contains(MntlConfigurationProperties.PROJECT_BASE_URL))
				.findFirst().get().getResponse();
		String cacheValueJs = responseForResource.response().getHeaders().stream()
				.filter(item -> item.getName().equals("Cache-Control")).findFirst().get().getValue();
		collector.checkThat("the cache control does not contain no-transform in response headers for filter " + filter
				+ " for URL " + noTransformTestUrl(), cacheValueJs, containsString("no-transform"));
	}
}
