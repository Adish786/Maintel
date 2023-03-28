package com.about.mantle.venus.model.analytics;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector("#onetrust-consent-sdk")
public class MntlGDPRBannerComponent extends MntlComponent {

    private final Lazy<WebElementEx> GDPRbannerBtnClose;
    private final Lazy<WebElementEx> GDPRbannerBtnCloseMobile;
    private final Lazy<WebElementEx> GDPRbannerPolicyText;
    private final Lazy<WebElementEx> GDPRbannerPolicySecondText;
    private final Lazy<WebElementEx> GDPRbannerBtnShowPartners;
    private final Lazy<WebElementEx> GDPRbannerBtnAcceptCookies;
    private final Lazy<WebElementEx> GDPRbannerBtnShowPurposes;
    private final Lazy<WebElementEx> GDPRbannerBtnClickHere;
    private final Lazy<WebElementEx> GDPRbannerBtnAllowAll;
    private final Lazy<List<WebElementEx>> GDPRbannerPrivacyCenter;
    private final Lazy<WebElementEx> GDPRbannerPrivacyTargetingCookiesRow;
    private final Lazy<WebElementEx> GDPRbannerPrivacyStoringInfoRow;
    private final Lazy<WebElementEx> GDPRbannerPrivacyTargetingCookiesCheckbox;
    private final Lazy<WebElementEx> GDPRbannerPrivacyStoringInfoCheckbox;
    private final Lazy<WebElementEx> GDPRbannerPrivacyListOfIABvendors;
    private final Lazy<WebElementEx> GDPRbannerPrivacySearchHandler;
    private final Lazy<WebElementEx> GDPRbannerPrivacyGoogleCheckbox;
    private final Lazy<WebElementEx> GDPRbannerPrivacyBackButton;
    private final Lazy<WebElementEx> GDPRbannerPrivacyCenterConfirmChoicesButton;

    public MntlGDPRBannerComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.GDPRbannerBtnClose = lazy(() -> findElement(By.cssSelector("#onetrust-close-btn-container .banner-close-button")));
        this.GDPRbannerBtnCloseMobile = lazy(() -> findElement(By.cssSelector("#onetrust-close-btn-container-mobile .banner-close-button")));
        this.GDPRbannerPolicyText = lazy(() -> findElement(By.id("onetrust-policy-text")));
        this.GDPRbannerPolicySecondText = lazy(() -> findElement(By.cssSelector(".ot-dpd-desc")));
        this.GDPRbannerBtnShowPartners = lazy(() -> findElement(By.cssSelector(".show-link")));
        this.GDPRbannerBtnAcceptCookies = lazy(() -> findElement(By.id("onetrust-accept-btn-handler")));
        this.GDPRbannerBtnShowPurposes = lazy(() -> findElement(By.id("onetrust-pc-btn-handler")));
        this.GDPRbannerBtnClickHere = lazy(() -> findElement(By.cssSelector("#onetrust-policy-text a:not(.onetrust-vendors-list-handler)")));
        this.GDPRbannerBtnAllowAll = lazy(() -> findElement(By.id("accept-recommended-btn-handler")));
        this.GDPRbannerPrivacyCenter = lazy(() -> findElements(By.cssSelector("#onetrust-pc-sdk")));
        this.GDPRbannerPrivacyTargetingCookiesRow = lazy(() -> findElement(By.cssSelector(".ot-sdk-four #ot-header-id-C0004")));
        this.GDPRbannerPrivacyStoringInfoRow = lazy(() -> findElement(By.cssSelector(".ot-sdk-four #ot-header-id-IABV2_1")));
        this.GDPRbannerPrivacyTargetingCookiesCheckbox = lazy(() -> findElement(By.cssSelector("#ot-desc-id-C0004 #ot-group-id-C0004")));
        this.GDPRbannerPrivacyStoringInfoCheckbox = lazy(() -> findElement(By.cssSelector("#ot-desc-id-IABV2_1 #ot-group-id-IABV2_1")));
        this.GDPRbannerPrivacyListOfIABvendors = lazy(() -> findElement(By.cssSelector("#ot-desc-id-IABV2_1 .category-vendors-list-handler")));
        this.GDPRbannerPrivacySearchHandler = lazy(() -> findElement(By.cssSelector(".action-container #vendor-search-handler")));
        this.GDPRbannerPrivacyGoogleCheckbox = lazy(() -> findElement(By.cssSelector("#select-all-vendor-groups-handler")));
        this.GDPRbannerPrivacyBackButton = lazy(() -> findElement(By.cssSelector(".pc-back-button-text")));
        this.GDPRbannerPrivacyCenterConfirmChoicesButton = lazy(() -> findElement(By.cssSelector(".save-preference-btn-handler")));
    }

    public List<WebElementEx> privacyCenter() { return GDPRbannerPrivacyCenter.get(); }

    public WebElementEx storingInfoRow() { return GDPRbannerPrivacyStoringInfoRow.get(); }

    public WebElementEx targetingCookiesRow() { return GDPRbannerPrivacyTargetingCookiesRow.get(); }

    public WebElementEx targetingCookiesCheckbox() { return GDPRbannerPrivacyTargetingCookiesCheckbox.get(); }

    public WebElementEx storingInfoCheckbox() { return GDPRbannerPrivacyStoringInfoCheckbox.get(); }

    public WebElementEx listOfIABvendors() { return GDPRbannerPrivacyListOfIABvendors.get(); }

    public WebElementEx searchHandler() { return GDPRbannerPrivacySearchHandler.get(); }

    public WebElementEx googleCheckbox() { return GDPRbannerPrivacyGoogleCheckbox.get(); }

    public WebElementEx backButton() { return GDPRbannerPrivacyBackButton.get(); }

    public WebElementEx confirmChoicesButton() { return GDPRbannerPrivacyCenterConfirmChoicesButton.get(); }

    public WebElementEx bannerClose() {
        if (mobile() && getElement().elementExists("#onetrust-close-btn-container-mobile .banner-close-button")) return GDPRbannerBtnCloseMobile.get();
        else return GDPRbannerBtnClose.get();
    }

    public String returnPolicyText() { return GDPRbannerPolicyText.get().getText(); }

    public String returnPolicySecondText() { return GDPRbannerPolicySecondText.get().getText(); }

    public WebElementEx showPartners() {
        return GDPRbannerBtnShowPartners.get();
    }

    public WebElementEx acceptCookies() { return GDPRbannerBtnAcceptCookies.get(); }

    public WebElementEx showPurposes() { return GDPRbannerBtnShowPurposes.get(); }

    public WebElementEx clickHere() {
        return GDPRbannerBtnClickHere.get();
    }

    public WebElementEx allowAll() { return GDPRbannerBtnAllowAll.get(); }

}