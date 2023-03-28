package com.about.mantle.venus.model.schema;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

@MntlComponentCssSelector(".mntl-schema-unified")
public class SchemaComponent extends MntlComponent {

	public SchemaComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}

	public String schemaContent() {
		return getElement().getAttribute("innerHTML");
	}
	
	
}
