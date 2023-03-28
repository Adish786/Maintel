package com.about.mantle.components.global;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.utils.MntlUrl;
import com.about.venus.core.useragent.UserAgent;
import com.about.venus.core.utils.HTMLUtils;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.hamcrest.Matchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.about.venus.core.useragent.UserAgent.DESKTOP;
import static com.about.venus.core.useragent.UserAgent.IPAD;
import static com.about.venus.core.useragent.UserAgent.MOBILE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Note: the favicon meta data is created <a href="https://bitbucket.prod.aws.about.com/projects/FRON/repos/mantle/browse/mantle-resources/src/main/resources/components/head-tags/favicons/view/favicons.ftl">here</a>
 * @author ksaifullah
 *
 */
public abstract class MntlFaviconTest extends MntlVenusTest {
	protected List<UserAgent> deviceList;
	
	/**
	 * Copy and paste from <a href="https://bitbucket.prod.aws.about.com/projects/FRON/repos/mantle/browse/mantle-resources/src/main/resources/components/head-tags/favicons/view/favicons.ftl">here</a> or from vertical if overriden
	 */
	protected List<String> expectedHeaderEntries;
	
	protected final Consumer<HtmlPage> testFavicon = (page) -> {
		final DomElement resourceElement = page.getElementsByTagName("html").get(0);
		final String resourceVersion = resourceElement.getAttribute("data-resource-version");

		final DomElement head = page.getElementsByTagName("head").get(0);

		//replace ${model.path} with actual value using deployed version
		int size = expectedHeaderEntries.size();
		for(int i=0; i < size; ++i) {
			expectedHeaderEntries.set(i, expectedHeaderEntries.get(i).replaceAll("\\$\\{model\\.path\\}", "/static/" + resourceVersion + "/icons/favicons"));
		}
		expectedHeaderEntries.forEach(entry -> collector.checkThat("favicon path location is incorrect", head.asXml(), containsString(entry)));
	};

	protected final Consumer<MntlCommonTestMethods.MntlRunner> testPageFavicon = (runner) -> {
		String resourceVersion = runner.driver().executeScript("return document.querySelector(\"html\").getAttribute(\"data-resource-version\")");
		String htmlHead = runner.driver().executeScript("return document.head.outerHTML");

		collector.checkThat("expectedHeaderEntries list size is 0", expectedHeaderEntries.size(), greaterThan(0));

		for(int i = 0; i < expectedHeaderEntries.size(); ++i) {
			this.expectedHeaderEntries.set(i, ((String)this.expectedHeaderEntries.get(i)).replaceAll("\\$\\{model\\.path\\}", "/static/" + resourceVersion + "/icons/favicons"));
			this.expectedHeaderEntries.set(i, ((String)this.expectedHeaderEntries.get(i)).replaceAll("/>$", ">"));
		}

		this.expectedHeaderEntries.forEach((entry) -> {
			this.collector.checkThat("favicon path location is incorrect", htmlHead, containsString(entry));
		});
	};
	
	protected final BiFunction<String, String, HtmlPage> htmlPage = (url, userAgent) -> {
		String targetUrl = new MntlUrl(url, true).url();
		HtmlPage page;
		try {
			page = HTMLUtils.htmlPage(targetUrl, userAgent);
		} catch (Exception e) {
			throw new RuntimeException("Error opening page for url " + url + " and useragent " + userAgent, e);
		}
		return page;
	};
	
	protected final Consumer<String> faviconTest = (url) -> {
		deviceList.forEach(userAgent -> {
			final HtmlPage page = htmlPage.apply(url,  userAgent.userAgent());
			testFavicon.accept(page);
		});
		
	};
	
	public MntlFaviconTest() {
		//add device to test
		deviceList = new ArrayList<>();
		Collections.addAll(deviceList, DESKTOP, MOBILE, IPAD);
		
		//add expected entries
		expectedHeaderEntries = new ArrayList<>();
		expectedHeaderEntries.add("<link rel=\"shortcut icon\" href=\"/favicon.ico\">");
		expectedHeaderEntries.add("<link rel=\"icon\" type=\"image/x-icon\" href=\"/favicon.ico\">");
		expectedHeaderEntries.add("<link rel=\"apple-touch-icon-precomposed\" sizes=\"57x57\" href=\"/apple-touch-icon-57x57.png\">");
		expectedHeaderEntries.add("<link rel=\"apple-touch-icon-precomposed\" sizes=\"76x76\" href=\"/apple-touch-icon-76x76.png\">");
		expectedHeaderEntries.add("<link rel=\"apple-touch-icon-precomposed\" sizes=\"120x120\" href=\"/apple-touch-icon-120x120.png\">");
		expectedHeaderEntries.add("<link rel=\"apple-touch-icon-precomposed\" sizes=\"152x152\" href=\"/apple-touch-icon-152x152.png\">");
		expectedHeaderEntries.add("<link rel=\"apple-touch-icon-precomposed\" sizes=\"60x60\" href=\"/apple-touch-icon-60x60.png\"/>");
		expectedHeaderEntries.add("<link rel=\"apple-touch-icon-precomposed\" sizes=\"180x180\" href=\"/apple-touch-icon-180x180.png\"/>");
		expectedHeaderEntries.add("<meta name=\"msapplication-TileColor\" content=\"#F4F4F4\"/>"); 
		expectedHeaderEntries.add("<meta name=\"msapplication-TileImage\" content=\"${model.path}/mstile-144x144.png\">"); 
		expectedHeaderEntries.add("<meta name=\"msapplication-square70x70logo\" content=\"${model.path}/mstile-70x70.png\">"); 
		expectedHeaderEntries.add("<meta name=\"msapplication-square150x150logo\" content=\"${model.path}/mstile-150x150.png\">"); 
		expectedHeaderEntries.add("<meta name=\"msapplication-square310x310logo\" content=\"${model.path}/mstile-310x310.png\">"); 
		expectedHeaderEntries.add("<meta name=\"msapplication-wide310x150logo\" content=\"${model.path}/mstile-310x150.png\">");
		
		//convert ending > tag to /> because page source tags ends with /> 
		int size = expectedHeaderEntries.size();
		for(int i=0; i < size; ++i) {
			expectedHeaderEntries.set(i, expectedHeaderEntries.get(i).replaceAll("(?m)([^/])>$", "$1/>"));
		} 
	}
}
