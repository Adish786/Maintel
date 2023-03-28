package com.about.mantle.venus.model.components.widgets;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-commerce-disclosure")
public class MntlCommerceDisclosureComponent extends MntlComponent {

	private final Lazy<WebElementEx> reviewProcessLink;

	public MntlCommerceDisclosureComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.reviewProcessLink = lazy(() -> findElement(By.cssSelector(".mntl-commerce-disclosure__review-process-link")));
	}

	@Validate
	public WebElementEx reviewProcessLink() {
		return reviewProcessLink.get();
	}
	
}
