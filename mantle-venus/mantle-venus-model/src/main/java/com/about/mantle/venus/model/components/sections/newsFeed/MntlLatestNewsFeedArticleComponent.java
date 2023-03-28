package com.about.mantle.venus.model.components.sections.newsFeed;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentId("mntl-card-list-items")
public class MntlLatestNewsFeedArticleComponent extends MntlComponent {

    private final Lazy<WebElementEx> image;
    private final Lazy<WebElementEx> cardTitle;
    private final Lazy<WebElementEx> cardContent;
    private final Lazy<WebElementEx> cardByline;
    private final Lazy<WebElementEx> cardBylineDate;

    public MntlLatestNewsFeedArticleComponent (WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.image = lazy(() -> findElement(By.tagName("img")));
        this.cardTitle = lazy(() -> findElement(By.cssSelector(".card__title-text")));
        this.cardContent = lazy(() -> findElement(By.cssSelector(".card__content")));
        this.cardByline = lazy(() -> findElement(By.cssSelector(".mntl-card__byline")));
        this.cardBylineDate = lazy(() -> findElement(By.cssSelector(".card__date")));
    }

    public WebElementEx image() { return image.get(); }

    public WebElementEx cardTitle() { return cardTitle.get(); }

    public WebElementEx cardContent() { return cardContent.get(); }

    public WebElementEx cardByline() { return cardByline.get(); }

    public WebElementEx cardBylineDate() { return cardBylineDate.get(); }
}