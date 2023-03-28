package com.about.mantle.seo;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.utils.MntlUrl;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.about.venus.core.utils.ConfigurationProperties;
import com.about.venus.core.utils.HTMLUtils;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.codehaus.plexus.util.StringUtils;
import org.jsoup.Jsoup;
import org.junit.Assert;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.about.venus.core.useragent.UserAgent.DESKTOP;
import static com.about.venus.core.useragent.UserAgent.MOBILE;
import static com.about.venus.core.useragent.UserAgent.NEXUS7;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

public class MntlMetaTagsTest extends MntlVenusTest implements MntlCommonTestMethods {

    protected final Map<String, String> desktopHeaders = ImmutableMap.of("country_code_iso2", "US", "country_code",
            "US", "user-agent", DESKTOP.userAgent());

    protected final Map<String, String> mobileHeaders = ImmutableMap.of("country_code_iso2", "US", "country_code", "US",
            "user-agent", MOBILE.userAgent());

    protected final Map<String, String> tabletHeaders = ImmutableMap.of("country_code_iso2", "US", "country_code", "US",
            "user-agent", NEXUS7.userAgent());

    private final WebClient client = webClient();

    private final static String seleneAws = ConfigurationProperties.getTargetSelene("") + "/verticalconfig/";
    protected String FILTER = "/filters:no_upscale():max_bytes(150000):strip_icc()";

    protected void metaTagsTest(String mntlUrl, Map<String, String> expectedTags, Map<String, String> verticalSpecificData, String url) {
        testMetaTags(HTMLUtils.metaTagsFromDocument(page(mntlUrl, desktopHeaders)), expectedTags, verticalSpecificData, url);
        testMetaTags(HTMLUtils.metaTagsFromDocument(page(mntlUrl, tabletHeaders)), expectedTags, verticalSpecificData, url);
        testMetaTags(HTMLUtils.metaTagsFromDocument(page(mntlUrl, mobileHeaders)), expectedTags, verticalSpecificData, url);
    }

