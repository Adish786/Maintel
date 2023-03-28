package com.about.mantle.venus.model.components.sections.newsFeed;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-news-feed")
public class MntlLatestNewsFeedComponent extends MntlComponent {

    private final Lazy<WebElementEx> newsFeedTitle;
    private final Lazy<List<MntlLatestNewsFeedArticleComponent>> newsFeedArticles;
    private final Lazy<WebElementEx> seeMoreNewsLink;

    public MntlLatestNewsFeedComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.newsFeedTitle = lazy(() -> findElement(By.cssSelector(".news-feed__title")));
        this.newsFeedArticles = lazy(() -> findComponents(MntlLatestNewsFeedArticleComponent.class));
        this.seeMoreNewsLink = lazy(() -> findElement(By.cssSelector(".news-feed__see-more-link")));
    }

    public WebElementEx newsFeedTitle() { return newsFeedTitle.get(); }

    public List<MntlLatestNewsFeedArticleComponent> newsFeedArticles() { return newsFeedArticles.get(); }

    public WebElementEx seeMoreNewsLink() { return seeMoreNewsLink.get(); }
}