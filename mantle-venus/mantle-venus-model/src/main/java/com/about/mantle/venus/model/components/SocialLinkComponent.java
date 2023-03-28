package com.about.mantle.venus.model.components;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

@MntlComponentId("socialLinks")
public class SocialLinkComponent extends MntlComponent {

	public SocialLinkComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}

	public static class Form extends AbstractForm {

		public Form(WebDriverExtended driver, WebElementEx element, String prefix) {
			super(driver, element, prefix);
		}

		public void string(int string) {
			field("string", string);
		}
	}
}