package com.about.mantle.components.privacy;

import com.about.mantle.venus.model.components.privacy.OneTrustBanner;
import com.about.mantle.venus.model.components.privacy.OneTrustPolicyConsentModal;
import com.about.mantle.venus.model.components.privacy.optanon.GdprDescriptionCenter;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.DataLayerUtils;
import com.google.common.base.Predicate;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

public abstract class GDPRBannerComponentTest extends CommonBannerComponentMethods {

    public GDPRBannerComponentTest() {
    }

    /* This test will check banner's behavior when accepting cookies and then opting out */
    protected Consumer<MntlRunner> GDPRbannerTestForAdsTargeting = (runner) -> {
        runner.driver().waitFor(1, TimeUnit.SECONDS);
        OneTrustBanner banner = (OneTrustBanner) (new MntlBasePage(runner.driver(), OneTrustBanner.class)).getComponent();
        checkBannerIsPresent(runner, banner);
        checkForBannerDescriptionTrackingEventAndAdConsent(runner, banner);
        collector.checkThat("OptanonConsent cookie is not refreshed/ Cache not cleared", getOptanonConsentCookieValue(runner, "interactionCount"), CoreMatchers.is("0"));
        click(runner, banner.acceptCookiesBtn());
        collector.checkThat("User interaction not registered", getOptanonConsentCookieValue(runner, "interactionCount"), CoreMatchers.is("1"));
        collector.checkThat("All Cookies not in accepted status", allCookiesAccepted(runner), CoreMatchers.is(true));
        refreshAndScroll(runner);
        collector.checkThat("ReadyForThirdPartyTracking event is not present after accepting cookies", DataLayerUtils.dataLayerEvents(runner.driver(), this.EVENT_NAME).size(), CoreMatchers.is(1));
        collector.checkThat("AD targeting consent is not given", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(true));
        collector.checkThat("EU Privacy footer link is not clickable or the Privacy center did not appear", isPrivacyCenterDisplayed(runner, Arrays.asList("EU PRIVACY", "EU PRIVACIDAD"), OneTrustPolicyConsentModal.class), CoreMatchers.is(true));
        OneTrustPolicyConsentModal policyModal = (OneTrustPolicyConsentModal) (new MntlBasePage(runner.driver(), OneTrustPolicyConsentModal.class)).getComponent();
        click(runner, policyModal.gdprModalContent().categoryItems().targetingCookies());
        WebElementEx toggleSwitch = mobile(runner.driver()) ? policyModal.gdprModalContent().categoryItems().itemDesc(TARGETINGCOOKIES) : policyModal.gdprModalContent().descriptionCenter().targetingCookiesDesc();
        click(runner, GdprDescriptionCenter.toggleSwitch(toggleSwitch));
        click(runner, policyModal.confirmMyChoicesBtn());
        collector.checkThat("Target Cookie consent preference not updated", isCookieAccepted(runner, TARGETINGCOOKIES), CoreMatchers.is(false));
        refreshAndScroll(runner);
        collector.checkThat("ReadyForThirdPartyTracking event is present after opting out", DataLayerUtils.dataLayerEvents(runner.driver(), this.EVENT_NAME).size(), CoreMatchers.is(0));
        collector.checkThat("AD consent value is still 'y'", isConsentProvidedOrNot(runner, 'n'), CoreMatchers.is(true));
    };

