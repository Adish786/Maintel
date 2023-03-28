package com.about.mantle.venus.model.components.structuredContent;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-sc-block-location")
public class MntlScBlockLocation extends MntlComponent {
	private final Lazy<WebElementEx> addressLabel;
	private final Lazy<WebElementEx> fullAddress;
	private final Lazy<WebElementEx> directions;
	private final Lazy<WebElementEx> phone;
	private final Lazy<WebElementEx> phoneLabel;
	private final Lazy<WebElementEx> phoneText;

	private final Lazy<WebElementEx> website;
	private final Lazy<WebElementEx> websiteLabel;
	private final Lazy<WebElementEx> websiteLink;

	public MntlScBlockLocation(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.addressLabel = lazy(() -> findElement(By.className("mntl-sc-block-location__address-label")));
		this.fullAddress = lazy(() -> findElement(By.tagName("address")));
		this.directions = lazy(() -> findElement(By.className("mntl-sc-block-location__url")));
		this.phone = lazy(() -> findElement(By.className("mntl-sc-block-location__phone")));
		this.phoneLabel = lazy(() -> phone().findElementEx(By.className("mntl-sc-block-location__phone-label")));
		this.phoneText = lazy(() -> phone().findElementEx(By.className("mntl-sc-block-location__phone-text")));

		this.website = lazy(() -> findElement(By.className("mntl-sc-block-location__website")));
		this.websiteLabel = lazy(() -> website().findElementEx(By.className("mntl-sc-block-location__website-label")));
		this.websiteLink = lazy(() -> website().findElementEx(By.className("mntl-sc-block-location__website-text")));

	}

	public WebElementEx addressLabel() {
		return addressLabel.get();
	}

	public WebElementEx fullAddress() {
		return fullAddress.get();
	}

	public WebElementEx directions() {
		return directions.get();
	}

	public WebElementEx phone() {
		return phone.get();
	}

	public WebElementEx phoneLabel() {
		return phoneLabel.get();
	}

	public WebElementEx phoneText() {
		return phoneText.get();
	}

	public WebElementEx website() {
		return website.get();
	}

	public WebElementEx websiteLabel() {
		return websiteLabel.get();
	}

	public WebElementEx websiteLink() {
		return websiteLink.get();
	}
}
