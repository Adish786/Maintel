package com.about.mantle.venus.model.pages;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlPage;
import com.about.mantle.venus.model.components.MntlPlLayoutPage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
//** Test base page file **//
public class PatternLibPage<T extends MntlComponent> extends MntlPage {

	private final Lazy<List<WebElementEx>> breakpoints;
	private final WebDriverExtended driver;
	private final Supplier<T> component;

	public T getComponent() {
		return component.get();
	}

	public PatternLibPage(WebDriverExtended driver, Class<T> componentClass) {
		super(driver);
		this.driver = driver;
		this.component = () -> findPlComponent(componentClass);
		this.breakpoints = breakpointsLazyMethod();
	}
	
	/**
	 * This constructor should be used for Non-Mantle Components
	 * @param driver
	 * @param by
	 * @param function
	 */
	public PatternLibPage(WebDriverExtended driver ,By by, BiFunction<WebDriverExtended, WebElementEx, T> function) {
		super(driver);
		this.driver = driver;
		this.component = () -> findPlComponent(by, function);
		this.breakpoints = breakpointsLazyMethod();
	}
	
	public List<WebElementEx> breakpoints() {
		driver.switchToDefaultContent();
		return breakpoints.get();
	}
	
	private Lazy<List<WebElementEx>> breakpointsLazyMethod() {
		return lazy(() -> findElements(By.cssSelector(".breakpoint-pointers a")));
	}
	
	private <T1 extends MntlComponent> T findPlComponent(Class<T> componentClass) {
		MntlPlLayoutPage<T> mntlPl = new MntlPlLayoutPage<>(driver, componentClass);
		return mntlPl.component() ;
	}
	
	private <T1 extends MntlComponent> T findPlComponent(By by, BiFunction<WebDriverExtended, WebElementEx, T> function) {
		MntlPlLayoutPage<T> mntlPl = new MntlPlLayoutPage<>(driver, by, function);
		return mntlPl.component() ;
	}

}