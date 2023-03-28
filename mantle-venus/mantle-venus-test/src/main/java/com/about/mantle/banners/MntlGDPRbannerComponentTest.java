package com.about.mantle.banners;

import com.about.mantle.venus.model.analytics.MntlGDPRBannerComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.utils.CookieUtils;
import com.about.venus.core.utils.DataLayerUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

@Deprecated
public abstract class MntlGDPRbannerComponentTest extends MntlCommonBannerComponentMethods {

    /*
    This test will check banner's behavior when confirming choices where targeting cookies are not checked
     */
    protected Consumer<MntlRunner> GDPRbannerTestAdsNoTargeting = (runner) -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        List<MntlGDPRBannerComponent> mntlGDPRBanners = (List<MntlGDPRBannerComponent>) new MntlBasePage<>(runner.driver(), MntlGDPRBannerComponent.class).getComponents();
        collector.checkThat("notification banner presence/absence is not as expected", mntlGDPRBanners.size(), is(1));
        if (mntlGDPRBanners.size() > 0) {
            MntlGDPRBannerComponent banner = (MntlGDPRBannerComponent) mntlGDPRBanners.get(0);
            runner.driver().waitFor(1, TimeUnit.SECONDS);
            collector.checkThat("notification banner text is not as expected", getPolicyText(banner), is(bannerDescription()));
            List<Map<String, Object>> readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
            collector.checkThat("readyForThirdPartyTracking event is present before giving a consent",
                    readyForThirdPartyTrackingEvent.size(), is(0));
            click(runner, banner.acceptCookies());
            checkForTrackingEvent(runner, banner);
            refreshAndScroll(runner);
            collector.checkThat("Banner reappeared after allowing consent and page refreshed or consent is not given",
                    isConsentProvidedOrNot(runner, 'y'), is(true));
            collector.checkThat("EU Privacy footer link is not clickable or the Privacy center did not appear",
                    isPrivacyCenterDisplayed(runner, Arrays.asList("EU PRIVACY", "EU PRIVACIDAD"), MntlGDPRBannerComponent.class), is(true));
            runner.driver().waitFor(1, TimeUnit.SECONDS);
            banner = (MntlGDPRBannerComponent) new MntlBasePage<>(runner.driver(), MntlGDPRBannerComponent.class).getComponent();
            runner.driver().waitFor(1, TimeUnit.SECONDS);
            banner.targetingCookiesRow().click();
            banner.targetingCookiesCheckbox().jsClick();
            click(runner, mobile(runner.driver()) ? banner.confirmChoicesButton() : banner.confirmChoicesButton());
            refreshAndScroll(runner);
            collector.checkThat("adconsent value is still 'y'", isConsentProvidedOrNot(runner, 'n'), is(true));
        }
    };

    /*
    This test will check banner's behavior when allowing all cookies. It will take `secondaryURL` as a parameter
    to redirect to another page after accepting cookies to check whether `adconsent=y` still.
     */
    protected BiConsumer<MntlRunner, String> GDPRbannerTestAllowAll = (runner, secondaryURL) -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        List<MntlGDPRBannerComponent> mntlGDPRBanners = (List<MntlGDPRBannerComponent>) new MntlBasePage<>(runner.driver(), MntlGDPRBannerComponent.class).getComponents();
        collector.checkThat("notification banner presence/absence is not as expected", mntlGDPRBanners.size(), is(1));
        collector.checkThat("pc cookie value exist before accept cookies", CookieUtils.cookieValue(runner.driver(), "pc"), is(nullValue()));
        if (mntlGDPRBanners.size() > 0) {
            MntlGDPRBannerComponent banner = (MntlGDPRBannerComponent) mntlGDPRBanners.get(0);
            runner.driver().waitFor(1, TimeUnit.SECONDS);
            collector.checkThat("notification banner text is not as expected", getPolicyText(banner), is(bannerDescription()));
            List<Map<String, Object>> readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
            collector.checkThat("readyForThirdPartyTracking event is present before giving a consent",
                    readyForThirdPartyTrackingEvent.size(), is(0));
            click(runner, banner.acceptCookies());
            checkForTrackingEvent(runner, banner);
            refreshAndScroll(runner);
            collector.checkThat("Banner reappeared after allowing consent and page refreshed or consent is not given",
                    isConsentProvidedOrNot(runner, 'y'), is(true));
            collector.checkThat("pc cookie value is not correct after refresh", CookieUtils.pc(runner.driver()), is(2));
            runner.driver().get(secondaryURL + "?globeTest_cmp=1");
            runner.driver().waitFor(1, TimeUnit.SECONDS);
            collector.checkThat("Banner reappeared after allowing consent and page refreshed or consent is not given",
                    isConsentProvidedOrNot(runner, 'y'), is(true));
        }
    };

    /*
    This test will check banner's absence when in the US.
    */
    protected Consumer<MntlRunner> GDPRbannerComponentTestUS = (runner) -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        List<MntlGDPRBannerComponent> mntlGDPRBanners = (List<MntlGDPRBannerComponent>) new MntlBasePage<>(runner.driver(), MntlGDPRBannerComponent.class).getComponents();
        // Using value of proxy as a flag to determine if we are expecting banner or not.
        collector.checkThat("notification banner presence/absence is not as expected",
                mntlGDPRBanners.size(), is(0));
        List<Map<String, Object>> readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        collector.checkThat("readyForThirdPartyTracking event is not present",
                readyForThirdPartyTrackingEvent.size(), is(1));
        runner.driver().waitFor(1, TimeUnit.SECONDS);
    };

    /*
    This method will first check if `readyForThirdPartyTracking` event is present in datalayer after
    accepting cookies. Then it will disable and re-enable `Store and/or access
    information on a device' and check if multiple `readyForThirdPartyTracking` events are not fired.
    */
    protected void checkForTrackingEvent(MntlRunner runner, MntlGDPRBannerComponent banner) {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        List<Map<String, Object>> readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        runner.driver().waitFor(250, TimeUnit.MILLISECONDS);
        collector.checkThat("readyForThirdPartyTracking event is not present after giving a consent",
                readyForThirdPartyTrackingEvent.size(), is(1));
        runner.driver().waitFor(250, TimeUnit.MILLISECONDS);
        isPrivacyCenterDisplayed(runner, Arrays.asList("EU PRIVACY", "EU PRIVACIDAD"), MntlGDPRBannerComponent.class);
        banner = (MntlGDPRBannerComponent) new MntlBasePage<>(runner.driver(), MntlGDPRBannerComponent.class).getComponent();
        runner.driver().waitFor(250, TimeUnit.MILLISECONDS);
        banner.storingInfoRow().click();
        banner.storingInfoCheckbox().jsClick();
        runner.driver().waitFor(250, TimeUnit.MILLISECONDS);
        readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        collector.checkThat("readyForThirdPartyTracking event fired more than once",
                readyForThirdPartyTrackingEvent.size(), is(1));
        runner.driver().waitFor(250, TimeUnit.MILLISECONDS);
        banner.storingInfoCheckbox().jsClick();
        runner.driver().waitFor(250, TimeUnit.MILLISECONDS);
        readyForThirdPartyTrackingEvent = DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME);
        collector.checkThat("readyForThirdPartyTracking event fired more than once",
                readyForThirdPartyTrackingEvent.size(), is(1));
    }

    public String getPolicyText(MntlGDPRBannerComponent banner) {
        String policyText = banner.returnPolicyText() + banner.returnPolicySecondText();
        return policyText;
    }

}