    /* This test will check banner's behavior when toggling on the cookies passed to the test and then toggling it off */
    protected BiConsumer<MntlRunner, String> GDPRbannerTestFor = (runner, cookieName) -> {
        runner.driver().waitFor(1, TimeUnit.SECONDS);
        OneTrustBanner banner = (OneTrustBanner) (new MntlBasePage(runner.driver(), OneTrustBanner.class)).getComponent();
        checkBannerIsPresent(runner, banner);
        checkForBannerDescriptionTrackingEventAndAdConsent(runner, banner);
        collector.checkThat("OptanonConsent cookie is not refreshed/ Cache not cleared", getOptanonConsentCookieValue(runner, "interactionCount"), CoreMatchers.is("0"));
        click(runner, banner.cookiesSettingsBtn());
        OneTrustPolicyConsentModal policyModal = (OneTrustPolicyConsentModal) (new MntlBasePage(runner.driver(), OneTrustPolicyConsentModal.class)).getComponent();
        click(runner, policyModal.gdprModalContent().categoryItems().categoryItem(cookieName));
        WebElementEx toggleSwitch = mobile(runner.driver()) ? policyModal.gdprModalContent().categoryItems().itemDesc(cookieName) : policyModal.gdprModalContent().descriptionCenter().itemDesc(cookieName);
        click(runner, GdprDescriptionCenter.toggleSwitch(toggleSwitch));
        click(runner, policyModal.confirmMyChoicesBtn());
        collector.checkThat("User interaction not registered", getOptanonConsentCookieValue(runner, "interactionCount"), CoreMatchers.is("1"));
        collector.checkThat("Strictly Necessary cookies should always be active", isCookieAccepted(runner, STRICTLYNECESSARYCOOKIES), CoreMatchers.is(true));
        collector.checkThat(cookieName + " consent preference not updated", isCookieAccepted(runner, cookieName), CoreMatchers.is(true));
        refreshAndScroll(runner);
        collector.checkThat("EU Privacy footer link is not clickable or the Privacy center did not appear", isPrivacyCenterDisplayed(runner, Arrays.asList("EU PRIVACY", "EU PRIVACIDAD"), OneTrustPolicyConsentModal.class), CoreMatchers.is(true));
        policyModal = (OneTrustPolicyConsentModal) (new MntlBasePage(runner.driver(), OneTrustPolicyConsentModal.class)).getComponent();
        click(runner, policyModal.gdprModalContent().categoryItems().categoryItem(cookieName));
        toggleSwitch = mobile(runner.driver()) ? policyModal.gdprModalContent().categoryItems().itemDesc(cookieName) : policyModal.gdprModalContent().descriptionCenter().itemDesc(cookieName);
        click(runner, GdprDescriptionCenter.toggleSwitch(toggleSwitch));
        click(runner, policyModal.confirmMyChoicesBtn());
        refreshAndScroll(runner);
        collector.checkThat("ReadyForThirdPartyTracking event is not present after opting out", DataLayerUtils.dataLayerEvents(runner.driver(), this.EVENT_NAME).size(), CoreMatchers.is(0));
        collector.checkThat("AD targeting consent is not given", isConsentProvidedOrNot(runner, 'n'), CoreMatchers.is(true));
        collector.checkThat("User interaction not registered", getOptanonConsentCookieValue(runner, "interactionCount"), CoreMatchers.is("2"));
        collector.checkThat("Strictly Necessary cookies should always be active", isCookieAccepted(runner, STRICTLYNECESSARYCOOKIES), CoreMatchers.is(true));
        collector.checkThat(cookieName + " consent preference not updated", isCookieAccepted(runner, cookieName), CoreMatchers.is(false));
    };

