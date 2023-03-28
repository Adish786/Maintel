package com.about.mantle.venus.model.components;

import java.util.function.BiFunction;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlPage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

public class MntlPlLayoutPage<T extends MntlComponent> extends MntlPage {
	
	private final Lazy<T> component;
	
	public MntlPlLayoutPage(WebDriverExtended driver, Class<T> componentClass) {
		super(driver.switchToFrame(By.cssSelector("[id^=iframe-]")));
		this.component = lazy(() -> findComponent(componentClass));
	}
	
	public MntlPlLayoutPage(WebDriverExtended driver,By by, BiFunction<WebDriverExtended, WebElementEx, T> function) {
		super(driver.switchToFrame(By.cssSelector("[id^=iframe-]")));
		this.component = lazy(() -> findElement(by, function));
	}
	
	public T component() {
		return component.get();
	}

}
