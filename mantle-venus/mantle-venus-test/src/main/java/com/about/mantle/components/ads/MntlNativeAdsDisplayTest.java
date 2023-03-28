package com.about.mantle.components.ads;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.ads.MntlNativeAdComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;

public class MntlNativeAdsDisplayTest extends MntlVenusTest implements MntlCommonTestMethods {

    protected Consumer<MntlRunner> nativeAdDisplayTest = (runner) -> {
        MntlBasePage<MntlNativeAdComponent> page = new MntlBasePage<>(runner.driver(), MntlNativeAdComponent.class);
        page.waitFor().exactMoment(2, TimeUnit.SECONDS);
        List<MntlNativeAdComponent> nativeAdComponent = page.getComponents();
        collector.checkThat("native ad component is not present ", nativeAdComponent.size(), is(not(0)));
        slowScroll(nativeAdComponent.get(0), page);
        collector.checkThat("native ad is not displayed ", nativeAdComponent.get(0).mntlNativeAdUnit().isDisplayed(), is(true));
    };

}