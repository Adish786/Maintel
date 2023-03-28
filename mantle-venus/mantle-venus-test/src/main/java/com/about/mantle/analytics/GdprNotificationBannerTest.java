package com.about.mantle.analytics;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.analytics.MntlGdprNotificationBanner;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.utils.DataLayerUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.fail;

public abstract class GdprNotificationBannerTest <T extends MntlGdprNotificationBanner> extends MntlVenusTest
		implements MntlCommonTestMethods {
	
	protected Consumer<MntlRunner> notificationBannerTest = runner -> {
		List<T> mntlGdprNotificationBanners = (List<T>) new MntlBasePage<>(runner.driver(), getBannerComp())
				.getComponents();
		// note here we are using value of proxy as flag to determine if
		// we are expecting banner or not. since banner is shown when we EU cipa
		// is added to proxy, null value for proxy is used to determine the
		// case of absence. This is just to avoid another test method.
		collector.checkThat("notification banner presence/absence is not as expected",
				mntlGdprNotificationBanners.size(), is(runner.proxy() == null ? 0 : 1));
		if (mntlGdprNotificationBanners.size() > 0) {
			T banner = (T) mntlGdprNotificationBanners.get(0);
			collector.checkThat("notification banner text is incorrect", banner.getBannerText(), is(bannerText()));
			collector.checkThat("the policy link href in the banner is incorrect", banner.getBannerLink().href(),
					endsWith(linkSlug()));
			collector.checkThat("the policy link text in the banner is incorrect", banner.getBannerLink().text(),
					is(linkText()));
			banner.bannerClose().click();
			runner.page().waitFor().aMoment();
			collector.checkThat("the banner is not closed", banner.attributeValue("class"),
					containsString("is-closed"));
			collector.checkThat("the banner is still visible", banner.isHidden(), is(true));
			runner.driver().navigate().refresh();
			List<T> mntlGdprNotificationBannersAfterRefresh = (List<T>) new MntlBasePage<>(runner.driver(),
					getBannerComp()).getComponents();
			collector.checkThat("notification banner is present", mntlGdprNotificationBannersAfterRefresh.size(),
					is(0));

		} 
	};
	
	protected Consumer<MntlRunner> dataLayerTest = runner -> {
		List<T> mntlGdprNotificationBanners = (List<T>) new MntlBasePage<>(runner.driver(), getBannerComp())
				.getComponents();
		final Map<String, Object> unifiedPageviewObjectsFirst = DataLayerUtils.dataLayerEvents(runner.driver(),
				"unifiedPageview").get(0);
		// note here we are using value of proxy as flag to determine if
		// we are expecting banner or not. since banner is shown when we EU cipa
		// is added to proxy, null value for proxy is used to determine the
		// case of absence. This is just to avoid another test method.
		collector.checkThat("notification banner presence/absence is not as expected",
				mntlGdprNotificationBanners.size(), is(runner.proxy() == null ? 0 : 1));
		if (mntlGdprNotificationBanners.size() > 0)
			collector.checkThat("euTrafficFlag value is true", unifiedPageviewObjectsFirst.get("euTrafficFlag"), is(true));
		 else collector.checkThat("euTrafficFlag value is false", unifiedPageviewObjectsFirst.get("euTrafficFlag"), is(false));
	};

	protected abstract String linkSlug() ;

	protected abstract String url() ;

	protected abstract String bannerText() ;

	protected abstract String linkText() ;

	public abstract Class<T> getBannerComp() ;
	
	protected void validateTestParameters() {
		if (StringUtils.isEmpty(url()) || StringUtils.isEmpty(bannerText()) || StringUtils.isEmpty(linkText())
				|| getBannerComp() == null) {
			fail("please check all the values are provided.Required values are for url , the banner text,linkText and banner component");
		}
	}
}
