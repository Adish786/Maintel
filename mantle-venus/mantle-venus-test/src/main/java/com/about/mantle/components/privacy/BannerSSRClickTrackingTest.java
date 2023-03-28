package com.about.mantle.components.privacy;

import com.about.mantle.clickTracking.MntlClickTrackingTest;
import com.about.mantle.venus.model.components.privacy.OneTrustBanner;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.driver.WebElementEx;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class BannerSSRClickTrackingTest extends MntlClickTrackingTest {

    public Consumer<MntlRunner> gdprAcceptCookiesClickTest = runner -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        OneTrustBanner banner = new MntlBasePage<>(runner.driver(), OneTrustBanner.class).getComponent();

        WebElementEx acceptButton = banner.acceptCookiesBtn();
        acceptButton.scrollIntoView();
        TrackingData data = new TrackingData()
                .action(acceptButton.text())
                .label("No Target URL")
                .category("onetrust-consent-sdk Click");
        setLinkTarget(runner.driver(), acceptButton);
        testLinkClickDLEventCategory(data.category, 0, getMap(data), runner.driver(), acceptButton, collector);
    };

    public Consumer<MntlRunner> gdprRejectAllCookiesClickTest = runner -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        OneTrustBanner banner = new MntlBasePage<>(runner.driver(), OneTrustBanner.class).getComponent();

        WebElementEx rejectAllButton = banner.rejectAllBtn();
        rejectAllButton.scrollIntoView();
        TrackingData data = new TrackingData()
                .action(rejectAllButton.text())
                .label("No Target URL")
                .category("onetrust-consent-sdk Click");
        setLinkTarget(runner.driver(), rejectAllButton);
        testLinkClickDLEventCategory(data.category, 0, getMap(data), runner.driver(), rejectAllButton, collector);
    };

    public Consumer<MntlRunner> gdprCookiesSettingsClickTest = runner -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        OneTrustBanner banner = new MntlBasePage<>(runner.driver(), OneTrustBanner.class).getComponent();

        WebElementEx cookieSettingsButton = banner.cookiesSettingsBtn();
        cookieSettingsButton.scrollIntoView();
        TrackingData data = new TrackingData()
                .action(cookieSettingsButton.text())
                .label("No Target URL")
                .category("onetrust-consent-sdk Click");
        setLinkTarget(runner.driver(), cookieSettingsButton);
        testLinkClickDLEventCategory(data.category, 0, getMap(data), runner.driver(), cookieSettingsButton, collector);
    };

    public Consumer<MntlRunner> gdprCloseButtonClickTest = runner -> {
        runner.driver().waitFor(2, TimeUnit.SECONDS);
        OneTrustBanner banner = new MntlBasePage<>(runner.driver(), OneTrustBanner.class).getComponent();

        WebElementEx closeButton = banner.closeBannerBtn();
        TrackingData data = new TrackingData()
                .action(closeButton.getAttribute("aria-label"))
                .label("No Target URL")
                .category("onetrust-consent-sdk Click");
        setLinkTarget(runner.driver(), closeButton);
        testLinkClickDLEventCategory(data.category, 0, getMap(data), runner.driver(), closeButton, collector);
    };
}