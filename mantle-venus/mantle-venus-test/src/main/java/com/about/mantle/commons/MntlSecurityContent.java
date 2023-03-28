package com.about.mantle.commons;

import com.about.mantle.utils.test.MntlVenusTest;
import com.about.venus.core.utils.HTMLUtils;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;

import java.util.HashMap;

import static com.about.venus.core.useragent.UserAgent.DESKTOP;

public abstract class MntlSecurityContent extends MntlVenusTest {

	protected abstract HashMap<String, String> urlContentMap();

	protected WebResponse getResponse(String url) throws Exception {
		WebClient client = new WebClient();
		client.getOptions().setUseInsecureSSL(true);
		Page htmlPage = HTMLUtils.page(url, DESKTOP.userAgent(), client);
		WebResponse response = htmlPage.getWebResponse();
		return response;
	}
}
