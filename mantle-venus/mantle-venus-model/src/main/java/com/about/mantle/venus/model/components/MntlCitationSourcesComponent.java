package com.about.mantle.venus.model.components;


import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-citation-sources")
public class MntlCitationSourcesComponent extends MntlComponent implements MntlCitationInterface {
	
	private final Lazy<List<WebElementEx>> contentList;
	private final Lazy<WebElementEx> orderedList;

	public MntlCitationSourcesComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.contentList = lazy(() -> findElements(By.cssSelector("li.mntl-sources__source")));
		this.orderedList = lazy(() -> findElement(By.cssSelector("ol.mntl-sources__content")));

	}

	public List<WebElementEx> contentList() {
		return contentList.get();
	}
	
	@Validate
	public WebElementEx list() {
		return orderedList.get();
	}
	
}
