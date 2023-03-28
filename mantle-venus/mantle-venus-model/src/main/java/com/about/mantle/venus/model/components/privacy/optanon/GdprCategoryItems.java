package com.about.mantle.venus.model.components.privacy.optanon;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector(".ot-cat-grp")
public class GdprCategoryItems extends MntlComponent {

    private final Lazy<WebElementEx> strictlyNecessaryCookies = lazy(() -> findElement(By.cssSelector("div[aria-controls=ot-desc-id-1]")));
    private final Lazy<WebElementEx> targetingCookies = lazy(() -> findElement(By.cssSelector("div[aria-controls=ot-desc-id-4]")));

    public GdprCategoryItems(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

    public WebElementEx categoryItem(String itemName) {
        return findElements(By.cssSelector("div.category-menu-switch-handler"))
                .stream().filter(WebElementEx -> WebElementEx.getText().replaceAll("\\s", "").toUpperCase().equals(itemName)).findFirst().get();
    }

    public WebElementEx itemDesc(String itemName) {
        return findElements(By.cssSelector("li.ot-cat-item"))
                .stream().filter(WebElementEx -> WebElementEx.getText().replaceAll("\\s", "").toUpperCase().contains(itemName.replace("COOKIES", ""))).findFirst().get();
    }

    public WebElementEx strictlyNecessaryCookies() {
        return this.strictlyNecessaryCookies.get();
    }

    public WebElementEx targetingCookies() {
        return this.targetingCookies.get();
    }

}