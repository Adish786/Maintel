package com.about.mantle.components.ads;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.analytics.MntlCCPABannerComponent;
import com.about.mantle.venus.model.analytics.MntlGDPRBannerComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.mantle.venus.utils.UrlParams;
import com.about.venus.core.driver.proxy.VenusHarEntry;
import com.about.venus.core.driver.proxy.VenusHarRequest;
import com.about.venus.core.driver.proxy.VenusProxy;
import com.about.venus.core.test.annotations.TestDescription;
import com.about.venus.core.test.categoryTags.Ads;
import com.about.venus.core.test.categoryTags.Components;
import org.apache.logging.log4j.util.TriConsumer;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.about.venus.core.driver.selection.Device.Mobile_ChromeEmulator;
import static com.about.venus.core.driver.selection.Device.PC;
import static com.about.venus.core.driver.selection.DriverSelection.Matcher.devices;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;

public class MntlRTBTests extends MntlVenusTest implements MntlCommonTestMethods {

    protected final String prebidVersion = "v7.37.0";
    protected final String prebidLibrary = "prebid.js";
    protected final String lotameLibrary = "lt.min.js";
    protected final String amazonLibrary = "apstag.js";
    protected final String gpt_library = "securepubads.g.doubleclick.net/tag/js/gpt.js";
    protected final String criteo = "gum.criteo.com";
    protected final String lotame = "tags.crwdcntrl.net";
    protected final String rubicon = "fastlane.rubiconproject.com";
    protected final String index_exchange = "casalemedia.com";
    protected final String amazon = "amazon-adsystem.com";
    protected final String teads = "teads.tv";
    protected final String mediagrid = "grid.bidswitch.net";
    protected final String rubiconPG = "pg-prebid-server.rubiconproject.com/openrtb2/auction";
    protected final String liveRamp = "ats-wrapper.privacymanager.io/ats-modules/c6d292bf-6b43-45c2-87b3-2816a83f97d3/ats.js";

    protected TriConsumer<Runner, Boolean, String> gdprBidderHeaderTest = (runner, consent, bidderHeader) -> {
            MntlGDPRBannerComponent mntlGDPRBannerComponent = (MntlGDPRBannerComponent) runner.component();
            if (!consent) {
                mntlGDPRBannerComponent.bannerClose().jsClick();
                waitForBiddersToLoad(runner);
                collector.checkThat(bidderHeader + " is not set up properly", this.networkTabEntry(bidderHeader, runner).size(), adsServingPage(runner) ? equalTo(0) : equalTo(0));
            } else {
                mntlGDPRBannerComponent.acceptCookies().jsClick();
                if(bidderHeader.equals(teads)){
                    runner.page().slowScrollToBottom();
                }else {
                    waitForBiddersToLoad(runner);
                }
        }
    };

    protected BiConsumer<Runner, String> usBidderHeaderTest = (runner, bidderHeader) -> {
        if(bidderHeader.equals(teads) || bidderHeader.equals(liveRamp)){
            runner.page().slowScrollToBottom();
        }else {
            waitForBiddersToLoad(runner);
        }
        collector.checkThat(bidderHeader + " is not set up properly", this.networkTabEntry(bidderHeader, runner).size(), adsServingPage(runner) ? greaterThan(0) : equalTo(0));
    };

    protected BiConsumer<Runner, String> canadaBidderHeaderTest = (runner, bidderHeader) -> {
        if(bidderHeader.equals(teads)){
            runner.page().slowScrollToBottom();
        } else {
            waitForBiddersToLoad(runner);
            collector.checkThat(bidderHeader + " is not set up properly", this.networkTabEntry(bidderHeader, runner).size(), adsServingPage(runner) ? greaterThan(0) : equalTo(0));
        }
    };

    protected boolean gdprAndCaliforniaAreaWithAdConsent(Runner runner) {
        boolean consentToCookies = true;
        List<VenusHarEntry> calls = networkTabEntry("ads?", runner);
        VenusHarRequest request = calls.get(0).request();
        UrlParams gdprParam = new UrlParams(request);
        UrlParams.UrlParameters urlParameters = gdprParam.urlParam();
        if (urlParameters.prev_scp().contains("adconsent")) {
            consentToCookies = urlParameters.prev_scp().contains("adconsent=y") ? true : false;
        }
        return consentToCookies;
    }

    protected Consumer<Runner> GPTUrlValidation = runner -> {
        collector.checkThat("gpt library is not loading correctly", networkTabEntry(gpt_library, runner).size(), greaterThan(0));
    };

