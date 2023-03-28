package com.about.mantle.venus.model.components.ratings;

import java.util.stream.Collectors;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

@MntlComponentId("mntl-star-rating")
public class MntlStarRatingComponent extends MntlRatingComponent {


	public MntlStarRatingComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}

	public WebElementEx starNo(int no) {
		return findElements(By.tagName("a")).get(no-1);
	}

	public int value() {
		waitFor().aMoment();
		return findElements(By.tagName("a")).stream().filter(star -> star.className().contains("active")).collect(Collectors.toList())
				.size();
	}
	

}