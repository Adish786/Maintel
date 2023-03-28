package com.about.mantle.rss;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.selene.client.common.RestAssuredClient;
import com.about.mantle.utils.selene.version.Version;
import com.about.mantle.utils.test.MntlVenusTest;
import org.junit.Assert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsNull.nullValue;

@SuppressWarnings("rawtypes")
public class MntlCommonRssFeedsTests extends MntlVenusTest implements MntlCommonTestMethods {
    /**
     * Used to hold expected data of RSS feed data to verify against
     */
    public static class TestData {
        private Boolean expectContent = false;
        private Boolean hasCopyRight = true;
        private Boolean hasImage = true;
        private Boolean hasAmazon = false;
        private Boolean isSmartNews = false;
        private Boolean isPinterest = false;
        private Boolean isYahoo = false;
        private List<String> listOfSponsoredArticles;

        public TestData() { }
        public TestData expectContent(Boolean expectContent) {
            this.expectContent = expectContent;
            return this;
        }
        public TestData hasCopyRight(Boolean hasCopyRight) {
            this.hasCopyRight = hasCopyRight;
            return this;
        }
        public TestData hasImage(Boolean hasImage) {
            this.hasImage = hasImage;
            return this;
        }
        public TestData hasAmazon(Boolean hasAmazon) {
            this.hasAmazon = hasAmazon;
            return this;
        }
        public TestData isSmartNews(Boolean isSmartNews) {
            this.isSmartNews = isSmartNews;
            return this;
        }
        public TestData isYahoo(Boolean isYahoo) {
            this.isYahoo = isYahoo;
            return this;
        }
        public TestData listOfSponsoredArticles(List<String> listOfSponsoredArticles) {
            this.listOfSponsoredArticles = listOfSponsoredArticles;
            return this;
        }
        public TestData isPinterest(Boolean isSmartNews) {
            this.isPinterest = isSmartNews;
            return this;
        }
    }

