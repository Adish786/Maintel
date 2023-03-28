package com.about.mantle.venus.model.components;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-article")
public class MnltArticleComponent extends MntlComponent {

    private final Lazy<List<WebElementEx>> tagNavItems;
    private final Lazy<WebElementEx> heading;

    public MnltArticleComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.tagNavItems = lazy(() -> findElements(By.className("tag-nav__item")));
        this.heading = lazy(() -> findElement(By.className("heading")));
    }

    public List<WebElementEx> tagNavItems() {
        return tagNavItems.get();
    }
    public String heading() {
        return heading.get().getText();
    }
}
