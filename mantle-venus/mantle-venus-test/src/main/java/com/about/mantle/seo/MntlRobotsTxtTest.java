package com.about.mantle.seo;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.venus.core.utils.HTMLUtils;
import com.about.venus.core.utils.Url;
import com.gargoylesoftware.htmlunit.TextPage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.nio.charset.StandardCharsets;

import static com.about.venus.core.useragent.UserAgent.DESKTOP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public abstract class MntlRobotsTxtTest extends MntlVenusTest implements MntlCommonTestMethods {

	protected static String ROBOTS_TXT_EXPECTED = "/mntl-robots.txt";

	protected abstract String domain();
	protected abstract boolean googleNewsSiteMap();

	protected abstract String url();

	public void testRobotsTxt() throws Exception {
		String robotsTxtExpected = StringEscapeUtils.escapeJava(IOUtils
				.toString(MntlRobotsTxtTest.class.getResourceAsStream(ROBOTS_TXT_EXPECTED), StandardCharsets.UTF_8)
				.replace("<DOMAIN>", domain()));

		TextPage page = (TextPage) HTMLUtils.page(new Url(url(), true).url(), DESKTOP.userAgent(), webClient());
		String robotsTxt = StringEscapeUtils.escapeJava(page.getContent());
		
		// flag should be set true for verticals using google news sitemap
		if(googleNewsSiteMap() == true) {
			robotsTxtExpected = robotsTxtExpected  + "\\n" + "Sitemap: https://www." +domain() + ".com/google-news-sitemap.xml" + "\\n" ;
		}
		assertThat("Robots.txt content not matching expected robots.txt", robotsTxt, is(robotsTxtExpected));

		
	}
}
