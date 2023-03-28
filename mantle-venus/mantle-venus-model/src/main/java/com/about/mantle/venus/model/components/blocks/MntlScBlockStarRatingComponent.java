package com.about.mantle.venus.model.components.blocks;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.By;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

@MntlComponentId("mntl-sc-block-starrating")
public class MntlScBlockStarRatingComponent extends MntlComponent{

	private Lazy<List<WebElementEx>> starList;
	private Lazy<WebElementEx> rating;

	public MntlScBlockStarRatingComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.starList = lazy(() -> findElements(By.className("mntl-sc-block-starrating__wrapper")));
		this.rating = lazy(() -> findElement(By.className("mntl-sc-block-starrating__label")));
	}

	public String testedByImageSize() {
		return this.getElement().pseudoElementProperty("before", "height") + " by " + this.getElement().pseudoElementProperty("before", "width");
	}

	public List<WebElementEx> starList(){
		return starList.get();
	}

	public WebElementEx rating(){
		return rating.get();
	}
}