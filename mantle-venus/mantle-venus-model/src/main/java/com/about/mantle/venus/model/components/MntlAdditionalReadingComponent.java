package com.about.mantle.venus.model.components;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-additional-reading")
public class MntlAdditionalReadingComponent extends MntlComponent implements MntlCitationInterface  {

	private final Lazy<List<WebElementEx>> contentList;
	private final Lazy<WebElementEx> unorderedList;

	public MntlAdditionalReadingComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.contentList = lazy(() -> findElements(By.cssSelector("li.mntl-sources__source")));
		this.unorderedList = lazy(() -> findElement(By.cssSelector("ul.mntl-sources__content")));

	}

	public WebElementEx list() {
		return unorderedList.get();
	}
	
	public List<WebElementEx> contentList() {
		return contentList.get();
	}
}
