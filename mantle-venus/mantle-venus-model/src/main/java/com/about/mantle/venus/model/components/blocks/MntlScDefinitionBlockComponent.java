package com.about.mantle.venus.model.components.blocks;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-sc-block-definition")
public class MntlScDefinitionBlockComponent extends MntlComponent {
	private final Lazy<WebElementEx> heading;
	private final Lazy<WebElementEx> body;

	
	public MntlScDefinitionBlockComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.heading = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-definition-heading")));
		this.body = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-definition-body")));

	}

	public WebElementEx heading() {
		return heading.get();
	}

	public String bodyText() {
		return body.get().findElement(By.tagName("p")).getText();
	}

	public WebElementEx body() {
		return body.get();
	}

}