    /* This test will check banner's behavior when accepting the cookies and then redirecting to a secondary url */
    protected BiConsumer<MntlRunner, String> GDPRbannerTestAllowAll = (runner, secondaryURL) -> {
        runner.driver().waitFor(1, TimeUnit.SECONDS);
        OneTrustBanner banner = (OneTrustBanner) (new MntlBasePage(runner.driver(), OneTrustBanner.class)).getComponent();
        checkBannerIsPresent(runner, banner);
        checkForBannerDescriptionTrackingEventAndAdConsent(runner, banner);
        collector.checkThat("OptanonConsent cookie is not refreshed/ Cache not cleared", getOptanonConsentCookieValue(runner, "interactionCount"), CoreMatchers.is("0"));
        click(runner, banner.acceptCookiesBtn());
        refreshAndScroll(runner);
        collector.checkThat("User interaction not registered", getOptanonConsentCookieValue(runner, "interactionCount"), CoreMatchers.is("1"));
        collector.checkThat("ReadyForThirdPartyTracking event is not present after accepting cookies", DataLayerUtils.dataLayerEvents(runner.driver(), this.EVENT_NAME).size(), CoreMatchers.is(1));
        collector.checkThat("AD targeting consent is not given after accepting cookies", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(true));
        collector.checkThat("All Cookies not in accepted status", allCookiesAccepted(runner), CoreMatchers.is(true));
        runner.driver().get(secondaryURL + "?globeTest_cmp=1");
        scrollToFooter(runner);
        runner.driver().waitFor(4, TimeUnit.SECONDS);
        collector.checkThat("ReadyForThirdPartyTracking event is not present after accepting cookies and redirecting to a secondary url", DataLayerUtils.dataLayerEvents(runner.driver(), this.EVENT_NAME).size(), CoreMatchers.is(1));
        collector.checkThat("AD targeting consent is not present after accepting cookies and redirecting to a secondary url", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(true));
        collector.checkThat("All Cookies not in accepted status", allCookiesAccepted(runner), CoreMatchers.is(true));
    };

    /* This test will check banner's behavior when rejecting all cookies and redirecting to a secondary url */
    protected BiConsumer<MntlRunner, String> GDPRbannerTestRejectAll = (runner, secondaryURL) -> {
        runner.driver().waitFor(1, TimeUnit.SECONDS);
        OneTrustBanner banner = (OneTrustBanner) (new MntlBasePage(runner.driver(), OneTrustBanner.class)).getComponent();
        checkBannerIsPresent(runner, banner);
        checkForBannerDescriptionTrackingEventAndAdConsent(runner, banner);
        collector.checkThat("OptanonConsent cookie is not refreshed/ Cache not cleared", getOptanonConsentCookieValue(runner, "interactionCount"), CoreMatchers.is("0"));
        click(runner, banner.rejectAllBtn());
        refreshAndScroll(runner);
        collector.checkThat("Banner reappeared after allowing consent and page refreshed or AD consent given", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(false));
        collector.checkThat("User interaction not registered", getOptanonConsentCookieValue(runner, "interactionCount"), CoreMatchers.is("1"));
        collector.checkThat("Strictly Necessary cookies should always be active", isCookieAccepted(runner, STRICTLYNECESSARYCOOKIES), CoreMatchers.is(true));
        collector.checkThat("All Cookies (except strictlyNecessaryCookies) not in rejected status", allCookiesRejected(runner), CoreMatchers.is(true));
        collector.checkThat("ReadyForThirdPartyTracking event is present after rejecting all", DataLayerUtils.dataLayerEvents(runner.driver(), this.EVENT_NAME).size(), CoreMatchers.is(0));
        collector.checkThat("AD targeting consent is present after rejecting all", isConsentProvidedOrNot(runner, 'n'), CoreMatchers.is(true));
        runner.driver().get(secondaryURL + "?globeTest_cmp=1");
        scrollToFooter(runner);
        collector.checkThat("ReadyForThirdPartyTracking event is present after rejecting all and redirecting to a secondary url", DataLayerUtils.dataLayerEvents(runner.driver(), this.EVENT_NAME).size(), CoreMatchers.is(0));
        collector.checkThat("AD targeting consent is present after rejecting all and redirecting to a secondary url", isConsentProvidedOrNot(runner, 'n'), CoreMatchers.is(true));
        collector.checkThat("Banner reappeared after allowing consent and page refreshed or AD consent given", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(false));
        collector.checkThat("EU Privacy footer link is not clickable or the Privacy center did not appear", isPrivacyCenterDisplayed(runner, Arrays.asList("EU PRIVACY", "EU PRIVACIDAD"), OneTrustPolicyConsentModal.class), CoreMatchers.is(true));
    };

