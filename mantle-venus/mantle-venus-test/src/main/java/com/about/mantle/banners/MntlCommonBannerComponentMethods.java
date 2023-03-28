package com.about.mantle.banners;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.analytics.MntlCCPABannerComponent;

import com.about.mantle.venus.model.analytics.MntlGDPRBannerComponent;
import com.about.mantle.venus.model.components.global.MntlFooterComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.driver.proxy.VenusHarEntry;
import net.lightbody.bmp.core.har.HarNameValuePair;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Deprecated
public abstract class MntlCommonBannerComponentMethods<T extends MntlFooterComponent> extends MntlVenusTest implements MntlCommonTestMethods {

    protected abstract String bannerDescription();

    protected abstract Class<T> getFooter();

    protected String EVENT_NAME = "readyForThirdPartyTracking";

    /* This method with go through all ad calls and verify that adconsent equals passed value (either "n" or "y") */
    protected boolean isConsentProvidedOrNot(MntlRunner runner, char yesNo) {
        boolean adConsentProvided = false;
        runner.driver().waitFor(1, TimeUnit.SECONDS);
        List<VenusHarEntry> entries = runner.proxy().capture().page(runner.proxyPage()).entries("ads?").stream()
                .collect(Collectors.toList());
        for (VenusHarEntry entry : entries) {
            List<HarNameValuePair> list = entry.request().harRequest().getQueryString();
            ListIterator<HarNameValuePair> nvp = list.listIterator();
            while (nvp.hasNext()) {
                HarNameValuePair nvpair = nvp.next();
                String name = nvpair.getName();
                if (name.equals("prev_scp")) {
                    /* Checking if at least one entry contains desired value of "adconsent=" */
                    if (nvpair.getValue().contains("adconsent=" + yesNo)) {
                        adConsentProvided = true;
                        break;
                    } else {
                        adConsentProvided = false;
                    }
                }
            }
            if (adConsentProvided) break;
        }
        return adConsentProvided;
    }

    protected void click(MntlRunner runner, WebElementEx bannerButton) {
        runner.driver().waitFor(250, TimeUnit.MILLISECONDS);
        bannerButton.jsClick();
        runner.driver().waitFor(250, TimeUnit.MILLISECONDS);
    }

    protected void refreshAndScroll(MntlRunner runner) {
        runner.driver().waitFor(250, TimeUnit.MILLISECONDS);
        runner.driver().navigate().refresh();
        runner.driver().waitFor(500, TimeUnit.MILLISECONDS);
        runner.driver()
                .executeScript("$(document).ready(function(){ $(window).scrollTop(0); });");
        runner.driver().waitFor(500, TimeUnit.MILLISECONDS);
        scrollToFooter(runner);
        runner.driver().waitFor(500, TimeUnit.MILLISECONDS);
    }

    protected void scrollToFooter(MntlRunner runner) {
        MntlBasePage mntlScPage = new MntlBasePage<>(runner.driver(), getFooter());
        MntlFooterComponent footerComponent = (MntlFooterComponent) mntlScPage.getComponent();
        slowScroll(footerComponent, mntlScPage);
    }

    protected boolean isPrivacyCenterDisplayed(MntlRunner runner, List<String> privacyCenter, Class<T> componentClass) {
        boolean privacyCenterDisplayed = false;
        MntlBasePage mntlScPage = new MntlBasePage<>(runner.driver(), getFooter());
        MntlFooterComponent footerComponent = (MntlFooterComponent) mntlScPage.getComponent();
        for (WebElementEx link : footerComponent.links()) {
            if (privacyCenter.contains(link.getText().toUpperCase())) {
                link.scrollIntoViewCentered();
                link.jsClick();
                if (componentClass.getName().contains("MntlGDPRBannerComponent")) {
                    MntlGDPRBannerComponent banner = new MntlBasePage<>(runner.driver(), MntlGDPRBannerComponent.class).getComponent();
                    List<WebElementEx> privacyCenters = banner.privacyCenter();
                    if (privacyCenters.size() > 0) {
                        privacyCenterDisplayed = true;
                        break;
                    }
                } else if (componentClass.getName().contains("MntlCCPABannerComponent")) {
                    MntlCCPABannerComponent banner = new MntlBasePage<>(runner.driver(), MntlCCPABannerComponent.class).getComponent();
                    List<WebElementEx> privacyCenters = banner.privacyCenter();
                    if (privacyCenters.size() > 0) {
                        privacyCenterDisplayed = true;
                        break;
                    }
                }
            }
        }
        return privacyCenterDisplayed;
    }
}
