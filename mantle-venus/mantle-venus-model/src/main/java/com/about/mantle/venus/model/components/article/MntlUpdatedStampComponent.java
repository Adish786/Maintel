package com.about.mantle.venus.model.components.article;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@Deprecated //until all the verticals are using MntlBylinesComponent https://dotdash.atlassian.net/browse/AXIS-2022
@MntlComponentCssSelector(".mntl-updated-stamp")
public class MntlUpdatedStampComponent extends MntlComponent {

	private final Lazy<WebElementEx> updatedStampText;

	public MntlUpdatedStampComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.updatedStampText = lazy(() -> findElement(By.className("mntl-updated-stamp__text")));
	}

	public WebElementEx updatedStampText() {
		return updatedStampText.get();
	}

}
