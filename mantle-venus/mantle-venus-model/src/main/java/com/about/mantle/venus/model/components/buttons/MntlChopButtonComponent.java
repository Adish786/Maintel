package com.about.mantle.venus.model.components.buttons;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-chop")
public class MntlChopButtonComponent extends MntlComponent {
	
	private final Lazy<WebElementEx> buttonChop;
	private final Lazy<WebElementEx> buttonTitle;

	public MntlChopButtonComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.buttonChop = lazy(() -> findElement(By.className("btn-chop")));
		this.buttonTitle = lazy(() -> findElement(By.className("btn-title")));
	}
	
	public WebElementEx buttonChop() {
		return buttonChop.get();
	}
	
	public WebElementEx buttonTitle() {
		return buttonTitle.get();
	}

}
