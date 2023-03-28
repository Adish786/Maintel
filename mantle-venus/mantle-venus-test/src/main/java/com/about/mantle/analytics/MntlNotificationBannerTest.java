package com.about.mantle.analytics;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.analytics.MntlNotificationBanner;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.mantle.venus.utils.GeoIps;

import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;

public abstract class MntlNotificationBannerTest<T extends MntlNotificationBanner> extends MntlVenusTest
		implements MntlCommonTestMethods {
	protected Consumer<MntlRunner> mntlBannerTest = runner -> {
		List<T> banners = (List<T>) new MntlBasePage<>(runner.driver(), getBannerComp()).getComponents();
		runner.page().waitFor().aMoment();

		/*
		 * note here we are using value of proxy as flag to determine if we are
		 * expecting banner or not. since banner is shown when we specific region is
		 * added to proxy, null value for proxy is used to determine the case of
		 * absence. This is just to avoid another test method.
		 */
		collector.checkThat("banner presence/absence is not as expected", banners.size(),
				is(runner.proxy() == null ? 0 : 1));
	};

	protected abstract String url();

	public abstract Class<T> getBannerComp();
	
	public abstract GeoIps  getGeoIps();

}