    /* This test will check banner's behavior in US */
    protected Consumer<MntlRunner> GDPRbannerAndModalComponentsInUS = (runner) -> {
        OneTrustBanner banner = new MntlBasePage<>(runner.driver(), OneTrustBanner.class).getComponent();
        collector.checkThat("Notification banner presence/absence is not as expected", banner.attributeValue("class").isEmpty(), CoreMatchers.is(true));
        scrollToFooter(runner);
        waitForDLEvent(runner);
        collector.checkThat("ReadyForThirdPartyTracking event is not present", DataLayerUtils.dataLayerEvents(runner.driver(), this.EVENT_NAME).size(), CoreMatchers.is(1));
        collector.checkThat("EU Privacy footer link appear on the page", isPrivacyCenterDisplayed(runner, Arrays.asList("EU PRIVACY", "EU PRIVACIDAD"), OneTrustPolicyConsentModal.class), CoreMatchers.is(false));
    };

    /* This test will check banner's behavior in places outside EU and US */
    protected Consumer<MntlRunner> GDPRbannerAndModalComponentsOutsideEUOrUS = (runner) -> {
        List<OneTrustBanner> GDPRBanners = new MntlBasePage<>(runner.driver(), OneTrustBanner.class).getComponents();
        collector.checkThat("Notification banner presence/absence is not as expected", GDPRBanners.size(), CoreMatchers.is(0));
        waitForDLEvent(runner);
        collector.checkThat("ReadyForThirdPartyTracking event is not present", DataLayerUtils.dataLayerEvents(runner.driver(), this.EVENT_NAME).size(), CoreMatchers.is(1));
        collector.checkThat("EU Privacy footer link appear on the page", isPrivacyCenterDisplayed(runner, Arrays.asList("EU PRIVACY", "EU PRIVACIDAD"), OneTrustPolicyConsentModal.class), CoreMatchers.is(false));
    };

    /* This test will check banner's behavior when closing the banner without any selection */
    protected Consumer<MntlRunner> GDPRbannerTestCloseWithoutSelection = runner -> {
        runner.driver().waitFor(1, TimeUnit.SECONDS);
        OneTrustBanner banner = (OneTrustBanner) (new MntlBasePage(runner.driver(), OneTrustBanner.class)).getComponent();
        checkBannerIsPresent(runner, banner);
        checkForBannerDescriptionTrackingEventAndAdConsent(runner, banner);
        collector.checkThat("OptanonConsent cookie is not refreshed/ Cache not cleared", getOptanonConsentCookieValue(runner, "interactionCount"), CoreMatchers.is("0"));
        collector.checkThat("Close banner button is empty (aria-label attribute is null or empty)", banner.closeBannerBtn().getAttribute("aria-label"), not(emptyOrNullString()));
        click(runner, banner.closeBannerBtn());
        refreshAndScroll(runner);
        collector.checkThat("ReadyForThirdPartyTracking event is present after closing the banner and refreshing the page", DataLayerUtils.dataLayerEvents(runner.driver(), this.EVENT_NAME).size(), CoreMatchers.is(0));
        collector.checkThat("AD targeting consent is present after closing the banner and refreshing the page", isConsentProvidedOrNot(runner, 'n'), CoreMatchers.is(true));
        collector.checkThat("User interaction not registered", getOptanonConsentCookieValue(runner, "interactionCount"), CoreMatchers.is("1"));
        collector.checkThat("Strictly Necessary cookies should always be active", isCookieAccepted(runner, STRICTLYNECESSARYCOOKIES), CoreMatchers.is(true));
        collector.checkThat("All Cookies (except strictlyNecessaryCookies) not in rejected status", allCookiesRejected(runner), CoreMatchers.is(true));
        collector.checkThat("EU Privacy footer link is not clickable or the Privacy center did not appear", isPrivacyCenterDisplayed(runner, Arrays.asList("EU PRIVACY", "EU PRIVACIDAD"), OneTrustPolicyConsentModal.class), CoreMatchers.is(true));
    };

