package com.about.mantle.venus.model.components.blocks;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-sc-block-guide")
public class MntlScBlockGuideComponent extends MntlComponent {
	private final Lazy<WebElementEx> heading;
	private final Lazy<WebElementEx> description;
	private final Lazy<WebElementEx> image;
	private final Lazy<WebElementEx> downloadLink;
	public MntlScBlockGuideComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.heading = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-guide-heading")));
		this.description = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-guide-description")));
		this.image = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-guide-image img")));
		this.downloadLink = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-guide-link a")));
	}
	public WebElementEx heading() {
		return heading.get();
	}
	
	public WebElementEx description() {
		return description.get();
	}
	public WebElementEx image() {
		return image.get();
	}
	public WebElementEx downloadLink() {
		return downloadLink.get();
	}
	
	public boolean hasHeading() {
		return getElement().elementExists(".mntl-sc-block-guide-heading");
	}

}
