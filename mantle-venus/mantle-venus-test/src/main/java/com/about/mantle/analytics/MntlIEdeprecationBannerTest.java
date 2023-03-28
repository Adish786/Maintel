package com.about.mantle.analytics;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.analytics.MntlIEdeprecationBannerComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MntlIEdeprecationBannerTest extends MntlVenusTest implements MntlCommonTestMethods {

    protected Consumer<MntlRunner> ieDeprecationBannerAbsenceTest = (runner) -> {
        runner.driver().waitFor(1, TimeUnit.SECONDS);
        List<MntlIEdeprecationBannerComponent> IEdeprecationBanners = (List<MntlIEdeprecationBannerComponent>) new MntlBasePage<>(runner.driver(), MntlIEdeprecationBannerComponent.class).getComponents();
        assertThat("IE deprecation banner is displayed in Chrome", IEdeprecationBanners.size(), is(0));
    };

}
