package com.about.mantle.venus.model.components.privacy.optanon;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector("section.ot-cat-grp")
public class CCPAManageConsentPreferences extends MntlComponent {

    private final Lazy<WebElementEx> strictlyNecessaryCookies = lazy(() -> findElement(By.cssSelector("button[aria-controls=ot-desc-id-1]")));
    private final Lazy<WebElementEx> toggleSwitch = this.lazy(() -> {
        return this.findElement(By.cssSelector("span.ot-switch-nob"));
    });

    public CCPAManageConsentPreferences(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

    public WebElementEx strictlyNecessaryCookies() {
        return this.strictlyNecessaryCookies.get();
    }

    public WebElementEx toggleSwitch() {
        return this.toggleSwitch.get();
    }
}