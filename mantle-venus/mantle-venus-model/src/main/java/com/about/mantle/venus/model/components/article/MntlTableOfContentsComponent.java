package com.about.mantle.venus.model.components.article;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.MntlComponentXpathSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-toc , .mntl-sticky-toc , .mntl-toc-list")
public class MntlTableOfContentsComponent extends MntlComponent{
    private Lazy<WebElementEx> header;
    private Lazy<List<WebElementEx>> items;
    private final Lazy<WebElementEx> viewAllButton;
    private Lazy<WebElementEx> mobileToggleButton;

    public MntlTableOfContentsComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);

        this.header = lazy(() -> findElement(By.xpath("//*[contains(@class,'mntl-toc__heading-text')] | " +
                "//*[contains(@class,'toc__heading')] | //*[contains(@class,'toc__heading-text')]")));
        this.items = lazy(() -> findElements(By.cssSelector("a")));
        this.viewAllButton = lazy(() -> findElement(By.cssSelector(".mntl-toc-toggle .mntl-toc-toggle__btn")));
        this.mobileToggleButton = lazy(() -> findElement(By.cssSelector(".mntl-toc__mobile-toggle span")));
    }

    public WebElementEx header() {
        return header.get();
    }
    public List<WebElementEx> items() {
        return items.get();
    }
    public WebElementEx viewAllButton() { return this.viewAllButton.get(); }
    public WebElementEx mobileToggleButton() {
        return mobileToggleButton.get();
    }
    public boolean isExpanded() {
        return this.getElement().className().contains("mntl-toc--expanded");
    }
    public boolean viewAllButtonExist() {
        return this.getElement().elementExists(".mntl-toc-toggle");
    }
    public boolean isViewMoreButtonPresent() {
        return getElement().elementExists(".mntl-toc-toggle .mntl-toc-toggle__btn");
    }
    public boolean isMobileToggleExist(){
        return this.getElement().elementExists(".mntl-toc__mobile-toggle");
    }
    public boolean mobileToggleInViewport() {
        return this.getElement().elementExists(".mntl-toc__mobile-toggle.is-visible");
    }
    public boolean isTocListExpanded() {
        return this.getElement().elementExists(".is-expanded");
    }
}
