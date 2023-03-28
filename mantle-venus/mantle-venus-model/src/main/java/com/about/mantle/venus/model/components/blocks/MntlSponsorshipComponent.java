package com.about.mantle.venus.model.components.blocks;

import com.about.mantle.venus.model.MntlComponentCssSelector;
import org.openqa.selenium.By;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-sponsorship")
public class MntlSponsorshipComponent extends MntlComponent {

	private final Lazy<WebElementEx> title;
	private final Lazy<MntlSponsorshipContentComponent> content;
	private final Lazy<WebElementEx> disclaimer;
	private final Lazy<WebElementEx> outerLogoWrapper;

	public MntlSponsorshipComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.title = lazy(() -> findElement(By.cssSelector(".mntl-sponsorship__title")));
		this.content = lazy(() -> findComponent(MntlSponsorshipContentComponent.class));
		this.disclaimer = lazy(() -> findElement(By.cssSelector(".mntl-sponsorship__disclaimer")));
		this.outerLogoWrapper = lazy(() -> findElement(By.cssSelector(".mntl-sponsorship__logo-wrapper")));
	}
	
	public WebElementEx title() {
		return title.get();
	}
	
	public MntlSponsorshipContentComponent content() {
		return content.get();
	}

	public WebElementEx disclaimer() {
		return disclaimer.get();
	}

	public WebElementEx outerLogoWrapper() {
		return outerLogoWrapper.get();
	}

	@MntlComponentId("mntl-sponsorship__content")
	public static class MntlSponsorshipContentComponent extends MntlComponent{

		private final Lazy<WebElementEx> logoWrapper;
		private final Lazy<WebElementEx> logo;
		private final Lazy<WebElementEx> disclaimer;

		public MntlSponsorshipContentComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.logoWrapper = lazy(() -> findElement(By.cssSelector(".mntl-sponsorship__logo-wrapper")));
			this.logo = lazy(() -> logoWrapper().findElementEx(By.tagName("img")));
			this.disclaimer = lazy(() -> findElement(By.cssSelector(".mntl-sponsorship__disclaimer")));
		}
		
		public WebElementEx logoWrapper() {
			return logoWrapper.get();
		}
		
		public WebElementEx logo() {
			return logo.get();
		}

		public WebElementEx disclaimer() {
			return disclaimer.get();
		}

	}

}
