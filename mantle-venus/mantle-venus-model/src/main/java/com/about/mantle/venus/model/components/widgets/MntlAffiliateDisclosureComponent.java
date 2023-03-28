package com.about.mantle.venus.model.components.widgets;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-affiliate-disclosure")
public class MntlAffiliateDisclosureComponent extends MntlComponent {

	private final Lazy<WebElementEx> affiliateDisclosureLabel;
	private final Lazy<WebElementEx> affiliateDisclosureIcon;

	public MntlAffiliateDisclosureComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.affiliateDisclosureLabel = lazy(() -> findElement(By.cssSelector(".mntl-affiliate-disclosure__text")));
		this.affiliateDisclosureIcon = lazy(() -> findElement(By.cssSelector(".mntl-affiliate-disclosure__icon")));
	}

	public WebElementEx affiliateDisclosureLabel() {
		return affiliateDisclosureLabel.get();
	}
	
	public WebElementEx affiliateDisclosureIcon() {
		return affiliateDisclosureIcon.get();
	}

	
}
