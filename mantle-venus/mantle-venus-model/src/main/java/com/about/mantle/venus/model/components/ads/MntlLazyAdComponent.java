package com.about.mantle.venus.model.components.ads;

import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

@MntlComponentId("mntl-lazy-ad")
public class MntlLazyAdComponent extends MntlAdComponent {

	public MntlLazyAdComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}

}
