package com.about.mantle.venus.model.components.navigation;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".breadcrumbs")
public class MntlBreadcrumbsComponent extends MntlComponent {

    private Lazy<List<WebElementEx>> breadcrumbsItems;

    public MntlBreadcrumbsComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.breadcrumbsItems = lazy(() -> findElements(By.tagName("a")));
    }

    public List<WebElementEx> breadcrumbsItems() {
        return breadcrumbsItems.get();
    }
}
