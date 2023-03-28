package com.about.mantle.components.ads;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.components.ads.AdCallObject;
import com.about.mantle.venus.model.components.ads.MntlLeaderboardDeferredFooterComponent;
import com.about.mantle.venus.utils.UrlParams;
import com.about.venus.core.driver.selection.Device;
import com.google.common.base.Predicate;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;

@SuppressWarnings({"rawtypes","unchecked"})
public class MntlLeaderboardFooterTest extends MntlVenusTest implements MntlCommonTestMethods {
	
	protected String url;
	protected Map<Device, List<Map<String, List<String>>>> leaderboardFooter1;
	protected Map<Device, List<Map<String, List<String>>>> leaderboardFooter2;
	protected Map<Device, List<Map<String, List<String>>>> leaderboardPostContent;
	protected Class<? extends MntlLeaderboardDeferredFooterComponent> leaderboardFooter1Component;
	protected Class<? extends MntlLeaderboardDeferredFooterComponent> leaderboardFooter2Component;
	protected Class<? extends MntlLeaderboardDeferredFooterComponent> leaderboardPostContentComponent;
	
	public MntlLeaderboardFooterTest(String url, Map<Device, List<Map<String, List<String>>>> leaderboardFooter1, Map<Device, List<Map<String, List<String>>>> leaderboardFooter2, Map<Device, List<Map<String, List<String>>>> leaderboardPostContent,
			Class<? extends MntlLeaderboardDeferredFooterComponent> leaderboardFooter1Component, Class<? extends MntlLeaderboardDeferredFooterComponent> leaderboardFooter2Component,
			Class<? extends MntlLeaderboardDeferredFooterComponent> leaderboardPostContentComponent) {
		this.url = url;
		this.leaderboardFooter1 = leaderboardFooter1;
		this.leaderboardFooter2 = leaderboardFooter2;
		this.leaderboardPostContent = leaderboardPostContent;
		this.leaderboardFooter1Component = leaderboardFooter1Component;
		this.leaderboardFooter2Component = leaderboardFooter2Component;
		this.leaderboardPostContentComponent = leaderboardPostContentComponent;
	}

	protected Consumer<MntlRunner> deferredFooterLeaderboardTest = runner -> {
		Class<? extends MntlComponent> componentClass = runner.componentClass();
		if(componentClass.getName().endsWith("LeaderboardPostContentComponent")) {
			if(tablet(runner.driver()))
				return;
		}
		MntlLeaderboardDeferredFooterComponent deferredFooterLeaderboard = (MntlLeaderboardDeferredFooterComponent) runner.component();
		int scroll = deferredFooterLeaderboard.location().getY();
		Long currentAfterScroll = runner.page().scroll().currentPosition();
		Long currentBeforeScroll = (long) Double.NEGATIVE_INFINITY;
		while(currentAfterScroll < scroll) {
			if(currentBeforeScroll.equals(currentAfterScroll))
				break;
			currentBeforeScroll = runner.page().scroll().currentPosition();
			runner.page().scroll().scrollBy(0, 100);
			sleep(500, TimeUnit.MILLISECONDS);
			currentAfterScroll = runner.page().scroll().currentPosition();
		}	
		runner.driver().waitFor((Predicate<WebDriver>) wd -> deferredFooterLeaderboard.googelAdsFrame().isDisplayed(), 30);
		collector.checkThat("footer leaderboard is not displayed", deferredFooterLeaderboard.googelAdsFrame().isDisplayed(), is(true));
		for(Map<String, List<String>> values : runner.values()) {
			UrlParams adCallParams = new AdCallObject(runner.proxy(), runner.proxyPage(), runner.driver(), values, "leaderboard").urlParams(collector);
			testAdCallParams(runner,adCallParams, values, collector);
		}
	};

}
