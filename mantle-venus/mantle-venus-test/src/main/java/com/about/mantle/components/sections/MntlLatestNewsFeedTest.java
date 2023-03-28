package com.about.mantle.components.sections;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.sections.newsFeed.MntlLatestNewsFeedArticleComponent;
import com.about.mantle.venus.model.components.sections.newsFeed.MntlLatestNewsFeedComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import java.util.function.BiConsumer;

import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWithIgnoringCase;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;

/*
This test will validate the latest news feed component.
It will first check if the component exists in the DOM, then will check if it's displayed, its title and articles size
(verticals are supposed to pass expected values from their side).
Then it will loop through each article and check the image is displayed, image src not null (if image is expected to be -
by default it will check images, if verticals don't expect image on cards they should set the flag to false on their side),
article href not null, article content is displayed, article tag (taxonomy) not null, title is displayed and not null,
byline not empty and starts with 'by', byline separator not null and byline date not null.
If verticals are expecting See All News link to be at the bottom of the component it will check the link href
(by default it will check it, if verticals are not expecting the link to be on the component they should set the flag
to false on their side).
*/

public abstract class MntlLatestNewsFeedTest extends MntlVenusTest implements MntlCommonTestMethods {

    protected abstract String latestNewsTitle();
    protected abstract int latestNewsArticlesSize();
    protected boolean expectImagesOnCards = true;
    protected boolean expectSeeMoreNewsLink = true;

    protected BiConsumer<MntlRunner, String> testLatestNewsFeed = (runner, newsPage) -> {
        assertTrue("Latest News Feed component is not present in the DOM", runner.page().componentExists(MntlLatestNewsFeedComponent.class));
        MntlLatestNewsFeedComponent latestNewsFeedComponent = new MntlBasePage<>(runner.driver(), MntlLatestNewsFeedComponent.class).getComponent();
        latestNewsFeedComponent.scrollIntoViewCentered();
        collector.checkThat("Latest News Feed component is not displayed on the page", latestNewsFeedComponent.displayed(), is(true));
        collector.checkThat("Latest News Feed Component title is not correct", latestNewsFeedComponent.newsFeedTitle().getText(), is(latestNewsTitle()));
        collector.checkThat("Latest News Feed Component articles size is not correct", latestNewsFeedComponent.newsFeedArticles().size(), is(latestNewsArticlesSize()));
        int articleCount = 1;
        for (MntlLatestNewsFeedArticleComponent latestNewsArticle : latestNewsFeedComponent.newsFeedArticles()) {
            latestNewsArticle.scrollIntoViewCentered();
            if (expectImagesOnCards) {
                collector.checkThat("Latest news article " + articleCount + " image is not displayed",
                        latestNewsArticle.image().isDisplayed(), is(true));
                collector.checkThat("Latest news article " + articleCount + " image src is null or empty",
                        latestNewsArticle.image().src(), not(emptyOrNullString()));
            }
            collector.checkThat("Latest news article " + articleCount + " href is null or empty",
                    latestNewsArticle.href(), not(emptyOrNullString()));
            collector.checkThat("Latest news article " + articleCount + "content is not displayed",
                    latestNewsArticle.cardContent().isDisplayed(), is(true));
            collector.checkThat("Latest news article " + articleCount + " taxonomy is null or empty",
                    latestNewsArticle.cardContent().pseudoElementProperty("before", "content"), not(emptyOrNullString()));
            collector.checkThat("Latest news article " + articleCount + " title is not displayed",
                    latestNewsArticle.cardTitle().isDisplayed(), is(true));
            collector.checkThat("Latest news article " + articleCount + " title is empty or null",
                    latestNewsArticle.cardTitle().getText(), not(emptyOrNullString()));
            collector.checkThat("Latest news article " + articleCount + " byline is empty or null",
                    latestNewsArticle.cardByline().getAttribute("data-byline"), not(emptyOrNullString()));
            collector.checkThat("Latest news article " + articleCount + " byline doesn't start with 'By'",
                    latestNewsArticle.cardByline().getAttribute("data-byline"), startsWithIgnoringCase("By"));
            collector.checkThat("Latest news article " + articleCount + " byline separator is empty or null",
                    latestNewsArticle.cardBylineDate().pseudoElementProperty("before", "content"), not(emptyOrNullString()));
            collector.checkThat("Latest news article " + articleCount + " byline date is empty or null",
                    latestNewsArticle.cardBylineDate().getAttribute("data-block-date"), not(emptyOrNullString()));
            articleCount++;
        }
        if (expectSeeMoreNewsLink) {
            collector.checkThat("See more news link is empty or null", latestNewsFeedComponent.seeMoreNewsLink().href(), not(emptyOrNullString()));
            latestNewsFeedComponent.seeMoreNewsLink().click();
            latestNewsFeedComponent.waitFor().pageRefreshed();
            collector.checkThat("Clicking on see more news link did not redirect to the right url", runner.driver().getCurrentUrl(), is(newsPage));
        }
    };
}