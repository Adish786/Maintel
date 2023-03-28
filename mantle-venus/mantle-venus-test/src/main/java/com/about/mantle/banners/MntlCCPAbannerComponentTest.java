package com.about.mantle.banners;

import com.about.mantle.venus.model.analytics.MntlCCPABannerComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.utils.DataLayerUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Deprecated
public abstract class MntlCCPAbannerComponentTest extends MntlCommonBannerComponentMethods {

    /* This test will check banner's behavior when accepting the cookies, refreshing the page and opting out the targeting cookies */
    protected Consumer<MntlRunner> CCPABannerTestAdsNoTargeting = (runner) -> {
        checkBannerPresence(runner);
        MntlCCPABannerComponent banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        List<Map<String, Object>> readyForThirdPartyTrackingEvent;
        checkForBannerDescriptionTrackingEventAndAdConsent(runner, banner);
        click(runner, banner.acceptCookies());
        refreshAndScroll(runner);
        readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        collector.checkThat("readyForThirdPartyTrackingEvent is fired more than once",
                readyForThirdPartyTrackingEvent.size(), is(1));
        collector.checkThat("adconsent=n after accepting cookies",
                isConsentProvidedOrNot(runner, 'y'), is(true));
        collector.checkThat("Do Not Sell My Personal Information footer link is not clickable or the Privacy center did not appear",
                isPrivacyCenterDisplayed(runner, Arrays.asList("DO NOT SELL MY PERSONAL INFORMATION", "NO VENDER MIS DATOS PERSONALES"),
                        MntlCCPABannerComponent.class), is(true));
        runner.driver().waitFor(1, TimeUnit.SECONDS);
        banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        runner.driver().waitFor(1, TimeUnit.SECONDS);
        banner.targetingCookiesCheckbox().jsClick();
        click(runner, mobile(runner.driver()) ? banner.confirmChoices() : banner.confirmChoices());
        refreshAndScroll(runner);
        banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        collector.checkThat("Bottom notification banner is displayed after opting out",
                banner.bottomBanner().isDisplayed(), is(false));
        readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        collector.checkThat("readyForThirdPartyTrackingEvent is present after opting out",
                readyForThirdPartyTrackingEvent.size(), is(0));
        collector.checkThat("adconsent=y after opting out",
                isConsentProvidedOrNot(runner, 'n'), is(true));

    };

