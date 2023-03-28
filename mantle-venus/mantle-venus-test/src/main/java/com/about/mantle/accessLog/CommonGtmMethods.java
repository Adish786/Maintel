package com.about.mantle.accessLog;

import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.utils.MntlUrl;
import com.about.venus.core.useragent.UserAgent;
import com.about.venus.core.utils.HTMLUtils;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.about.venus.core.useragent.UserAgent.DESKTOP;
import static com.about.venus.core.useragent.UserAgent.IPAD;
import static com.about.venus.core.useragent.UserAgent.MOBILE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;

/**
 * Test for checking the GTM values being used are correct for the given environment. This can be seen by manually
 * looking at the page source for a given url and viewing iframes or scripts from googletagmanager
 *
 * Example usage:
 *
 * public class GtmTest extends CommonGtmMethods {
 *     static {
 *         QA_GTM_ID = "GTM-KQ2QZNV";
 *         GTM_ID = "GTM-5TR9JND";
 *     }
 *
 *     @Test
 *     @TestDescription(device = {Any}, page = AboutUsPage.class, desc = "verify gtm value on about us page")
 *     @Category({Analytics.class})
 *     public void AboutUsTest() {
 *         gtmTest.accept(ABOUTUS.data().url());
 *     }
 *
 *     @Test
 *     @TestDescription(device = {Any}, desc = "verify gtm values appear on a list of urls")
 *     @Category({Analytics.class})
 *     public void TestAllUrlsAtOnce() {
 *          ArrayList<String> items = new ArrayList<>();
 *          items.add(VIDEO.data().url());
 *          items.add(TERMS_SC.data().url());
 *          gtmTestUrls.accept(items);
 *     }
 * }
 */
public class CommonGtmMethods extends MntlVenusTest {
   
    protected static final Logger logger = LoggerFactory.getLogger("MntlGtmTest");
    protected List<UserAgent> deviceList = Arrays.asList(new UserAgent[]{DESKTOP, MOBILE, IPAD});
    protected static String QA_GTM_ID;
    protected static String GTM_ID;
    @Before
    public void setup() {
        LoggerFactory.getLogger("MntlGtmTest");
        if (QA_GTM_ID == null || QA_GTM_ID.isEmpty()) { Assert.fail("QA_GTM_ID needs to be initialized"); }
        if (GTM_ID == null || GTM_ID.isEmpty()) { Assert.fail("GTM_ID needs to be initialized"); }
    }

    protected String expectedId() {
        return isProd() ? GTM_ID : QA_GTM_ID;
    }
    /**
     * gtm id noscript tag assertion
     *
     * @param HtmlPage
     * page
     */
    protected final Consumer<HtmlPage> testNoScript = (page) -> {
        logger.info(String.format("testNoScript - checking for: //www.googletagmanager.com/ns.html?id=%s", expectedId()));
        final DomElement gtmElement = page.getElementsByTagName("iframe").stream()
            .filter(iframe -> iframe.getAttribute("src").contains("googletagmanager")).findFirst().orElse(null);
        assertThat(gtmElement.getAttribute("src"), is("//www.googletagmanager.com/ns.html?id=" + expectedId()));
    };

    /**
     * gtm id script tag assertion
     *
     * @param HtmlPage
     * page
     */
    protected final Consumer<HtmlPage> testScript = (page) -> {
        final DomElement gtmElement = page.getElementsByTagName("script").stream()
            .filter(script -> script.getTextContent().contains("googletagmanager")).findFirst().orElse(null);
        ArrayList<String> gtmLoadStrings = new ArrayList<>();
        gtmLoadStrings.add("Mntl.utilities.onLoad(function() {");
        gtmLoadStrings.add("(window,document,'script','dataLayer','" + expectedId() + "');");
        gtmLoadStrings.add("var dataLayer = dataLayer || [];");
        logger.info(String.format("testScript - checking for: (window,document,'script','dataLayer','%s')", expectedId()));
        assertThat(gtmElement.getTextContent(), stringContainsInOrder(gtmLoadStrings));
    };

    /**
     * returns an htmlpage for a url
     *
     * @param String
     * url
     * @param String
     * useragent
     * @return htmlpage
     */
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

    /**
     * entry point for gtm tests
     *
     * @param String
     * url
     * @param String
     * useragent
     */
    protected final Consumer<String> gtmTest = (url) -> {
        for (UserAgent device : deviceList) {
            logger.info(String.format("Check device: %s", device.toString()));
            final HtmlPage page = htmlPage.apply(url, device.userAgent());
            testNoScript.accept(page);
            testScript.accept(page);
        }
    };

    /**
     * entry point for gtm tests when testing multiple urls at once
     *
     * @param ArrayList<String>
     * url
    **/
    protected final Consumer<ArrayList<String>> gtmTestUrls = (urls) -> {
        for(String url : urls) {
            gtmTest.accept(url);
        }
    };
}
