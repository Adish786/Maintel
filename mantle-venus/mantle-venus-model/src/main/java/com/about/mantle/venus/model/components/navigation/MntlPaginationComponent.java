package com.about.mantle.venus.model.components.navigation;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

public class MntlPaginationComponent extends MntlComponent {
	
	private final Lazy<List<WebElementEx>> paginationItems;
	private final Lazy<WebElementEx> next;
	private final Lazy<WebElementEx> previous;

	public MntlPaginationComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.paginationItems = lazy(() -> findElements(By.cssSelector("li a")));
		this.next = lazy(() -> findElement(By.className("pagination-next")));
		this.previous = lazy(() -> findElement(By.className("pagination-prev")));
	}
	
	public List<WebElementEx> paginationItems() {
		return paginationItems.get();
	}

	public WebElementEx next() {
		return next.get();
	}
	
	public WebElementEx previous() {
		return previous.get();
	}
	
}
