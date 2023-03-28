package com.about.mantle.venus.model.analytics;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector(".mntl-notification-banner")
public class MntlNotificationBanner extends MntlComponent {

	private final Lazy<WebElementEx> bannerBtnClose;

	public MntlNotificationBanner(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.bannerBtnClose = lazy(() -> findElement(By.cssSelector(".js-banner-dismiss")));
	}

	@Validate
	public WebElementEx bannerClose() {
		return bannerBtnClose.get();
	}
}
