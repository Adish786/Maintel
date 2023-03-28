package com.about.mantle.components.privacy;

import com.about.mantle.venus.model.components.privacy.OneTrustPolicyConsentModal;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.utils.DataLayerUtils;
import com.google.common.base.Predicate;
import org.hamcrest.CoreMatchers;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

public abstract class CCPAModalComponentTest extends CommonBannerComponentMethods {

    /* This test will check banner's behavior when opting out and then opting in */
    protected Consumer<MntlRunner> CCPABannerTestAdsNoTargeting = (runner) -> {
        scrollToFooter(runner);
        waitForDLEvent(runner);
        collector.checkThat("ReadyForThirdPartyTrackingEvent is not present before the action", DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME).size(), CoreMatchers.is(1));
        collector.checkThat("ADconsent is 'n' on page load", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(true));
        collector.checkThat("DoNotSellMyPersonalInformation footer link is not clickable or the Privacy center did not appear", isPrivacyCenterDisplayed(runner, Arrays.asList("DO NOT SELL MY PERSONAL INFORMATION", "NO VENDER MIS DATOS PERSONALES"), OneTrustPolicyConsentModal.class), CoreMatchers.is(true));
        OneTrustPolicyConsentModal policyModal = (OneTrustPolicyConsentModal) (new MntlBasePage(runner.driver(), OneTrustPolicyConsentModal.class)).getComponent();
        collector.checkThat("All Cookies not in accepted status", allCookiesAccepted(runner), CoreMatchers.is(true));
        click(runner, policyModal.ccpaModalContent().manageConsentPreferences().toggleSwitch());
        click(runner, policyModal.confirmMyChoicesBtn());
        refreshAndScroll(runner);
        collector.checkThat("ReadyForThirdPartyTrackingEvent is present after opting out", DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME).size(), CoreMatchers.is(0));
        collector.checkThat("ADconsent is 'y' after opting out", isConsentProvidedOrNot(runner, 'n'), CoreMatchers.is(true));
        collector.checkThat("Target Cookie consent preference not updated", isCookieAccepted(runner, TARGETINGCOOKIES), CoreMatchers.is(false));
        collector.checkThat("Extra Target Cookie consent preference not updated", isCookieAccepted(runner, EXTRATARGETINGCOOKIESCCPA), CoreMatchers.is(false));
        collector.checkThat("Do Not Sell My Personal Information footer link is not clickable or the Privacy center did not appear", isPrivacyCenterDisplayed(runner, Arrays.asList("DO NOT SELL MY PERSONAL INFORMATION", "NO VENDER MIS DATOS PERSONALES"), OneTrustPolicyConsentModal.class), CoreMatchers.is(true));
        policyModal = (OneTrustPolicyConsentModal) (new MntlBasePage(runner.driver(), OneTrustPolicyConsentModal.class)).getComponent();
        click(runner, policyModal.ccpaModalContent().manageConsentPreferences().toggleSwitch());
        click(runner, policyModal.confirmMyChoicesBtn());
        refreshAndScroll(runner);
        collector.checkThat("ReadyForThirdPartyTrackingEvent is not present after opting in", DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME).size(), CoreMatchers.is(1));
        collector.checkThat("ADconsent is 'n' after opting in", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(true));
        collector.checkThat("Target Cookie consent preference not updated", isCookieAccepted(runner, TARGETINGCOOKIES), CoreMatchers.is(true));
        collector.checkThat("Extra Target Cookie consent preference not updated", isCookieAccepted(runner, EXTRATARGETINGCOOKIESCCPA), CoreMatchers.is(true));
    };

    /* This test will check banner's behavior when opting out without confirming choices */
    protected Consumer<MntlRunner> CCPABannerComponentTestOptOutWithoutConfirming = (runner) -> {
        scrollToFooter(runner);
        waitForDLEvent(runner);
        collector.checkThat("ReadyForThirdPartyTrackingEvent is not present before the action", DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME).size(), CoreMatchers.is(1));
        collector.checkThat("ADconsent is 'n' on page load", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(true));
        collector.checkThat("DoNotSellMyPersonalInformation footer link is not clickable or the Privacy center did not appear", isPrivacyCenterDisplayed(runner, Arrays.asList("DO NOT SELL MY PERSONAL INFORMATION", "NO VENDER MIS DATOS PERSONALES"), OneTrustPolicyConsentModal.class), CoreMatchers.is(true));
        OneTrustPolicyConsentModal policyModal = (OneTrustPolicyConsentModal) (new MntlBasePage(runner.driver(), OneTrustPolicyConsentModal.class)).getComponent();
        collector.checkThat("All Cookies not in accepted status", allCookiesAccepted(runner), CoreMatchers.is(true));
        click(runner, policyModal.ccpaModalContent().manageConsentPreferences().toggleSwitch());
        collector.checkThat("Close modal button is empty (aria-label attribute is null or empty)", policyModal.ccpaModalHeader().closeModalBtn().getAttribute("aria-label"), not(emptyOrNullString()));
        click(runner, policyModal.ccpaModalHeader().closeModalBtn());
        refreshAndScroll(runner);
        collector.checkThat("ReadyForThirdPartyTrackingEvent is not present after closing the privacy center and refreshing the page", DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME).size(), CoreMatchers.is(1));
        collector.checkThat("ADconsent is 'n' after closing the privacy center and refreshing the page", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(true));
        collector.checkThat("Target Cookie consent preference not updated", isCookieAccepted(runner, TARGETINGCOOKIES), CoreMatchers.is(true));
        collector.checkThat("Extra Target Cookie consent preference not updated", isCookieAccepted(runner, EXTRATARGETINGCOOKIESCCPA), CoreMatchers.is(true));
    };

    /* This test will check banner's behavior when opting out and redirecting to a secondary url */
    protected BiConsumer<MntlRunner, String> CCPABannerTestOptOutWithSecondaryURL = (runner, secondaryURL) -> {
        scrollToFooter(runner);
        waitForDLEvent(runner);
        collector.checkThat("ReadyForThirdPartyTrackingEvent is not present before the action", DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME).size(), CoreMatchers.is(1));
        collector.checkThat("ADconsent is 'n' on page load", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(true));
        collector.checkThat("DoNotSellMyPersonalInformation footer link is not clickable or the Privacy center did not appear", isPrivacyCenterDisplayed(runner, Arrays.asList("DO NOT SELL MY PERSONAL INFORMATION", "NO VENDER MIS DATOS PERSONALES"), OneTrustPolicyConsentModal.class), CoreMatchers.is(true));
        OneTrustPolicyConsentModal policyModal = (OneTrustPolicyConsentModal) (new MntlBasePage(runner.driver(), OneTrustPolicyConsentModal.class)).getComponent();
        collector.checkThat("All Cookies not in accepted status", allCookiesAccepted(runner), CoreMatchers.is(true));
        click(runner, policyModal.ccpaModalContent().manageConsentPreferences().toggleSwitch());
        click(runner, policyModal.confirmMyChoicesBtn());
        refreshAndScroll(runner);
        collector.checkThat("ReadyForThirdPartyTrackingEvent is present after opting out", DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME).size(), CoreMatchers.is(0));
        collector.checkThat("ADconsent is 'y' after opting out", isConsentProvidedOrNot(runner, 'n'), CoreMatchers.is(true));
        collector.checkThat("Target Cookie consent preference not updated", isCookieAccepted(runner, TARGETINGCOOKIES), CoreMatchers.is(false));
        collector.checkThat("Extra Target Cookie consent preference not updated", isCookieAccepted(runner, EXTRATARGETINGCOOKIESCCPA), CoreMatchers.is(false));
        runner.driver().get(secondaryURL);
        scrollToFooter(runner);
        collector.checkThat("ReadyForThirdPartyTrackingEvent is present after opting out and redirecting to a secondary url", DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME).size(), CoreMatchers.is(0));
        collector.checkThat("ADconsent is 'y' after opting out and redirecting to a secondary url", isConsentProvidedOrNot(runner, 'n'), CoreMatchers.is(true));
        collector.checkThat("Target Cookie consent preference not updated", isCookieAccepted(runner, TARGETINGCOOKIES), CoreMatchers.is(false));
        collector.checkThat("Extra Target Cookie consent preference not updated", isCookieAccepted(runner, EXTRATARGETINGCOOKIESCCPA), CoreMatchers.is(false));
    };

    /* This test will check banner's header and footer */
    protected Consumer<MntlRunner> CCPABannerComponentTestForHeaderAndFooter = (runner) -> {
        scrollToFooter(runner);
        waitForDLEvent(runner);
        collector.checkThat("ReadyForThirdPartyTrackingEvent is not present before the action", DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME).size(), CoreMatchers.is(1));
        collector.checkThat("ADconsent is 'n' on page load", isConsentProvidedOrNot(runner, 'y'), CoreMatchers.is(true));
        collector.checkThat("DoNotSellMyPersonalInformation footer link is not clickable or the Privacy center did not appear", isPrivacyCenterDisplayed(runner, Arrays.asList("DO NOT SELL MY PERSONAL INFORMATION", "NO VENDER MIS DATOS PERSONALES"), OneTrustPolicyConsentModal.class), CoreMatchers.is(true));
        OneTrustPolicyConsentModal policyModal = (OneTrustPolicyConsentModal) (new MntlBasePage(runner.driver(), OneTrustPolicyConsentModal.class)).getComponent();
        click(runner, policyModal.ccpaModalContent().manageConsentPreferences().toggleSwitch());
        collector.checkThat("Footer logo is not displayed on OneTrustPolicyConsentModal", policyModal.poweredByLogo().isDisplayed(), CoreMatchers.is(true));
        collector.checkThat("Header logo is not displayed on OneTrustPolicyConsentModal", policyModal.ccpaModalHeader().ccpaLogo().isDisplayed(), CoreMatchers.is(true));
        collector.checkThat("Close button not enabled on OneTrustPolicyConsentModal", policyModal.ccpaModalHeader().closeModalBtn().isEnabled(), CoreMatchers.is(true));
        collector.checkThat("Close modal button is empty (aria-label attribute is null or empty)", policyModal.ccpaModalHeader().closeModalBtn().getAttribute("aria-label"), not(emptyOrNullString()));
        click(runner, policyModal.ccpaModalHeader().closeModalBtn());
    };

    /* This test will check banner's behavior outside US */
    protected Consumer<Runner> CCPAModalComponentTestForOutsideUS = (runner)  -> {
        collector.checkThat("DoNotSellMyPersonalInformation footer link appear on the page", this.isPrivacyCenterDisplayed((MntlRunner) runner, Arrays.asList("DO NOT SELL MY PERSONAL INFORMATION", "NO VENDER MIS DATOS PERSONALES"), OneTrustPolicyConsentModal.class), CoreMatchers.is(false));
    };

    public CCPAModalComponentTest() {
    }

}