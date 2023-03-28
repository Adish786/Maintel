package com.about.mantle.venus.model.components.buttons;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;

import com.about.mantle.venus.model.MntlComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Page;
import com.google.common.base.Predicate;

public abstract class ClickActions<T extends Page> extends MntlComponent {

	public ClickActions(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}
	
	public String currentUrl() {
		return getDriver().getCurrentUrl();
	}

	/**
	 * Waits for the visibility of the button, clicks on it, and then switch to the newly opened window
	 * 
	 * @return a page
	 */
	public T openInNewWindow() {
		getElement().waitFor().visibility();
		getElement().scrollIntoViewCentered();
		String windowHandle = getDriver().getWindowHandle(() -> getElement().click());
		getDriver().waitFor((Predicate<WebDriver>) driver -> getDriver().getWindowHandles().size() >= 1);
		getDriver().switchTo().window(windowHandle);
		waitFor().pageTitleIsNot("about:blank");
		waitFor().aMoment(2, TimeUnit.SECONDS);
		return createPage();
	}

	/**
	 * close all windows except the parent
	 * 
	 * @param parent windows
	 * 
	 */
	public void closeWindow(String parent) {
		Set<String> windows = getDriver().getWindowHandles();
		Iterator<String> it = windows.iterator();
		while (it.hasNext()) {
			String windowHandle = it.next().toString();
			if (!windowHandle.equals(parent)) {
				getDriver().switchTo().window(windowHandle);
				getDriver().close();
			}
		}
		getDriver().switchTo().window(parent);
	}
	
	public abstract T createPage();

}
