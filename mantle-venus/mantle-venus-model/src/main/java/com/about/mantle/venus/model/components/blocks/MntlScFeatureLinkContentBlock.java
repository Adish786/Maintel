package com.about.mantle.venus.model.components.blocks;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.components.blocks.MntlScBlockCalloutComponent.MntlScBlockCalloutBodyComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-sc-block")
public class MntlScFeatureLinkContentBlock extends MntlComponent{
	
	private final Lazy<WebElementEx> featuredLinkWrapper;
	private final Lazy<WebElementEx> featuredLinkImage;

	public MntlScFeatureLinkContentBlock(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.featuredLinkWrapper = lazy(() -> findElement(By.className("link__wrapper")));
		this.featuredLinkImage = lazy(() -> findElement(By.cssSelector(".featured-link-image")));
	}
	
	public WebElementEx featuredLinkWrapper() {
		return featuredLinkWrapper.get();
	}
	
	public WebElementEx featuredLinkImage() {
		return featuredLinkImage.get();
	}

}
