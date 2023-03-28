package com.about.mantle.venus.model.components.ads;

import org.openqa.selenium.By;

import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

public class MntlLeaderboardDeferredFooterComponent extends MntlAdComponent {
	
	private final Lazy<WebElementEx> googelAdsFrame;

	public MntlLeaderboardDeferredFooterComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.googelAdsFrame = lazy(() -> findElement(By.cssSelector("[id^=google_ads_iframe_]")));
	}

	public WebElementEx googelAdsFrame() {
		return googelAdsFrame.get();
	}
}
