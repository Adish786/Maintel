package com.about.mantle.commerce;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.article.MantleArticleHeaderComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.mantle.venus.utils.selene.SeleneUtils;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.junit.Assert;

import java.time.Instant;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

public abstract class MntlLiveMonikerTest extends MntlVenusTest implements MntlCommonTestMethods {

    abstract public String getDocId();
    private final String revenuePath = "$.data.revenueGroup";
    private final String schemaTypePath = "$.data.webPageSchemaType";
    private final String liveBlogPostPath = "$.data.liveBlogPost";

    public Consumer<MntlRunner> liveMonikerTest = runner -> {
        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        DocumentContext seleneData = SeleneUtils.getDocuments(getDocId());
        MntlBasePage page = new MntlBasePage(runner.driver(), MantleArticleHeaderComponent.class);
        var revenue = JsonPath.using(conf).parse(seleneData.jsonString()).read(revenuePath);
        var webPageSchemaType = JsonPath.using(conf).parse(seleneData.jsonString()).read(schemaTypePath);
        var liveBlogPost = JsonPath.using(conf).parse(seleneData.jsonString()).read(liveBlogPostPath);

        if(revenue != null && webPageSchemaType != null){
            String revenueGroup = seleneData.read(revenuePath).toString().toLowerCase();
            String schemaType = seleneData.read(schemaTypePath).toString().toLowerCase();
            boolean isCommerce = revenueGroup.equalsIgnoreCase("commerce")
                    || revenueGroup.equalsIgnoreCase("commercenewsdeals") ? true : false;
            boolean isLiveBlog = schemaType.equalsIgnoreCase("LiveBlogPage") ? true : false;
            if(isCommerce && isLiveBlog){
                if(liveBlogPost != null) {
                    long endDate = JsonPath.using(conf).parse(seleneData.jsonString()).read(liveBlogPostPath+".coverageEndDate");
                    long currentDate = Instant.now().toEpochMilli();
                    MantleArticleHeaderComponent liveBlogBadge = (MantleArticleHeaderComponent) page.getComponent();
                    boolean isBlogBadgeDisplayed = liveBlogBadge.hasLiveBlogBadge();
                    if(endDate < currentDate){
                        Assert.assertFalse("Blog Badge is displayed when the date is ended", isBlogBadgeDisplayed);
                    }else {
                        Assert.assertTrue("Blog Badge is not displayed", isBlogBadgeDisplayed);
                        String badgeHref = liveBlogBadge.liveBlogBadge().getAttribute("xlink:href");
                        collector.checkThat("Blog badge href is not correct", badgeHref , is("#liveBlogBadge"));
                    }
                    collector.checkThat("Live Blog date is expired on the given document", endDate , greaterThan(currentDate));
                } else {
                    Assert.fail("Start and end date is not assigned for live blog badge");
                }
            } else {
                Assert.fail("A document should be provided with live blog");
            }
        } else {
            Assert.fail("A document should be provided with revenue group commerce/cnd and with webPageSchemaType");
        }
    };
}
