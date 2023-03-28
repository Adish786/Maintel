package com.about.mantle.venus.model.components;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-accordion--document-list")
public class MntlAccordionDocumentListComponent extends MntlComponent {
	
	private final Lazy<List<WebElementEx>> accordionTitle;

	public MntlAccordionDocumentListComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.accordionTitle = lazy(() -> findElements(By.cssSelector(".accordion__title")));
	}
	
	@Validate
	public List<WebElementEx> accordionTitle() {
		return accordionTitle.get();
	}
	
}