    /**
     * Test the RSS feed based on TestData
     * @return
     */
    protected Consumer<Runner> testRssFeed() {
        return (runner) -> {
            try {
                TestData data = (TestData)runner.testData();
                String rssData = runner.page().body().text().replace("&", "&amp;");
                rssData = rssData.substring(rssData.indexOf("<rss"));

                InputStream stream = new ByteArrayInputStream(rssData.getBytes(StandardCharsets.UTF_8));
                JAXBContext jaxbContext = JAXBContext.newInstance(RSS.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                RSS rss = (RSS) jaxbUnmarshaller.unmarshal(stream);

                String url = runner.url();
                if (!url.contains("feedbuilder") && !url.contains("amazon")) {
                    int indexOfFeed = url.indexOf("/feeds");
                    String homepageUrl = runner.url().substring(0, indexOfFeed);
                    collector.checkThat("The link is not pointing to the homepage", rss.getChannel().getLink(), is(homepageUrl));
                }
                if(data.isSmartNews) {
                    collector.checkThat("Smart news feed's `snf:logo` url is empty", rss.getChannel().getSnFLogo().getUrl(), not(emptyOrNullString()));
                    collector.checkThat("Smart news feed's `snf:darkModeLogo` url is empty", rss.getChannel().getDarkModeLogo().getUrl(), not(emptyOrNullString()));
                    String rssMediaUrl = rssData.replace("<rss xmlns:media=\"", "").substring(0, rssData.replace("<rss xmlns:media=\"", "").indexOf("\""));
                    collector.checkThat("Smart news media url does not equal to http://search.yahoo.com/mrss/", rssMediaUrl, is("http://search.yahoo.com/mrss/"));
                }
                collector.checkThat("Missing title in RSS feed", rss.getChannel().getTitle(), notNullValue());
                if (data.hasCopyRight == true)
                    collector.checkThat("Missing copyright in RSS feed", rss.getChannel().getCopyright(), notNullValue());
                collector.checkThat("Missing description in RSS feed", rss.getChannel().getDescription(), notNullValue());
                collector.checkThat("Missing language in RSS feed", rss.getChannel().getLanguage(), notNullValue());
                if (data.hasImage == true) {
                    Image image = rss.getChannel().getImage();
                    collector.checkThat("Missing image in RSS feed", image, notNullValue());
                    collector.checkThat("Missing image title in RSS feed", image.getTitle(), notNullValue());
                    collector.checkThat("Missing image url in RSS feed", image.getUrl(), notNullValue());
                    collector.checkThat("Missing image link in RSS feed", image.getLink(), notNullValue());
                    collector.checkThat("Missing image width in RSS feed", image.getWidth(), notNullValue());
                    collector.checkThat("Missing image height in RSS feed", image.getHeight(), notNullValue());
                }

                // Items may be 0 to many - if one exists check for required fields
                int enclosureCount = 0;
                for (Item item : rss.getChannel().getItems()) {
                    if ((data.isYahoo || data.isSmartNews) && data.listOfSponsoredArticles != null) {
                        for (String sponsoredArticleTitle : data.listOfSponsoredArticles) {
                            collector.checkThat("Sponsored article with the title " + sponsoredArticleTitle + " shows up in the feed", item.getGuid(), not(containsStringIgnoringCase(sponsoredArticleTitle)));
                        }
                    }
                    collector.checkThat("Missing item title in RSS feed", item.getTitle(), notNullValue());
                    collector.checkThat("Missing item link in RSS feed", item.getLink(), notNullValue());
                    if(data.hasAmazon != true) {
                        collector.checkThat("Missing item description in RSS feed", item.getDescription(), notNullValue());
                        if(data.isPinterest != true)
                            collector.checkThat("Missing item guid in RSS feed", item.getGuid(), notNullValue());
                    }
                    if(data.isSmartNews) {
                        collector.checkThat("The item contains <enclosure url=“{imageURL}” /> element", item.getEnclosure(), nullValue());
                        collector.checkThat("The item does not contain <media:thumbnail url=“{imageURL}” /> element", item.getThumbnail().getUrl(), startsWith("https://"));
                    }
                    if(data.expectContent) { // Should be yahoo specific
                        if(item.getContent() != null) {
                            collector.checkThat("Missing item content in RSS feed", item.getContent(), notNullValue());
                        }
                    }
                    if(data.isPinterest != true)
                        collector.checkThat("Missing item pubdate in RSS feed", item.getPubDate(), notNullValue());

                    if(item.getEnclosure() != null) {
                        enclosureCount++;
                        collector.checkThat("item enclosure length is 0 in RSS feed", item.getEnclosure().getLength(), not(0));
                        collector.checkThat("Missing item enclosure url in RSS feed", item.getEnclosure().getUrl(), notNullValue());
                    }
                    if (item.getMetadata() != null) {
                        if (item.getMetadata().getMetaDataType() != null)
                            collector.checkThat("item metadata type is empty in RSS feed", item.getMetadata().getMetaDataType().getFormalName(), notNullValue());
                        if (item.getMetadata().getProperty().size() > 0)
                            collector.checkThat("item metadata getproperty is empty in RSS feed", item.getMetadata().getProperty().get(1).getValue(), notNullValue());
                    }
                    if(data.hasAmazon == true) {
                        if (item.getAmazonIntroText() != null) {
                            collector.checkThat("Missing item subtitle in Amazon RSS feed", item.getAmazonIntroText(), notNullValue());
                            if (item.getAmazonHeroImage() != null) {
                                collector.checkThat("Missing item hero image in Amazon RSS feed", item.getAmazonHeroImage(), notNullValue());
                            }
                            for (AmazonProducts products : item.getAmazonProducts()) {
                                for (AmazonProduct product : products.getAmazonProduct()) {
                                    collector.checkThat("Missing amazon.com in URL", product.getAmazonProductURL(),
                                            containsString("amazon.com"));
                                    collector.checkThat("Missing The Verdict in Amazon product summary", product.getAmazonProductSummary(),
                                            containsString("The Verdict"));
                                    if (product.getAmazonProductHeadline() != null)
                                        collector.checkThat("Amazon Headline is empty", product.getAmazonProductHeadline(),
                                            not(emptyOrNullString()));
                                    collector.checkThat("Missing amazon award in Amazon product summary", product.getAmazonAward(),
                                            notNullValue());
                                }
                            }
                        }
                    }
                }
                if(data.isPinterest == true)
                    collector.checkThat("enclosure tags are not present at the feed", enclosureCount, greaterThan(0));
            } catch (JAXBException e) {
                Assert.fail("Failed to parse an xml");
                e.printStackTrace();
            }
        };
    }

    /*
    Method that can be used to identify if a document has a `sponsor` object present.
    It will consume `item.getGuid()`
     */
    private boolean isSponsoredDoc(String url) {
        boolean isSponsoredDoc= false;
        RestAssuredClient client = new RestAssuredClient();
        HashMap<?,HashMap<?,?>> jsonData = given().spec(client.requestSpecification()).param("url", url).params("includesummaries", false).headers(client.headers()).with().contentType(Version.V2.getContentType()).get("/document").jsonPath().getJsonObject("$");
        try {
            if (jsonData.get("data").containsKey("sponsor"))
                System.out.println("The document " + url + " has sponsor object");
            isSponsoredDoc = true;
        } catch (Exception e) { }
        return isSponsoredDoc;
    }

}
