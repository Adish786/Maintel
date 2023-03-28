package com.about.mantle.sitemap;

import com.about.mantle.seo.MntlMetaTagsTest;
import com.about.mantle.venus.model.MntlPage;
import com.about.mantle.venus.model.schema.SchemaComponent;
import com.about.mantle.venus.utils.MntlUrl;
import com.about.venus.core.utils.XMLUtils;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import io.restassured.path.json.JsonPath;
import org.apache.xml.utils.PrefixResolver;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

public class MntlSitemapTest extends MntlMetaTagsTest {

    protected static final Logger logger = LoggerFactory.getLogger("MntlGtmTest");

    /*
    This test will render a sitemap xml, then validate every sitemap listed in that xml,
    with common validations on root element, then will take a random `url` item and
    validate `lastmod` attribute against a corresponding value from schema in the page.
     */
    protected Consumer<Runner> testSitemap = (runner) -> {
        XmlPage page = getSitemapPage(runner.url());
        siteMapCommonValidations(page, "sitemap.xml", "sitemapindex");

        List<String> sitemaps = page.getByXPath("/xsd:sitemapindex/xsd:sitemap/xsd:loc", sitemapPrefixResolver)
                .stream()
                .map(loc -> {
                    return ((DomNode) loc).getTextContent();
                })
                .collect(Collectors.toList());
        collector.checkThat("no sitemaps in index", sitemaps.size(), greaterThan(0));

        for (String sitemap : sitemaps) {
            page = getSitemapPage(sitemap);
            siteMapCommonValidations(page, sitemap, "urlset");
            // Getting nodes of `url` in a list and validating 2 random nodes against a value of `dateModified` in the corresponding schemas
            NodeList urlNodeList = page.getElementsByTagName("url");
            collector.checkThat("There are no `url` nodes in " + sitemap + " sitemap", urlNodeList.getLength(), greaterThan(0));
            modifiedDateValidations(runner, urlNodeList);
        }
    };

    /*
    This test will execute all the validations from `testSitemap` for `google-news-sitemap.xml`
     */
    protected Consumer<Runner> testGoogleSitemap = (runner) -> {
        XmlPage page = getSitemapPage(runner.url());
        siteMapCommonValidations(page, "google-news-sitemap.xml", "urlset");
        NodeList urlNodeList = page.getElementsByTagName("url");
        collector.checkThat("There are no `url` nodes in " + "google-news-sitemap.xml" + " sitemap", urlNodeList.getLength(), greaterThan(0));
        modifiedDateValidations(runner, urlNodeList);

    };

    private XmlPage getSitemapPage(String url) {
        XmlPage page = null;
        try {
            page = XMLUtils.xmlPage(new MntlUrl(url).url(), true);
        } catch (NullPointerException npe) {
            logger.info("Sitemap " + url + " is empty");
        } catch (Exception e) {}
        return page;
    }

    /*
    This method will validate `lastmod` value in a set number of `url` items randomly selected from the list of all items in a sitemap.
    */
    private void modifiedDateValidations(Runner runner, NodeList urlNodeList) {
        // Just in case sitemap contains an urls that may not be accessible we will skip on those urls up to 10 times
        for (int numberOfTries = Math.min(urlNodeList.getLength(), 10); numberOfTries > 0; numberOfTries--) {
            Random random = new Random();
            Node node = urlNodeList.item(random.nextInt((urlNodeList.getLength() - 1)));
            Element urlElement = (Element) node;
            String[] urlElementAttributes = urlElement.getTextContent().split(" ");
            String url = runner.url().substring(0, runner.url().lastIndexOf("/")) + urlElementAttributes[1].substring(urlElementAttributes[1].lastIndexOf("/"));
            String lastmodValue = urlElementAttributes[2];
            runner.driver().get(url);
            MntlPage urlItemPage = runner.page();
            urlItemPage.waitFor().exactMoment(500, TimeUnit.MILLISECONDS);
            SchemaComponent schemaComp;
            try {
                schemaComp = urlItemPage.head().findElement(By.cssSelector("script[type=\"application/ld+json\"]"), SchemaComponent::new);
                JsonPath schema = new JsonPath(schemaComp.schemaContent());
                String dataInSchema = schema.get("dateModified").toString();
                collector.checkThat("Date in sitemap url item " + url + " doesn't match date in schema", dataInSchema.substring(1, dataInSchema.indexOf("T")), is(lastmodValue.substring(0, lastmodValue.indexOf("T"))));
                break;
            } catch (Exception e) { }
        }
    }

    private void siteMapCommonValidations(XmlPage page, String sitemap, String nodeName) {
        collector.checkThat("missing root element in sitemap page " + sitemap, page.getDocumentElement(), notNullValue());
        collector.checkThat(nodeName + " is not the root element in sitemap page " + sitemap, page.getDocumentElement().getNodeName(), is(nodeName));
    }

    private static final PrefixResolver sitemapPrefixResolver = new PrefixResolver() {

        private static final String ns = "http://www.sitemaps.org/schemas/sitemap/0.9";

        @Override
        public boolean handlesNullPrefixes() {
            return false;
        }

        @Override
        public String getNamespaceForPrefix(String prefix, Node context) {
            return ns;
        }

        @Override
        public String getNamespaceForPrefix(String prefix) {
            return ns;
        }

        @Override
        public String getBaseIdentifier() {
            return "";
        }
    };

}
