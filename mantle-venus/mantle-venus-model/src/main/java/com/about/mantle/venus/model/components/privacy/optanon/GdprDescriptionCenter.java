package com.about.mantle.venus.model.components.privacy.optanon;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector("div.ot-tab-desc")
public class GdprDescriptionCenter extends MntlComponent {

    private final Lazy<WebElementEx> strictlyNecessaryCookiesDesc = lazy(() -> findElement(By.id("ot-desc-id-1")));
    private final Lazy<WebElementEx> performanceCookiesDesc = lazy(() -> findElement(By.id("ot-desc-id-2")));
    private final Lazy<WebElementEx> functionalCookiesDesc = lazy(() -> findElement(By.id("ot-desc-id-3")));
    private final Lazy<WebElementEx> targetingCookiesDesc = lazy(() -> findElement(By.id("ot-desc-id-4")));
    private final Lazy<WebElementEx> socialMediaCookiesDesc = lazy(() -> findElement(By.id("ot-desc-id-5")));

    public GdprDescriptionCenter(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

    public static WebElementEx toggleSwitch(WebElementEx itemName) {
        return itemName.findElementEx(By.cssSelector("input.category-switch-handler"));
    }

    public WebElementEx itemDesc(String itemName) {
        return findElements(By.cssSelector("div.ot-desc-cntr"))
                .stream().filter(WebElementEx -> WebElementEx.getText().replaceAll("\\s", "").toUpperCase().contains(itemName)).findFirst().get();
    }

    public WebElementEx strictlyNecessaryCookiesDesc() {
        return this.strictlyNecessaryCookiesDesc.get();
    }

    public WebElementEx performanceCookiesDesc() {
        return this.performanceCookiesDesc.get();
    }

    public WebElementEx functionalCookiesDesc() {
        return this.functionalCookiesDesc.get();
    }

    public WebElementEx targetingCookiesDesc() {
        return this.targetingCookiesDesc.get();
    }

    public WebElementEx socialMediaCookiesDesc() {
        return this.socialMediaCookiesDesc.get();
    }
}