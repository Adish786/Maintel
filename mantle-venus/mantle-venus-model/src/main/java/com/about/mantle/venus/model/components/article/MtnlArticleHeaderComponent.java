package com.about.mantle.venus.model.components.article;

import com.about.mantle.venus.model.MntlComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

public abstract class MtnlArticleHeaderComponent extends MntlComponent {

	public MtnlArticleHeaderComponent(WebDriverExtended driver,
									  WebElementEx element) {
		super(driver, element);
	}

	/*
	Article Heading can be implemented in vertical with vertical specific selector
	Here we keep an abstract method to support Article Heading mantle test.
	@return WebElementEx for heading component
	*/
	public abstract WebElementEx heading();

	/*
	Article Sub Heading can be implemented in vertical with vertical specific selector
	Here we keep an abstract method to support Article Sub Heading mantle test.
	@return WebElementEx for sub heading component
	 */
	public abstract WebElementEx subHeading();
}
