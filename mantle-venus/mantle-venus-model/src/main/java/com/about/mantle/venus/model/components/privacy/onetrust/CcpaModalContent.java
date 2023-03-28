package com.about.mantle.venus.model.components.privacy.onetrust;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.components.privacy.optanon.CCPAManageConsentPreferences;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector("#ot-pc-content")
public class CcpaModalContent extends MntlComponent {

    private final Lazy<WebElementEx> optOutForm = lazy(() -> findElement(By.cssSelector("a.ot-btn-anchor button")));
    private final Lazy<CCPAManageConsentPreferences> manageConsentPreferences = lazy(() -> findComponent(CCPAManageConsentPreferences.class));

    public CcpaModalContent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

    public WebElementEx optOutForm() {
        return this.optOutForm.get();
    }

    public CCPAManageConsentPreferences manageConsentPreferences() {
        return this.manageConsentPreferences.get();
    }

}