    protected HtmlPage page(String url, Map<String, String> headers) {
        try {
            return (HtmlPage) HTMLUtils.page(new MntlUrl(url).url(), headers, client);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    This test works as a common test for meta tags for all verticals. To accommodate differences in verticals we
    use two maps:
        - verticalSpecificData map will include data specific to a vertical, as twitter site name or facebook id
        - expectedTags map will include the tags we would be validating against per template type
    At the moment a need to exclude certain validations in certain templates is implemented using if/else condition,
    though a better way can be proposed and implemented in the future ticket.
     */
    protected void testMetaTags(Map<String, String> tags, Map<String, String> expectedTags, Map<String, String> verticalSpecificData, String url) {
        String ogType = "article";
        if (expectedTags.containsKey("ogType")) ogType = expectedTags.get("ogType");
        String mntlUrl = new MntlUrl(url, null, null, true).envFreeUrl();
        String templateUsed = expectedTags.get("templateType");
        if ("SEARCH_PAGE".equals(templateUsed)) {
            String searchUrl = mntlUrl.contains("&") ? mntlUrl.replace("&globeNoTest=true", "") : mntlUrl.replace("?globeNoTest=true", "");
            collector.checkThat("og:url is not " + url, tags.get("og:url"),
                    anyOf(containsString(searchUrl), endsWith("search")));
        } else {
            collector.checkThat("og:url is not " + url, tags.get("og:url"), is(new MntlUrl(mntlUrl).queryFreeUrl()));
        }
        String[] robotsMeta = StringUtils.stripAll(tags.get("robots").split(","));
        String robotsMetaList = Arrays.asList(robotsMeta).stream().map(value -> value.toUpperCase())
                .collect(Collectors.joining(","));
        collector.checkThat("robots meta tags are not being set", robotsMetaList.length(), is(not(0)));
        collector.checkThat("robots values do not match the expected values", expectedTags.get("robotsTags"), is(robotsMetaList));
        collector.checkThat("viewport is not being set", tags.get("viewport"), not(isEmptyOrNullString()));
        collector.checkThat("article:section is not " + verticalSpecificData.get("articleSection"), tags.get("article:section"), is(verticalSpecificData.get("articleSection")));
        collector.checkThat("fb:app_id is not " + verticalSpecificData.get("fbAppID"), tags.get("fb:app_id"), is(verticalSpecificData.get("fbAppID")));
        collector.checkThat("og:type is not correct", tags.get("og:type"), is(ogType));
        collector.checkThat("og:site_name is not " + verticalSpecificData.get("siteName"), tags.get("og:site_name"), is(verticalSpecificData.get("siteName")));
        if (tags.containsKey("twitter:card")) {
            collector.checkThat("twitter:card is not summary", tags.get("twitter:card"), is(verticalSpecificData.get("twitterCard")));
            collector.checkThat("twitter:site is not " + verticalSpecificData.get("twitterSite"), tags.get("twitter:site"), is(verticalSpecificData.get("twitterSite")));
        }
        if (tags.containsKey("parsely-section")) {
            collector.checkThat("parsely-section is not present.", tags.get("parsely-section"), not(emptyOrNullString()));
            collector.checkThat("parsely-tags is not present.", tags.get("parsely-tags"), not(emptyOrNullString()));
        }
        if (!"SEARCH_PAGE".equals(templateUsed) && !"SEARCH_PAGE_2".equals(templateUsed)
                && !"ABOUT_US_PAGE".equals(templateUsed)) {
            if (!"HOMEPAGE".equals(templateUsed)) {
                if (tags.containsKey("og:image")) {
                    collector.checkThat("og:image does not have expected filter " + FILTER,
                            tags.get("og:image").contains(verticalSpecificData.containsKey("ogImage")
                                    ? verticalSpecificData.get("ogImage") : FILTER), is(true));
                    if (expectedTags.containsKey("og:image"))
                        collector.checkThat("og:image does not match", tags.get("og:image"),
                                endsWith(expectedTags.get("og:image")));
                }
                if (tags.containsKey("twitter:image")) {
                    collector.checkThat("twitter:image does not have expected filter " + FILTER,
                            tags.get("twitter:image").contains(verticalSpecificData.containsKey("twitterImage")
                                    ? verticalSpecificData.get("twitterImage") : FILTER), is(true));
                    if (expectedTags.containsKey("twitter:image"))
                        collector.checkThat("twitter:image does not match", tags.get("twitter:image"),
                                endsWith(expectedTags.get("twitter:image")));
                }
                if (!"TAX1_1".equals(templateUsed) && !"TAX2_1".equals(templateUsed) && !"TAX2_2".equals(templateUsed)
                        && !"TAX3_1".equals(templateUsed) && !"TAX4_1".equals(templateUsed)) {
                    collector.checkThat("sailthru.tags is not being set", tags.get("sailthru.tags"),
                            is(expectedTags.get("sailthru.tags")));
                    collector.checkThat("emailcontenttype is not being set", tags.get("emailcontenttype"),
                            is(expectedTags.containsKey("emailcontenttype") ? expectedTags.get("emailcontenttype") : null));
                    collector.checkThat("emailvertical is not being set", tags.get("emailvertical"),
                            is(expectedTags.containsKey("EMAIL_VERTICAL") ? expectedTags.get("EMAIL_VERTICAL") : verticalSpecificData.get("EMAIL_VERTICAL")));
                    collector.checkThat("sailthru.author is not being set", tags.get("sailthru.author"), is(expectedTags.get("sailthru.author")));
                }
                if (expectedTags.containsKey("articleAuthor")) {
                    collector.checkThat("article:author is not correct", tags.get("article:author"),
                            is(expectedTags.containsKey("articleAuthor") ? expectedTags.get("articleAuthor") : null));
                } else {
                    collector.checkThat("article:author is not correct", tags.get("article:author"),
                            is(verticalSpecificData.containsKey("articleAuthor") ? verticalSpecificData.get("articleAuthor") : null));
                }
            }
            String docId;
            if (expectedTags.containsKey("docId")) docId = expectedTags.get("docId");
            else docId = mntlUrl.substring(mntlUrl.lastIndexOf("-") + 1, mntlUrl.indexOf("?", mntlUrl.lastIndexOf("-") + 1));
            collector.checkThat("description is not being set", tags.get("description"), not(emptyOrNullString()));
            collector.checkThat("og:title does not contain expected value", tags.get("og:title"),
                    containsStringIgnoringCase(expectedTags.containsKey("ogTitle") ? expectedTags.get("ogTitle") : getExpectedMetaTitleValue(docId, mntlUrl, "og:title")));
            collector.checkThat("og:description is not being set", tags.get("og:description"),
                    not(emptyOrNullString()));
            collector.checkThat("twitter:title does not contain expected value", tags.get("twitter:title"),
                    containsStringIgnoringCase(expectedTags.containsKey("twitterTitle") ? expectedTags.get("twitterTitle") : getExpectedMetaTitleValue(docId, mntlUrl, "twitter:title")));
            collector.checkThat("itemprop name does not contain expected value", tags.get("name"),
                    containsStringIgnoringCase(expectedTags.containsKey("itemPropName") ? expectedTags.get("itemPropName") : getExpectedMetaTitleValue(docId, mntlUrl, "name")));
        }
    }

    /**
     * Read the FacebookAppId from selene
     *
     * @param slug is Vertical name(Eg: "TRAVEL")
     * @return String facebookAppId
     */
    public static String readFacebookAppIdFromSeleneData(String slug) {
        String endPoint = seleneAws + slug;
        HttpResponse<String> docResponse = null;
        try {
            docResponse = Unirest.get(endPoint).asString();
        } catch (UnirestException e) {
            Assert.fail("Error in reading data: " + slug);
        }
        DocumentContext responseJson = JsonPath.parse(docResponse.getBody());
        if (docResponse.getStatus() != Response.Status.OK.getStatusCode() && docResponse.getStatus() != Response.Status.BAD_REQUEST.getStatusCode()) {
            Assert.fail("Error in reading selene data.");
        }
        String facebookAppId = responseJson.read("$.data.facebookAppId");
        return facebookAppId;
    }

    /**
     * The method will get expected meta title value. For meta itemprop="name" it should be document heading, if empty then document title, if empty then URL title.
     * For meta property="og:title" and meta name="twitter:title" it should be socialTitle, if no socialTitle then document heading, if empty then document title, if empty then URL title.
     *
     * @param docId - document id
     * @param url - page url
     * return expected meta title value
     * */
    private static String getExpectedMetaTitleValue(String docId, String url, String field) {
        String expectedValue;
        DocumentContext documentData = SeleneUtils.getDocuments(docId);

        if (field.equals("og:title") || field.equals("twitter:title")) {
            try {
                expectedValue = documentData.read("$.data.socialTitle").toString();
                return Jsoup.parse(expectedValue).text();
            } catch (Exception ignored) {
            }
        }

        try {
            expectedValue = documentData.read("$.data.heading").toString();
            return Jsoup.parse(expectedValue).text();
        } catch (Exception ignored) {
        }

        try {
            expectedValue = documentData.read("$.data.title").toString();
            return Jsoup.parse(expectedValue).text();
        } catch (Exception ignored) {
        }

        // if the document doesn't have socialTitle/heading/title then expected value will be the URL title (basically taking the slug and replacing any -'s in it with a space)
        expectedValue = url.substring(url.lastIndexOf("/") + 1) // getting the url slug
                .replace("?globeNoTest=true", "")           // removing '?globeNoTest=true'
                .replaceAll("[*0-9]", "")                   // removing doc id if the slug has it
                .replaceAll("-", " ");                      // replacing any '-' with a space

        return Jsoup.parse(expectedValue).text();
    }
}