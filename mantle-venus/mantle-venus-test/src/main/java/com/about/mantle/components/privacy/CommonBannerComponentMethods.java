package com.about.mantle.components.privacy;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.components.global.MntlFooterComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.driver.proxy.VenusHarEntry;
import com.about.venus.core.utils.CookieUtils;
import com.about.venus.core.utils.DataLayerUtils;
import com.about.venus.core.utils.Wait;
import com.google.common.base.Predicate;
import net.lightbody.bmp.core.har.HarNameValuePair;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class CommonBannerComponentMethods<T extends MntlFooterComponent> extends MntlVenusTest implements MntlCommonTestMethods {

    protected String EVENT_NAME = "readyForThirdPartyTracking";
    protected static String STRICTLYNECESSARYCOOKIES = "STRICTLYNECESSARYCOOKIES";
    protected static String PERFORMANCECOOKIES = "PERFORMANCECOOKIES";
    protected static String FUNCTIONALCOOKIES = "FUNCTIONALCOOKIES";
    protected static String TARGETINGCOOKIES = "TARGETINGCOOKIES";
    protected static String SOCIALMEDIACOOKIES = "SOCIALMEDIACOOKIES";
    protected static String EXTRATARGETINGCOOKIESCCPA = "EXTRATARGETINGCOOKIESCCPA";
    protected static String BG = "BG82";

    public CommonBannerComponentMethods() {
    }

    protected abstract String bannerDescription();

    protected abstract Class<T> getFooter();

    /* This method with go through all ad calls and verify that adconsent equals passed value (either "n" or "y") */
    protected boolean isConsentProvidedOrNot(MntlRunner runner, char yesNo) {
        boolean adConsentProvided = false;
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
        bannerButton.jsClick();
    }

    protected void refreshAndScroll(MntlRunner runner) {
        runner.driver().waitFor(250, TimeUnit.MILLISECONDS);
        runner.driver().navigate().refresh();
        runner.driver().waitFor(500, TimeUnit.MILLISECONDS);
        runner.driver().executeScript("$(document).ready(function(){ $(window).scrollTop(0); });");
        runner.driver().waitFor(500, TimeUnit.MILLISECONDS);
        this.scrollToFooter(runner);
        runner.driver().waitFor(500, TimeUnit.MILLISECONDS);
    }

    protected void scrollToFooter(MntlRunner runner) {
        MntlBasePage ScPage = new MntlBasePage(runner.driver(), this.getFooter());
        MntlFooterComponent footerComponent = (MntlFooterComponent) ScPage.getComponent();
        slowScroll(footerComponent, ScPage,500,0);
        runner.driver().executeScript("$(window).on('load', function(){});");
    }

    protected boolean isPrivacyCenterDisplayed(MntlRunner runner, List<String> privacyCenter, Class<T> componentClass) {
        boolean privacyCenterDisplayed = false;
        MntlBasePage mntlScPage = new MntlBasePage<>(runner.driver(), getFooter());
        MntlFooterComponent footerComponent = (MntlFooterComponent) mntlScPage.getComponent();

        for (WebElementEx link : footerComponent.links()) {
            if (privacyCenter.contains(link.getText().toUpperCase())) {
                link.scrollIntoViewCentered();
                link.jsClick();
                MntlComponent modal = (new MntlBasePage(runner.driver(), componentClass)).getComponent();
                modal.waitFor().visibility();
                privacyCenterDisplayed = modal.displayed();
            }
        }
        return privacyCenterDisplayed;
    }

    protected String getOptanonConsentCookieValue(MntlRunner runner, String cookieName) {
        String cookieValue = null;
        String cookieString = URLDecoder.decode(CookieUtils.cookieValue(runner.driver(), "OptanonConsent"), StandardCharsets.UTF_8).replace("hosts=&", "");
        Map<String, String> cookies = Arrays.stream(cookieString.split("&")).map((c) -> {
            return c.split("=");
        }).collect(Collectors.toMap((c) -> {
            return c[0].trim();
        }, (c) -> {
            return c[1].trim();
        }));
        cookieValue = cookies.get(cookieName);
        return cookieValue;
    }

    private Map<String, String> getCookieStatusMap(MntlRunner runner) {
        Map<String, String> cookies = Arrays.stream(this.getOptanonConsentCookieValue(runner, "groups").split(",")).map((c) -> {
            return c.split(":");
        }).collect(Collectors.toMap((c) -> {
            if (c[0].trim().equals("1")) {
                return c[0].replace("1", STRICTLYNECESSARYCOOKIES);
            }
            if (c[0].trim().equals("2")) {
                return c[0].replace("2", PERFORMANCECOOKIES);
            }
            if (c[0].trim().equals("3")) {
                return c[0].replace("3", FUNCTIONALCOOKIES);
            }
            if (c[0].trim().equals("4")) {
                return c[0].replace("4", TARGETINGCOOKIES);
            }
            if (c[0].trim().equals("5")) {
                return c[0].replace("5", SOCIALMEDIACOOKIES);
            }
            if (c[0].trim().equals(BG)) {
                return c[0].replace(BG, EXTRATARGETINGCOOKIESCCPA);
            } else return c[0].trim();
        }, (c) -> {
            if (c[1].trim().equals("0")) {
                return c[1].replace("0", "rejected");
            }
            if (c[1].trim().equals("1")) {
                return c[1].replace("1", "accepted");
            } else return c[1].trim();
        }));
        return cookies;
    }

    protected boolean allCookiesAccepted(MntlRunner runner) {
        return this.getCookieStatusMap(runner).values().stream().distinct().count() == 1 &&
                this.getCookieStatusMap(runner).values().stream().distinct().findFirst().get().equals("accepted");
    }

    protected boolean allCookiesRejected(MntlRunner runner) {
        Map<String, String> cookies = getCookieStatusMap(runner);
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            if (cookie.getKey().equals(STRICTLYNECESSARYCOOKIES)) {
                if (cookie.getValue().equals("accepted")) continue;
                else return false;
            } else if (!cookie.getValue().equals("rejected")) return false;
        }
        return true;
    }

    protected boolean isCookieAccepted(MntlRunner runner, String cookieName) {
        return this.getCookieStatusMap(runner).get(cookieName).equals("accepted");
    }

    protected void waitForDLEvent(MntlRunner runner) {
        runner.driver().waitFor((Predicate<WebDriver>) wd -> DataLayerUtils.dataLayerEvents(runner.driver(), EVENT_NAME).size() == 1, 5);
    }
}