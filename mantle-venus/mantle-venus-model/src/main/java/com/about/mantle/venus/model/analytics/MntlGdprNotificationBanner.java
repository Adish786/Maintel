package com.about.mantle.venus.model.analytics;

import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;
public class MntlGdprNotificationBanner extends MntlNotificationBanner {

	private final Lazy<WebElementEx> gdprBannerTextElement;
	private final Lazy<WebElementEx> gdprBannerLinkElement;
	public MntlGdprNotificationBanner(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.gdprBannerTextElement = lazy(() -> findElement(By.className("gdpr-notification-banner__text")));
		this.gdprBannerLinkElement = lazy(() -> findElement(By.className("gdpr-notification-banner__link")));
	}

	public String getBannerText() {
		return gdprBannerTextElement.get().getText();
	}
	
	public WebElementEx getBannerLink() {
		return gdprBannerLinkElement.get();
	}

	public boolean isHidden() {
		return this.cssValue("transform").equals("matrix(1, 0, 0, 1, 0, " + this.height() + ")");
	}
}
