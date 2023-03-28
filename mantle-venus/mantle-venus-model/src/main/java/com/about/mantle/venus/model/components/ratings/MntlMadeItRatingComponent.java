package com.about.mantle.venus.model.components.ratings;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-made-it")
public class MntlMadeItRatingComponent extends MntlRatingComponent {

	private final Lazy<WebElementEx> madeIt;

	public MntlMadeItRatingComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.madeIt = lazy(() -> findElement(By.tagName("a")));
	}

	@Validate()
	public WebElementEx madeIt() {
		return madeIt.get();
	}

	public boolean value() {
		boolean value = false;
		if(madeIt.get().className().contains("active")) value=true;
		return value;
	}
	


}