package com.about.mantle.venus.model.components.blocks;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.components.buttons.MntlScBLockCommerceButton;
import com.about.mantle.venus.model.components.lists.MntlComparisionListComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("outro")
public class OutroBlockComponent extends MntlComponent {

	private final Lazy<List<WebElementEx>> listItemHeading;
	private final Lazy<List<MntlScBlockStarRatingComponent>> starRating;
	private final Lazy<List<MntlScBLockCommerceButton>> commerceSCButtons;
	private final Lazy<List<WebElementEx>> commerceSCImages;
	private final Lazy<List<WebElementEx>> inlineImage;
	private Lazy<List<MntlComparisionListComponent>> comparisonList;
	private final Lazy<List<WebElementEx>> commerceWidgetDescription;

	public OutroBlockComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.listItemHeading = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-heading")));
		this.starRating = lazy(
				() -> findElements(By.cssSelector(".mntl-sc-block-starrating"), MntlScBlockStarRatingComponent::new));
		this.commerceSCButtons = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-commerce__button"),
				MntlScBLockCommerceButton::new));
		this.commerceSCImages = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-commerce__image")));
		this.inlineImage = lazy(() -> findElements(By.cssSelector(".img-placeholder img")));
		this.comparisonList = lazy(
				() -> findElements(By.className("mntl-sc-block-comparisonlist"), MntlComparisionListComponent::new));
		this.commerceWidgetDescription = lazy(() -> findElements(By.cssSelector(".commerce-widget__description")));

	}

	public List<WebElementEx> listItemHeading() {
		return listItemHeading.get();
	}

	public List<MntlScBlockStarRatingComponent> starRating() {
		return starRating.get();
	}

	public List<MntlScBLockCommerceButton> commerceSCButtons() {
		return commerceSCButtons.get();
	}

	public List<WebElementEx> commerceSCImages() {
		return commerceSCImages.get();
	}

	public List<WebElementEx> inlineImage() {
		return inlineImage.get();
	}

	public List<MntlComparisionListComponent> comparisonList() {
		return comparisonList.get();
	}
	
	public List<WebElementEx> commerceWidgetDescription() {
		return commerceWidgetDescription.get();
	}

}