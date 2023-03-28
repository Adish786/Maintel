package com.about.mantle.venus.model.components.widgets;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

public class MntlAccordianComponent extends MntlComponent {
	
	private final Lazy<WebElementEx> title;
	private final Lazy<WebElementEx> plusIcon;
	private final Lazy<List<WebElementEx>> navItems;
	private final Lazy<List<WebElementEx>> listItems;

	public MntlAccordianComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.title = lazy(() -> findElement(By.className("accordion__title")));
		this.plusIcon = lazy(() -> findElement(By.className("journey-nav__toggle-icon")));
		this.navItems = lazy(() -> findElements(By.className("primary-nav__item-link")));
		this.listItems = lazy(() -> findElements(By.className("primary-nav-list-item")));
	}
	
	public boolean isActive() {
		return getElement().hasClass("is-active");
	}
	
	public WebElementEx title() {
		return title.get();
	}
	
	public WebElementEx plusIcon() {
		return plusIcon.get();
	}
	
	public List<WebElementEx> navItems() {
		return navItems.get();
	}
	
	public List<WebElementEx> listItems() {
		return listItems.get();
	}

}
