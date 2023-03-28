package com.about.mantle.venus.model.analytics;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector("#onetrust-consent-sdk")
public class MntlCCPABannerComponent extends MntlComponent {

    private final Lazy<WebElementEx> CCPABottomBanner;
    private final Lazy<WebElementEx> CCPAbannerBtnClose;
    private final Lazy<WebElementEx> CCPAbannerBtnCloseMobile;
    private final Lazy<WebElementEx> CCPAbannerPolicyText;
    private final Lazy<WebElementEx> CCPAbannerBtnAcceptCookies;
    private final Lazy<WebElementEx> CCPAbannerBtnDoNotSellInformationButton;
    private final Lazy<List<WebElementEx>> CCPAbannerPrivacyCenter;
    private final Lazy<WebElementEx> CCPAbannerPrivacyHeading;
    private final Lazy<WebElementEx> CCPAbannerPrivacyText;
    private final Lazy<WebElementEx> CCPAbannerPrivacyCenterClose;
    private final Lazy<WebElementEx> CCPAbannerPrivacyPolicyLink;
    private final Lazy<WebElementEx> CCPAbannerPrivacyEssentialFunctionalityButton;
    private final Lazy<WebElementEx> CCPAbannerPrivacyEssentialFunctionalityText;
    private final Lazy<WebElementEx> CCPAbannerPrivacyTargetingCookiesButton;
    private final Lazy<WebElementEx> CCPAbannerPrivacyTargetingCookiesCheckbox;
    private final Lazy<WebElementEx> CCPAbannerPrivacyTargetingCookiesText;
    private final Lazy<WebElementEx> CCPAbannerConfirmChoicesButton;

    public MntlCCPABannerComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.CCPABottomBanner = lazy(() -> findElement(By.id("onetrust-banner-sdk")));
        this.CCPAbannerBtnClose = lazy(() -> findElement(By.cssSelector("#onetrust-close-btn-container .banner-close-button")));
        this.CCPAbannerBtnCloseMobile = lazy(() -> findElement(By.cssSelector("#onetrust-close-btn-container-mobile .banner-close-button")));
        this.CCPAbannerPolicyText = lazy(() -> findElement(By.id("onetrust-policy-text")));
        this.CCPAbannerBtnAcceptCookies = lazy(() -> findElement(By.id("onetrust-accept-btn-handler")));
        this.CCPAbannerBtnDoNotSellInformationButton = lazy(() -> findElement(By.id("onetrust-pc-btn-handler")));
        this.CCPAbannerPrivacyHeading = lazy(() -> findElement(By.cssSelector("#ot-pc-content #ot-pc-title")));
        this.CCPAbannerPrivacyCenter = lazy(() -> findElements(By.id("onetrust-pc-sdk")));
        this.CCPAbannerPrivacyCenterClose = lazy(() -> findElement(By.id("close-pc-btn-handler")));
        this.CCPAbannerPrivacyText = lazy(() -> findElement(By.id("ot-pc-desc")));
        this.CCPAbannerPrivacyPolicyLink = lazy(() -> findElement(By.className("privacy-notice-link")));
        this.CCPAbannerPrivacyEssentialFunctionalityButton = lazy(() -> findElement(By.xpath("//button[@aria-labelledby='ot-header-id-C0001']")));
        this.CCPAbannerPrivacyEssentialFunctionalityText = lazy(() -> findElement(By.id("ot-desc-id-C0001")));
        this.CCPAbannerPrivacyTargetingCookiesButton = lazy(() -> findElement(By.xpath("//button[@aria-labelledby='ot-header-id-SPD_BG']")));
        this.CCPAbannerPrivacyTargetingCookiesCheckbox = lazy(() -> findElement(By.cssSelector(".ot-accordion-layout #ot-group-id-SPD_BG")));
        this.CCPAbannerPrivacyTargetingCookiesText = lazy(() -> findElement(By.cssSelector(".ot-accordion-layout #ot-desc-id-SPD_BG")));
        this.CCPAbannerConfirmChoicesButton = lazy(() -> findElement(By.className("save-preference-btn-handler")));
    }

    public WebElementEx bottomBanner() {
        return CCPABottomBanner.get();
    }

    public WebElementEx bannerClose() {
        if (mobile() && getElement().elementExists("#onetrust-close-btn-container-mobile .banner-close-button")) return CCPAbannerBtnCloseMobile.get();
        else return CCPAbannerBtnClose.get();
    }

    public String returnPolicyText() {
        return CCPAbannerPolicyText.get().getText();
    }

    public WebElementEx acceptCookies() {
        return CCPAbannerBtnAcceptCookies.get();
    }

    public WebElementEx doNotSellInformation() {
        return CCPAbannerBtnDoNotSellInformationButton.get();
    }

    public String returnPrivacyHeading() {
        return CCPAbannerPrivacyHeading.get().getText();
    }

    public List<WebElementEx> privacyCenter() {
        return CCPAbannerPrivacyCenter.get();
    }

    public WebElementEx privacyCenterClose() {
        return CCPAbannerPrivacyCenterClose.get();
    }

    public String returnPrivacyText() {
        return CCPAbannerPrivacyText.get().getText();
    }

    public WebElementEx privacyPolicy() {
        return CCPAbannerPrivacyPolicyLink.get();
    }

    public WebElementEx returnEssentialFunctionalityButton() {
        return CCPAbannerPrivacyEssentialFunctionalityButton.get();
    }

    public String returnEssentialFunctionalityText() {
        return CCPAbannerPrivacyEssentialFunctionalityText.get().getText();
    }

    public WebElementEx returnTargetingCookiesButton() {
        return CCPAbannerPrivacyTargetingCookiesButton.get();
    }

    public WebElementEx targetingCookiesCheckbox() {
        return CCPAbannerPrivacyTargetingCookiesCheckbox.get();
    }

    public String returnTargetingCookiesText() {
        return CCPAbannerPrivacyTargetingCookiesText.get().getText();
    }

    public WebElementEx confirmChoices() {
        return CCPAbannerConfirmChoicesButton.get();
    }


}
