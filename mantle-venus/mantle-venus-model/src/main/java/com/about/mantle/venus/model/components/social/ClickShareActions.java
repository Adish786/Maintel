package com.about.mantle.venus.model.components.social;

import java.util.Iterator;
import java.util.Set;

import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.model.Page;

public abstract class ClickShareActions<T extends Page> extends Component {

	public ClickShareActions(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}

	/**
	 * Waits for the visiblity of the share button, clicks on it, and then switch to the newly opened window
	 * 
	 * @return a social share page
	 */
	public T openInNewWindow() {
		getElement().waitFor().visibility();
		String windowHandle = getDriver().getWindowHandle(() -> getElement().click());
		waitFor().aMoment();
		getDriver().switchTo().window(windowHandle);
		waitFor().pageTitleIsNot("about:blank");
		return createPage();
	}
	
	public T openInNewTab() {
		return openInNewWindow();
	}
	
	public T share() {
		return openInNewWindow();
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
