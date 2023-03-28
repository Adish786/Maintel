package com.about.mantle.venus.model.components.privacy;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.components.privacy.onetrust.CcpaModalContent;
import com.about.mantle.venus.model.components.privacy.onetrust.CcpaModalHeader;
import com.about.mantle.venus.model.components.privacy.onetrust.GdprModalContent;
import com.about.mantle.venus.model.components.privacy.onetrust.GdprModalHeader;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector("div#onetrust-pc-sdk")
public class OneTrustPolicyConsentModal extends MntlComponent {

    private final Lazy<GdprModalHeader> gdprModalHeader = lazy(() -> findComponent(GdprModalHeader.class));
    private final Lazy<GdprModalContent> gdprModalContent = lazy(() -> findComponent(GdprModalContent.class));
    private final Lazy<CcpaModalHeader> ccpaModalHeader = lazy(() -> findComponent(CcpaModalHeader.class));
    private final Lazy<CcpaModalContent> ccpaModalContent = lazy(() -> findComponent(CcpaModalContent.class));
    private final Lazy<WebElementEx> poweredByLogo = lazy(() -> findElement(By.cssSelector("div.ot-pc-footer-logo")));
    private final Lazy<WebElementEx> confirmMyChoicesBtn = lazy(() -> findElement(By.cssSelector("button.save-preference-btn-handler")));

    public OneTrustPolicyConsentModal(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

    public GdprModalHeader gdprModalHeader() {
        return this.gdprModalHeader.get();
    }

    public GdprModalContent gdprModalContent() {
        return this.gdprModalContent.get();
    }

    public CcpaModalHeader ccpaModalHeader() {
        return this.ccpaModalHeader.get();
    }

    public CcpaModalContent ccpaModalContent() {
        return this.ccpaModalContent.get();
    }

    public WebElementEx poweredByLogo() {
        return this.poweredByLogo.get();
    }

    public WebElementEx confirmMyChoicesBtn() {
        return this.confirmMyChoicesBtn.get();
    }

}