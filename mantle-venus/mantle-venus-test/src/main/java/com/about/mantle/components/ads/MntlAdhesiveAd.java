package com.about.mantle.components.ads;


import com.about.mantle.venus.model.components.ads.MntlAdhesiveComponent;
import com.about.mantle.venus.utils.UrlParams;
import com.about.venus.core.driver.proxy.VenusHarEntry;
import com.about.venus.core.driver.proxy.VenusHarRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;

public class MntlAdhesiveAd extends MntlRTBTests {

    protected Consumer<Runner> adhesiveAdCallTest = runner -> {
        runner.page().scroll().scrollBy(10,10);
        runner.page().waitForComponent(MntlAdhesiveComponent.class, 5);
        List<VenusHarEntry> calls = filterForAds(runner, "adhesive");
        VenusHarRequest request = calls.get(0).request();
        UrlParams adhesiveParam = new UrlParams(request);
        UrlParams.UrlParameters urlParameters = adhesiveParam.urlParam();
        collector.checkThat("adhesive ads didn't load after small scroll ", filterForAds(runner, "adhesive").size() == 1, is(true));
        runner.page().waitFor().exactMoment(31,TimeUnit.SECONDS);
        collector.checkThat("adhesive ads didn't refresh ", filterForAds(runner, "adhesive").size() == 2, is(true));
    };

    protected static List<VenusHarEntry> filterForAds(Runner runner, String secondFilter){
        return runner.proxy().capture().page(runner.proxyPage()).entries("ads?").stream().filter((entry) -> entry.request().url().url().contains(secondFilter))
                .collect(Collectors.toList());
    }

}
