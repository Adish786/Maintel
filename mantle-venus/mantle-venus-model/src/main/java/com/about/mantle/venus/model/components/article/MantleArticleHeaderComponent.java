package com.about.mantle.venus.model.components.article;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector(".article-header")
public class MantleArticleHeaderComponent extends MntlComponent {

    private final Lazy<WebElementEx> testedBadge ;
    private final Lazy<WebElementEx> liveBlogBadge;

    public MantleArticleHeaderComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.testedBadge = lazy(() -> findElement(By.cssSelector(".commerce-tested__badge use")));
        this.liveBlogBadge = lazy(() -> findElement(By.cssSelector(".liveBlogBadge use")));
    }

    public WebElementEx testedBadge() { return testedBadge.get(); }
    public WebElementEx liveBlogBadge() { return liveBlogBadge.get(); }
    public boolean hasTestedBadge(){ return getElement().elementExists(".commerce-tested__badge"); }
    public boolean hasLiveBlogBadge(){ return getElement().elementExists(".liveBlogBadge"); }

}
