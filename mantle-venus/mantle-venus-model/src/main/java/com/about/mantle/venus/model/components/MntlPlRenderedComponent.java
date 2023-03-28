package com.about.mantle.venus.model.components;

import org.openqa.selenium.By;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;


@MntlComponentId("mntl-pl-rendered-component")
public class MntlPlRenderedComponent extends MntlComponent {
	
	private final Lazy<WebElementEx> code;

	public MntlPlRenderedComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.code = lazy(() -> findElement(By.tagName("code")));

	}
	
	public WebElementEx code() {
		return code.get();
	}
}
	
