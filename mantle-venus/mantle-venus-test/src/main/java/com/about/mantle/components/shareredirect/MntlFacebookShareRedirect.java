package com.about.mantle.components.shareredirect;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

//Bug report for this component GLBE-6293 about Incorrect component id

@MntlComponentId("mntl-facebook-share-redirect")

public class MntlFacebookShareRedirect extends MntlComponent {

	public MntlFacebookShareRedirect(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}

}
