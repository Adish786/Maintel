package com.about.mantle.components.blocks;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.blocks.MntlScBlockStarRatingComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.By;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class MntlScBlockStarRatingTest<T extends MntlCommonTestMethods.Runner<T>>
		extends MntlVenusTest
		implements MntlCommonTestMethods<T> {

	/**
	 * Test Star Rating for a given StarRatingComponent
	 * @param driver WebDriverExtended
	 * @param collector ErrorCollector
	 * @param component StarRatingComponent
	 * @param expectedRating Double value of expected rating (0.0 to 5.0)
	 */
	protected void testStarRating(WebDriverExtended driver, ErrorCollector collector, MntlScBlockStarRatingComponent component, double expectedRating) {
		WebElementEx ele = component.getWebElement();
		String expectedAlt = expectedRating + " out of 5 stars";
		collector.checkThat("star rating element not found", ele, is(notNullValue()));
		if (ele != null) {
			collector.checkThat("star rating isn't displayed", ele.isDisplayed(), is(true));
			collector.checkThat("star rating alt isn't correct", ele.getAttribute("aria-label"), is(expectedAlt));
			collector.checkThat("star rating isn't correct", component.rating().text(), is(Double.toString(expectedRating)));

			// Check to make sure the right amount of stars are highlighted - the stars bar width being 100% indicates full
			// else a ratio of star
			List<WebElementEx> stars = component.starList();
			for (int i = 1; i <= 5; i++) {
				WebElementEx star = stars.get(i - 1);
				WebElementEx starBar = star.findElementEx(By.className("mntl-sc-block-starrating__bar"));
				String barWidth = starBar.getAttribute("style");
				if (expectedRating >= i) { // if total is greater than the current index then star is fully displayed
					collector.checkThat("star (" + i + ") rating width isn't correct", barWidth, containsString("width: 100%"));
				} else if ((expectedRating < (i+1)) && (expectedRating > (i-1))) {
					Double expectedWidth = (expectedRating - (i-1)) * 100;
					String expectedWidthStyle = "width: " + String.format("%.0f", expectedWidth) + "%";
					collector.checkThat("star (" + i + ") rating width isn't correct", barWidth, containsString(expectedWidthStyle));
				} else {
					collector.checkThat("star (" + i + ") rating width isn't correct", barWidth, containsString("width: 0%"));
				}
			}
		}
	}
}
