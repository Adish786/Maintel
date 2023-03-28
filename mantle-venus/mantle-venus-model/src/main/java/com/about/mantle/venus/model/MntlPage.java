package com.about.mantle.venus.model;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.google.common.base.Predicate;
import org.openqa.selenium.By;

import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Page;
import com.about.venus.core.model.ScriptTagComponent;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class MntlPage extends Page {
	
	public MntlPage(WebDriverExtended driver) {
		super(driver);
	}

	protected <T extends MntlComponent> List<T> findComponents(Class<T> componentClass) {
		By locator = MntlUtils.getLocator(componentClass);
		BiFunction<WebDriverExtended, WebElementEx, T> constructorFunction = MntlUtils
				.getConstructorFunction(componentClass);

		return findElements(locator, constructorFunction);
	}

	protected <T extends MntlComponent> T findComponent(Class<T> componentClass) {
		By locator = MntlUtils.getLocator(componentClass);
		BiFunction<WebDriverExtended, WebElementEx, T> constructorFunction = MntlUtils
				.getConstructorFunction(componentClass);
		return findElement(locator, constructorFunction);
	}
	
	public List<ScriptTagComponent> getScriptsFromHead(String tag) {
		return head().scripts().stream().filter(script -> script.src().contains(tag)).collect(Collectors.toList());
	}
	
	public List<ScriptTagComponent> getScriptsFromBody(String tag) {
		return body().scripts().stream().filter(script -> script.innerHtml().contains(tag)).collect(Collectors.toList());
	}

	/**
	 * Will check if the component exists in the page
	 * @param componentClass - component .class to wait for
	 * @return true if component found, false otherwise
	 */
	public <T extends MntlComponent> Boolean componentExists(Class<T> componentClass) {
		return !findComponents(componentClass).isEmpty();
	}
	/**
	 * Will check if the component exists in the page
	 * @param by - By selector for component to find
	 * @return true if component found, false otherwise
	 */
	public Boolean componentExists(By by) {
		return !findElements(by).isEmpty();
	}

	/**
	 * Will wait for a component to exist on the page
	 * @param componentClass - component .class to wait for
	 * @param timeout - timeout in seconds
	 * @return true if found, false otherwise
	 */
	public <T extends MntlComponent> Boolean waitForComponent(Class<T> componentClass, int timeout) {
		Boolean foundComponent = true;
		try {
			this.getDriver().waitFor((Predicate<WebDriver>) wd -> componentExists(componentClass), timeout);
		} catch(TimeoutException e) {
			foundComponent = false;
		}
		return foundComponent;
	}

	/**
	 * Will check if the component exists in the page
	 * @param by - By selector for class to find
	 * @param timeout - timeout in seconds
	 * @return true if found, false otherwise
	 */
	public <T extends MntlComponent> Boolean waitForComponent(By by, int timeout) {
		Boolean foundComponent = true;
		try {
			this.getDriver().waitFor((Predicate<WebDriver>) wd -> componentExists(by), timeout);
		} catch(TimeoutException e) {
			foundComponent = false;
		}
		return foundComponent;
	}
}
