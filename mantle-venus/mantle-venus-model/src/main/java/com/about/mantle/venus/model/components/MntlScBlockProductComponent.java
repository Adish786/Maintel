package com.about.mantle.venus.model.components;


import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-sc-block-product")
public class MntlScBlockProductComponent extends MntlComponent{

	private final Lazy<WebElementEx> title;
	private final Lazy<WebElementEx> image;
	private final Lazy<WebElementEx> button;
	private final Lazy<WebElementEx> details;


	
	public MntlScBlockProductComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.title = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-product__title")));
		this.image = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-product__image,.universal-image__image")));
		this.button = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-product__button")));
		this.details = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-product__details")));

	}
	
	@Validate
	public WebElementEx title() {
		return title.get();
	}
	
	@Validate
	public WebElementEx image() {
		return image.get();
	}
	
	@Validate
	public WebElementEx button() {
		return button.get();
	}
	
	@Validate
	public WebElementEx details() {
		return details.get();
	}

}
