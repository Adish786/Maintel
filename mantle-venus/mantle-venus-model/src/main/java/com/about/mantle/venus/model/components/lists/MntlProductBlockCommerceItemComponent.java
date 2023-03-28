package com.about.mantle.venus.model.components.lists;
import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.components.buttons.MntlProductBlockCommerceButton;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".js-extended-commerce__block")
public class MntlProductBlockCommerceItemComponent extends MntlComponent{

	private final Lazy<List<MntlProductBlockCommerceButton>> commerceProductButtons;
	private final Lazy<WebElementEx> starRating;
	private final Lazy<WebElementEx> productBlockImage;
	private final Lazy<WebElementEx> productBlockHeadingText;
	private final Lazy<List<WebElementEx>> productBlockHeader;
	private final Lazy<List<WebElementEx>> productBlockButton;
	private final Lazy<List<WebElementEx>> productBlockImages;
	
	public MntlProductBlockCommerceItemComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.commerceProductButtons = lazy(() -> findComponents(MntlProductBlockCommerceButton.class));
		this.starRating = lazy(() -> findElement(By.cssSelector(".product-record__star-rating")));
		this.productBlockImage = lazy(() -> findElement(By.cssSelector(".js-extended-commerce__block img")));
		this.productBlockHeadingText = lazy (() -> findElement(By.cssSelector(".mntl-sc-block-productrecord__link-text")));
		this.productBlockHeader = lazy (() -> findElements(By.cssSelector(".mntl-sc-block-productrecord__link")));
		this.productBlockButton = lazy (() -> findElements(By.cssSelector(".mntl-sc-block-productrecord__commerce-button-list a")));
		this.productBlockImages = lazy (() -> findElements(By.cssSelector(".mntl-sc-block-productrecord img")));
		
	}
	
	public List<MntlProductBlockCommerceButton> commerceProductButtons() {
		return commerceProductButtons.get();
	}
	
	public boolean hasStarRating() {
		return getElement().elementExists(".product-record__star-rating");
	}
	
	public WebElementEx productStarRating() {
		return starRating.get();
	}
	
	public WebElementEx productBlockImage() {
        return this.productBlockImage.get();
    }
	
	public WebElementEx productBlockHeading() {
		return productBlockHeadingText.get();
	}
	
	public List<WebElementEx> productBlockHeader() {
		return productBlockHeader.get();
	}
	
	public List<WebElementEx> productBlockButton() {
		return productBlockButton.get();
	}
	
	public List<WebElementEx> productBlockImages() {
		return productBlockImages.get();
	}
}
