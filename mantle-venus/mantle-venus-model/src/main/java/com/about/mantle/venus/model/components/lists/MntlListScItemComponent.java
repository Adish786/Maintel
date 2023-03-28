package com.about.mantle.venus.model.components.lists;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.components.blocks.MntlScBlockStarRatingComponent;
import com.about.mantle.venus.model.components.buttons.MntlScBLockCommerceButton;
import com.about.mantle.venus.model.components.media.MntlFigureComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-sc-list-item")
public class MntlListScItemComponent extends MntlComponent{

	private final Lazy<WebElementEx> listHeading;
	private final Lazy<MntlFigureComponent> figureComponent;
	private final Lazy<WebElementEx> commerceImage;
	private final Lazy<List<WebElementEx>> inlineImages;
	private final Lazy<MntlScBlockStarRatingComponent> starRating;
	private final Lazy<MntlComparisionListComponent> comparisonList;
	private final Lazy<List<MntlScBLockCommerceButton>> commerceButtons;
	
	public MntlListScItemComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.listHeading = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-heading")));
		this.figureComponent = lazy(() -> findElement(By.tagName("figure"), MntlFigureComponent::new));
    	this.commerceImage = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-commerce__image img")));
    	this.inlineImages = lazy(() -> findElements(By.cssSelector(".img-placeholder img")));
		this.starRating = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-starrating"), MntlScBlockStarRatingComponent::new));
		this.comparisonList = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-comparisonlist"), MntlComparisionListComponent::new));
		this.commerceButtons = lazy(() -> findComponents(MntlScBLockCommerceButton.class));
	}
	
	public MntlFigureComponent figureComponent() {
	        return this.figureComponent.get();
	    }
	
	public List<WebElementEx> inlineImages() {
        return this.inlineImages.get();
    }
	 
	public MntlScBlockStarRatingComponent starRating() {
        return this.starRating.get();
    }
	
	public MntlComparisionListComponent comparisonList() {
        return this.comparisonList.get();
    }
	
	public WebElementEx commerceImage() {
        return this.commerceImage.get();
    }
	
	public boolean hasCommerceImage() {
		return getElement().elementExists(".mntl-sc-list-item > .mntl-sc-block-commerce__image");
	}
	
	public boolean hasCommerceButtons() {
		return getElement().elementExists(".mntl-sc-block-commerce__button");
	}

	public List<MntlScBLockCommerceButton> commerceButtons() {
		return commerceButtons.get();
	}
	
	public WebElementEx listHeading() {
		return listHeading.get();
	}

	public boolean hasStarRating() {
		return getElement().elementExists(".mntl-sc-block-starrating");
	}
	
	public boolean hasStarRatingProduct() {
		return getElement().elementExists(".star-rating");
	}
	
	public boolean hasProductBlock() {
		return getElement().elementExists(".product-record__commerce-buttons");
	}

}