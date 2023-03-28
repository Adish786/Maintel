package com.about.mantle.venus.model.components.ratings;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-aggregate-star-rating")

public class MntlAggregateStarRatingComponent extends MntlAggregateRatingComponent{
	
	private final Lazy<List<WebElementEx>> stars;
		
	public MntlAggregateStarRatingComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.stars = lazy(() -> findElements(By.tagName("a")));
	}

	public List<WebElementEx> stars() {
		return stars.get();
	}

}

