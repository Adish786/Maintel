package com.about.mantle.venus.model.components.social;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("social-share")
public class MntlSocialShareComponent extends MntlComponent {

	private final Lazy<MntlFacebookShare.ShareButton> facebookButton;
	private final Lazy<MntlPinterestShare.ShareButton> pinterestButton;
	private final Lazy<WebElementEx> emailButton;

	public MntlSocialShareComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.facebookButton = lazy(
				() -> new MntlFacebookShare.ShareButton(driver, findElement(By.className("share-link-facebook"))));
		this.pinterestButton = lazy(
				() -> new MntlPinterestShare.ShareButton(driver, findElement(By.className("share-link-pinterest"))));
		this.emailButton = lazy(() -> findElement(By.className("share-link-emailshare")));
	}
	
	public MntlFacebookShare.ShareButton facebookButton() {
		return facebookButton.get();
	}

	public MntlPinterestShare.ShareButton pinterestButton() {
		return pinterestButton.get();
	}

	public WebElementEx emailButton() {
		return emailButton.get();
	}
	
}
