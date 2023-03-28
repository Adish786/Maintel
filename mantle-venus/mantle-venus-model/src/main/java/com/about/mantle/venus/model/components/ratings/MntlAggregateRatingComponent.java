package com.about.mantle.venus.model.components.ratings;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-aggregate-rating")
public class MntlAggregateRatingComponent extends MntlRatingComponent {

	private final Lazy<WebElementEx> aggregate;

	public MntlAggregateRatingComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.aggregate = lazy(() -> findElement(By.tagName("a")));
	}

	public WebElementEx madeIt() {
		return aggregate.get();
	}

}