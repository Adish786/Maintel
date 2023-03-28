package com.about.mantle.clickTracking;

import com.about.mantle.venus.model.analytics.MntlCCPABannerComponent;
import com.about.mantle.venus.model.analytics.MntlGDPRBannerComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.driver.WebElementEx;
import com.google.common.collect.ImmutableMap;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/* This test class can be deleted once all the verticals have rolled out to the OT banners with SSR */
@Deprecated
public class MntlBannerClickTrackingTest extends MntlClickTrackingTest {

    public Consumer<Runner> ccpaAcceptCookiesClickTest = runner -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        MntlCCPABannerComponent banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();

        WebElementEx acceptButton = banner.acceptCookies();
        TrackingData data = new TrackingData()
                .action(acceptButton.text())
                .label("No Target URL")
                .category("onetrust-banner-sdk Click");
        setLinkTarget(runner.driver(), acceptButton);
        testLinkClickDLEventCategory(data.category, 0, getMap(data), runner.driver(), acceptButton, collector);
    };

    public Consumer<Runner> ccpaDoNotSellPersonalInformationClickTest = runner -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        MntlCCPABannerComponent banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();

        WebElementEx doNotSellPersonalInfoButton = banner.doNotSellInformation();
        TrackingData data = new TrackingData()
                .action(doNotSellPersonalInfoButton.text())
                .label("No Target URL")
                .category("onetrust-banner-sdk Click");
        setLinkTarget(runner.driver(), doNotSellPersonalInfoButton);
        testLinkClickDLEventCategory(data.category, 0, getMap(data), runner.driver(), doNotSellPersonalInfoButton, collector);
    };

    public Consumer<Runner> ccpaCloseButtonClickTest = runner -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        MntlCCPABannerComponent banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();

        WebElementEx closeButton = banner.bannerClose();
        TrackingData data = new TrackingData()
                .action(closeButton.getAttribute("aria-label"))
                .label("No Target URL")
                .category("onetrust-banner-sdk Click");
        setLinkTarget(runner.driver(), closeButton);
        testLinkClickDLEventCategory(data.category, 0, getMap(data), runner.driver(), closeButton, collector);
    };

    public Consumer<Runner> gdprAcceptCookiesClickTest = runner -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        MntlGDPRBannerComponent banner = new MntlBasePage<>(runner.driver(), MntlGDPRBannerComponent.class).getComponent();

        WebElementEx acceptButton = banner.acceptCookies();
        acceptButton.scrollIntoView();
        TrackingData data = new TrackingData()
                .action(acceptButton.text())
                .label("No Target URL")
                .category("onetrust-banner-sdk Click");
        setLinkTarget(runner.driver(), acceptButton);
        testLinkClickDLEventCategory(data.category, 0, getMap(data), runner.driver(), acceptButton, collector);
    };

    public Consumer<Runner> gdprShowPurposesClickTest = runner -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        MntlGDPRBannerComponent banner = new MntlBasePage<>(runner.driver(), MntlGDPRBannerComponent.class).getComponent();

        WebElementEx showPurposesButton = banner.showPurposes();
        showPurposesButton.scrollIntoView();
        TrackingData data = new TrackingData()
                .action(showPurposesButton.text())
                .label("No Target URL")
                .category("onetrust-banner-sdk Click");
        setLinkTarget(runner.driver(), showPurposesButton);
        testLinkClickDLEventCategory(data.category, 0, getMap(data), runner.driver(), showPurposesButton, collector);
    };

    public Consumer<Runner> gdprCloseButtonClickTest = runner -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        MntlGDPRBannerComponent banner = new MntlBasePage<>(runner.driver(), MntlGDPRBannerComponent.class).getComponent();

        WebElementEx closeButton = banner.bannerClose();
        TrackingData data = new TrackingData()
                .action(closeButton.getAttribute("aria-label"))
                .label("No Target URL")
                .category("onetrust-banner-sdk Click");
        setLinkTarget(runner.driver(), closeButton);
        testLinkClickDLEventCategory(data.category, 0, getMap(data), runner.driver(), closeButton, collector);
    };
}

