package com.about.mantle.components.ads;

import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.ads.MntlAdhesiveComponent;
import com.about.mantle.venus.model.components.ads.MntlLeaderboardFixedZeroComponent;
import com.about.mantle.venus.utils.UrlParams;
import com.about.venus.core.driver.proxy.VenusHarEntry;
import com.about.venus.core.driver.proxy.VenusHarRequest;
import com.about.venus.core.test.annotations.TestDescription;
import com.about.venus.core.test.categoryTags.Ads;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.about.venus.core.driver.selection.Device.Any;
import static com.about.venus.core.driver.selection.DriverSelection.Matcher.devices;
import static org.hamcrest.CoreMatchers.is;

public class MntlLeaderboardHeaderTest extends MntlRTBTests {

    protected Consumer<Runner> leaderboardZeroRefresh = runner -> {
        runner.page().waitFor().exactMoment(10, TimeUnit.SECONDS);
        collector.checkThat("adhesive ads didn't load after small scroll ", filterForAds(runner, "leaderboard-fixed-0").size() == 1, is(true));
        runner.page().waitFor().exactMoment(30, TimeUnit.SECONDS);
        collector.checkThat("adhesive ads didn't refresh ", filterForAds(runner, "leaderboard-fixed-0").size() == 2, is(true));
    };

    protected static List<VenusHarEntry> filterForAds(Runner runner, String secondFilter){
        return runner.proxy().capture().page(runner.proxyPage()).entries("ads?").stream().filter((entry) -> entry.request().url().url().contains(secondFilter))
                .collect(Collectors.toList());
    }


}