    /* This test will check banner's absence in geolocations others than CA */
    protected Consumer<MntlRunner> CCPABannerComponentTestOutsideCA = (runner) -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        assertFalse("Notification banner is present outside CA", runner.page().componentExists(MntlCCPABannerComponent.class));
        List<Map<String, Object>> readyForThirdPartyTrackingEvents = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        collector.checkThat("readyForThirdPartyTrackingEvent is not present in geolocation other than CA",
                readyForThirdPartyTrackingEvents.size(), is(1));
    };

    /* This test will check banner's behavior when closing the banner without performing any action */
    protected Consumer<MntlRunner> CCPABannerComponentTestCloseBanner = (runner) -> {
        checkBannerPresence(runner);
        MntlCCPABannerComponent banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        List<Map<String, Object>> readyForThirdPartyTrackingEvent;
        checkForBannerDescriptionTrackingEventAndAdConsent(runner, banner);
        click(runner, banner.bannerClose());
        refreshAndScroll(runner);
        banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        collector.checkThat("Bottom notification banner is displayed after closing the banner and refreshing the page",
                banner.bottomBanner().isDisplayed(), is(false));
        readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        collector.checkThat("readyForThirdPartyTrackingEvent is not present after closing the banner and refreshing the page",
                readyForThirdPartyTrackingEvent.size(), is(1));
        collector.checkThat("adconsent=n after closing the banner and refreshing the page",
                isConsentProvidedOrNot(runner, 'y'), is(true));

    };

    /* This test will check banner's behavior when opting out the checkbox and closing the privacy center without confirming choices */
    protected Consumer<MntlRunner> CCPABannerComponentTestOptOutWithoutConfirming = (runner) -> {
        checkBannerPresence(runner);
        MntlCCPABannerComponent banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        List<Map<String, Object>> readyForThirdPartyTrackingEvent;
        checkForBannerDescriptionTrackingEventAndAdConsent(runner, banner);
        click(runner, banner.doNotSellInformation());
        runner.driver().waitFor(1, TimeUnit.SECONDS);
        banner.targetingCookiesCheckbox().jsClick();
        click(runner, banner.privacyCenterClose());
        refreshAndScroll(runner);
        banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        collector.checkThat("Bottom notification banner is not displayed after closing the privacy center and refreshing the page",
                banner.bottomBanner().isDisplayed(), is(true));
        readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        collector.checkThat("readyForThirdPartyTrackingEvent is not present after closing the privacy center and refreshing the page",
                readyForThirdPartyTrackingEvent.size(), is(1));
        collector.checkThat("adconsent=n after closing the privacy center and refreshing the page",
                isConsentProvidedOrNot(runner, 'y'), is(true));
    };

    /* This test will check banner's behavior when opting out the checkbox and confirming choices */
    protected Consumer<MntlRunner> CCPABannerComponentTestOptOutConfirmChoices = (runner) -> {
        checkBannerPresence(runner);
        MntlCCPABannerComponent banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        List<Map<String, Object>> readyForThirdPartyTrackingEvent;
        checkForBannerDescriptionTrackingEventAndAdConsent(runner, banner);
        click(runner, banner.doNotSellInformation());
        runner.driver().waitFor(1, TimeUnit.SECONDS);
        banner.targetingCookiesCheckbox().jsClick();
        click(runner, banner.confirmChoices());
        refreshAndScroll(runner);
        banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        collector.checkThat("Bottom notification banner is displayed after opting out",
                banner.bottomBanner().isDisplayed(), is(false));
        readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        collector.checkThat("readyForThirdPartyTrackingEvent is present after opting out",
                readyForThirdPartyTrackingEvent.size(), is(0));
        collector.checkThat("adconsent is still 'y' after opting out",
                isConsentProvidedOrNot(runner, 'n'), is(true));

    };

    /* This test will refresh the page and check if the banner is still visible */
    protected Consumer<MntlRunner> CCPABannerComponentTestBannerVisibilityAfterRefreshingPage = (runner) -> {
        checkBannerPresence(runner);
        MntlCCPABannerComponent banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        checkForBannerDescriptionTrackingEventAndAdConsent(runner, banner);
        refreshAndScroll(runner);
        banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        collector.checkThat("Bottom notification banner is not displayed after refreshing the page",
                banner.bottomBanner().isDisplayed(), is(true));
        checkForBannerDescriptionTrackingEventAndAdConsent(runner, banner);

    };

    /* This test will check banner's behavior when opting out, redirecting to a secondary url and checking the "adconsent=n" */
    protected BiConsumer<MntlRunner, String> CCPABannerTestOptOutWithSecondaryURL = (runner, secondaryURL) -> {
        checkBannerPresence(runner);
        MntlCCPABannerComponent banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        List<Map<String, Object>> readyForThirdPartyTrackingEvent;
        checkForBannerDescriptionTrackingEventAndAdConsent(runner, banner);
        click(runner, banner.doNotSellInformation());
        banner.targetingCookiesCheckbox().jsClick();
        click(runner, banner.confirmChoices());
        refreshAndScroll(runner);
        banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        collector.checkThat("Bottom notification banner is displayed after opting out",
                banner.bottomBanner().isDisplayed(), is(false));
        readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        collector.checkThat("readyForThirdPartyTrackingEvent is present after opting out",
                readyForThirdPartyTrackingEvent.size(), is(0));
        collector.checkThat("adconsent=y after opting out",
                isConsentProvidedOrNot(runner, 'n'), is(true));
        runner.driver().get(secondaryURL);
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        collector.checkThat("Bottom notification banner is displayed after opting out and redirecting to a secondary url",
                banner.bottomBanner().isDisplayed(), is(false));
        readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        collector.checkThat("readyForThirdPartyTrackingEvent is present after opting out and redirecting to a secondary url",
                readyForThirdPartyTrackingEvent.size(), is(0));
        collector.checkThat("adconsent is 'y' after opting out and redirecting to a secondary url",
                isConsentProvidedOrNot(runner, 'n'), is(true));

    };

    protected void checkBannerPresence(MntlRunner runner) {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        assertTrue("Notification banner is not present in the DOM", runner.page().componentExists(MntlCCPABannerComponent.class));
        MntlCCPABannerComponent banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
        collector.checkThat("Bottom notification banner is not displayed on the page", banner.bottomBanner().isDisplayed(), is(true));
        runner.driver().waitFor(1, TimeUnit.SECONDS);
    }

    protected void checkForBannerDescriptionTrackingEventAndAdConsent(MntlRunner runner, MntlCCPABannerComponent banner) {
        scrollToFooter(runner);
        collector.checkThat("Notification banner text is not as expected", banner.returnPolicyText(), is(bannerDescription()));
        List<Map<String, Object>> readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        collector.checkThat("readyForThirdPartyTrackingEvent is not present on page load",
                readyForThirdPartyTrackingEvent.size(), is(1));
        collector.checkThat("adconsent=n on page load", isConsentProvidedOrNot(runner, 'y'), is(true));
    }

}

