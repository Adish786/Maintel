package com.about.mantle.venus.model.components.global;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

@MntlComponentId("logo")
public class MntlLogoComponent extends MntlComponent {

	public MntlLogoComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}

}