    protected Consumer<Runner> prebidLibraryLoadingOnCaliforniaPages = runner -> {
        runner.page().waitFor().aMoment(3, TimeUnit.SECONDS);
        collector.checkThat("prebid library is not loaded after consent was granted", networkTabEntry(prebidLibrary, runner).size(), adsServingPage(runner) ? greaterThan(0) : Matchers.is(0));
        MntlCCPABannerComponent mntlCCPABannerComponent = (MntlCCPABannerComponent) runner.component();
        mntlCCPABannerComponent.acceptCookies().jsClick();
        runner.page().waitFor().aMoment(2, TimeUnit.SECONDS);
        runner.loadUrl();
        runner.page().waitFor().aMoment(2, TimeUnit.SECONDS);
        collector.checkThat("prebid library is not loaded after consent was granted", networkTabEntry(prebidLibrary, runner).size(), adsServingPage(runner) ? greaterThan(0) : Matchers.is(0));
    };

    protected static List networkTabEntry(String filter, Runner runner) {
        List<VenusHarEntry> entries = runner.proxy().capture().page(runner.proxyPage()).entries(filter).stream()
                .collect(Collectors.toList());
        return entries;
    }

    protected boolean adsServingPage(Runner runner) {
        List<VenusHarEntry> calls = networkTabEntry("ads?", runner);
        return calls.size() > 0 ? true : false;
    }

    protected void waitForBiddersToLoad(Runner runner) {
        runner.page().waitFor().aMoment(7, TimeUnit.SECONDS);
        runner.page().scroll().bottom();
        runner.page().waitFor().aMoment(7, TimeUnit.SECONDS);
    }

    protected static VenusHarRequest callsList(MntlBasePage page, VenusProxy proxy, String proxyPage, int expectedEntries,
                                               String filter) {
        page.waitFor().entries(proxyPage, filter, expectedEntries, 5);
        List<VenusHarEntry> entries = proxy.capture().page(proxyPage).entries(filter).stream()
                .collect(Collectors.toList());
        return entries.get(expectedEntries - 1).request();
    }

    protected Consumer<Runner> prebidCriteoIdSystemValidation = runner -> {
        runner.page().scroll().bottom();
        runner.page().waitFor().aMoment(10,TimeUnit.SECONDS);
        String capturedVersion = runner.driver().executeScript("return window.pbjs.version");
        collector.checkThat("expected prebid version is " + prebidVersion + " but was " + capturedVersion, capturedVersion, is(prebidVersion));
        String criteoId = runner.driver().executeScript("return window.pbjs.getUserIds().criteoId");
        collector.checkThat("Criteo Id is null or empty", criteoId, not(emptyOrNullString()));
        List<String> criteoIdSystems = runner.driver().executeScript("return window.pbjs.installedModules");
        collector.checkThat("Criteo Id System is not found", criteoIdSystems, hasItem("criteoIdSystem"));
    };

    /**
     * This test validates adMetricsCalls with Query Params:
     * ?globeTest_orion=1&adMetricTracking=1100
     * ?globeTest_orion=1&adMetricTracking=0000
     * ?globeTest_orion=1&adMetricTracking=1000
     */
    protected BiConsumer<Runner,String> adMetricsCallValidation = (runner, queryParam) -> {
        runner.page().scroll().bottom();
        runner.page().waitFor().aMoment(7, TimeUnit.SECONDS);
        if(queryParam.contains("adMetricTracking=1100")) {
            collector.checkThat("Network tab response for 'pageData' not present", networkTabEntry("pageData", runner).size(), greaterThanOrEqualTo(1));
            collector.checkThat("Network tab response for 'slotData' not present", networkTabEntry("slotData", runner).size(), greaterThanOrEqualTo(1));
        }
        if(queryParam.contains("adMetricTracking=0000")) {
            collector.checkThat("Network tab response for 'pageData' present", networkTabEntry("pageData", runner).size(), is(0));
            collector.checkThat("Network tab response for 'slotData' present", networkTabEntry("slotData", runner).size(), is(0));
        }
        if(queryParam.contains("adMetricTracking=1000")) {
            collector.checkThat("Network tab response for 'pageData' not present", networkTabEntry("pageData", runner).size(), is(1));
            collector.checkThat("Network tab response for 'slotData' not present", networkTabEntry("slotData", runner).size(), is(1));
        }};
}