    /* This test will check banner's header and footer */
    protected Consumer<MntlRunner> GDPRbannerTestForHeaderAndFooter = runner -> {
        runner.driver().waitFor(1, TimeUnit.SECONDS);
        OneTrustBanner banner = (OneTrustBanner) (new MntlBasePage(runner.driver(), OneTrustBanner.class)).getComponent();
        checkBannerIsPresent(runner, banner);
        checkForBannerDescriptionTrackingEventAndAdConsent(runner, banner);
        collector.checkThat("OptanonConsent cookie is not refreshed/ Cache not cleared", getOptanonConsentCookieValue(runner, "interactionCount"), CoreMatchers.is("0"));
        collector.checkThat("Close banner button is empty (aria-label attribute is null or empty)", banner.closeBannerBtn().getAttribute("aria-label"), not(emptyOrNullString()));
        click(runner, banner.closeBannerBtn());
        collector.checkThat("Banner reappeared after allowing consent and page refreshed or AD consent given", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(false));
        collector.checkThat("User interaction not registered", getOptanonConsentCookieValue(runner, "interactionCount"), CoreMatchers.is("1"));
        collector.checkThat("Strictly Necessary cookies should always be active", isCookieAccepted(runner, STRICTLYNECESSARYCOOKIES), CoreMatchers.is(true));
        collector.checkThat("All Cookies (except strictlyNecessaryCookies) not in rejected status", allCookiesRejected(runner), CoreMatchers.is(true));
        refreshAndScroll(runner);
        collector.checkThat("Banner reappeared after allowing consent and page refreshed or AD consent given", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(false));
        collector.checkThat("EU Privacy footer link is not clickable or the Privacy center did not appear", isPrivacyCenterDisplayed(runner, Arrays.asList("EU PRIVACY", "EU PRIVACIDAD"), OneTrustPolicyConsentModal.class), CoreMatchers.is(true));
        OneTrustPolicyConsentModal policyModal = (OneTrustPolicyConsentModal) (new MntlBasePage(runner.driver(), OneTrustPolicyConsentModal.class)).getComponent();
        collector.checkThat("Footer logo is not displayed on OneTrustPolicyConsentModal", policyModal.poweredByLogo().isDisplayed(), CoreMatchers.is(true));
        collector.checkThat("Header logo is not displayed on OneTrustPolicyConsentModal", policyModal.gdprModalHeader().pcLogo().isDisplayed(), CoreMatchers.is(true));
        collector.checkThat("Close button not enabled on OneTrustPolicyConsentModal", policyModal.gdprModalHeader().closeModalBtn().isEnabled(), CoreMatchers.is(true));
        collector.checkThat("Close modal button is empty (aria-label attribute is null or empty)", policyModal.gdprModalHeader().closeModalBtn().getAttribute("aria-label"), not(emptyOrNullString()));
        click(runner, policyModal.gdprModalHeader().closeModalBtn());
    };

    protected void checkBannerIsPresent(MntlRunner runner, OneTrustBanner banner) {
        Assert.assertTrue("Notification banner is not present in the DOM", runner.page().componentExists(OneTrustBanner.class));
        collector.checkThat("Notification banner is not displayed on the page", banner.attributeValue("class"), CoreMatchers.equalTo("show-banner"));
    }

    protected void checkForBannerDescriptionTrackingEventAndAdConsent(MntlRunner runner, OneTrustBanner banner) {
        collector.checkThat("Notification banner text is not as expected", banner.policyBannerText().getText(), CoreMatchers.is(bannerDescription()));
        List<Map<String, Object>> readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        collector.checkThat("ReadyForThirdPartyTracking event is present before giving a consent", readyForThirdPartyTrackingEvent.size(), CoreMatchers.is(0));
        collector.checkThat("ADconsent is 'y' on page load", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(false));
    }

}