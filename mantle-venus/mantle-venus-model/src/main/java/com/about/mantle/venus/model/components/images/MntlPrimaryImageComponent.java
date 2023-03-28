package com.about.mantle.venus.model.components.images;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentXpathSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

//TODO: remove locator for 'mntl-primary-image' once image update is finished on all the verticals
@MntlComponentXpathSelector("//img[contains(@class, 'mntl-universal-primary-image')] | //img[contains(@class, 'mntl-primary-image')]")
public class MntlPrimaryImageComponent extends MntlComponent{

	public MntlPrimaryImageComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}
}
