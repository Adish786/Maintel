package com.about.mantle.venus.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.about.mantle.venus.model.pages.PatternLibPage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

public class MntlComponent extends Component {
	public MntlComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}
	

	public <T extends MntlComponent> T component() {
		@SuppressWarnings("unchecked")
		Class<T> componentClass = (Class<T>) this.getClass();
		List<T> possibles = findComponents(componentClass);
		if (possibles == null || possibles.size() == 0) {
			return null;
		}
		return possibles.get(0);
	}

	public <T extends MntlComponent> List<T> findComponents(Class<T> componentClass) {

		By locator = MntlUtils.getLocator(componentClass);
		BiFunction<WebDriverExtended, WebElementEx, T> constructorFunction = MntlUtils
				.getConstructorFunction(componentClass);

		return findElements(locator, constructorFunction);
	}

	/**
	 * Use the base getElement() in subsequent releases.
	 * @return
	 */
	@Deprecated
	public WebElementEx getWebElement(){
		return getElement();
	}
	public <T extends MntlComponent> T findComponent(Class<T> componentClass) {

		By locator = MntlUtils.getLocator(componentClass);
		BiFunction<WebDriverExtended, WebElementEx, T> constructorFunction = MntlUtils
				.getConstructorFunction(componentClass);

		return findElement(locator, constructorFunction);
	}

	@Validate
	public <T extends MntlComponent> void assertions(ErrorCollector collector, String className, Class<T> clazz)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		collector.checkThat("class name " + className + "does not exist in the component",
				this.className().contains(className), is(true));
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(Validate.class)) {
				if (method.getReturnType().equals(List.class)) {
					List<WebElementEx> elements = (List<WebElementEx>) method.invoke(this);
					collector.checkThat("element list with following component method has zero size :"
							+ className + " method: " + method.getName(), elements.size(), not(is(0)));
					for (WebElementEx element : elements) {
						collector.checkThat("element list with following component method has display issues :"
								+ className + " method: " + method.getName(), element.isDisplayed(), is(true));
					}
				} else {
					collector.checkThat("element with following method is not displayed :" + className + " method: "
							+ method.getName(), ((WebElementEx) method.invoke(this)).isDisplayed(), is(true));
				}
			}
		}

	}
	
	protected static abstract class AbstractForm {

		private final WebDriverExtended driver;
		private final WebElementEx element;
		private final String prefix;

		protected AbstractForm(WebDriverExtended driver, WebElementEx element, String prefix) {
			this.driver = driver;
			this.element = element;
			this.prefix = prefix;

		}

		private WebElement field(String name) {
			return driver.findElement(By.id(prefix + "_" + name));
		}

		protected void field(String name, int value) {
			field(name).sendKeys("" + value);
		}

		protected void field(String name, String value) {
			field(name).sendKeys(value);
		}

		protected WebDriverExtended getDriver() {
			return driver;
		}

		protected WebElementEx getElement() {
			return element;
		}

		protected String getPrefix() {
			return prefix;
		}

		public <T extends AbstractForm> T form(Class<T> formClass) {

			@SuppressWarnings("unchecked")
			Class<? extends MntlComponent> componentClass = (Class<? extends MntlComponent>) formClass
					.getEnclosingClass();
			String id = MntlUtils.getId(componentClass);

			try {
				Constructor<T> constructor = formClass.getConstructor(new Class[] { WebDriverExtended.class,
						WebElementEx.class, String.class });
				return constructor.newInstance(getDriver(), getElement(), prefix + "_" + id);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;

		}
	}
}