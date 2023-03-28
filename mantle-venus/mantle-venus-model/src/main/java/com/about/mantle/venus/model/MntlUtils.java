package com.about.mantle.venus.model;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiFunction;

import org.openqa.selenium.By;

import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

public final class MntlUtils {

	private MntlUtils() {
	}
	
	static String getId(Class<? extends MntlComponent> componentClass) {
		MntlComponentId annotation = componentClass.getAnnotation(MntlComponentId.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Mantle component class " + componentClass + " must have annotation "
					+ MntlComponentId.class.getSimpleName());
		}
		
		String locator = annotation.value();
		if (isBlank(locator)) {
			throw new IllegalArgumentException("Mantle component class " + componentClass + " annotation "
					+ MntlComponentId.class.getSimpleName() + " must have non blank string value for id, but was ["
					+ locator + "]");
		}
		return locator;
	}

	/**
	 * Returns the value of classes annotated with CssSelector, ex: @CssSelector("value")
	 * @param componentClass
	 * @return
	 */
	static String getCssSelector(Class<? extends MntlComponent> componentClass) {
		MntlComponentCssSelector annotation = componentClass.getAnnotation(MntlComponentCssSelector.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Mantle component class " + componentClass + " must have annotation "
					+ MntlComponentCssSelector.class.getSimpleName());
		}

		String locator = annotation.value();
		if (isBlank(locator)) {
			throw new IllegalArgumentException("Mantle component class " + componentClass + " annotation "
					+ MntlComponentCssSelector.class.getSimpleName() + " must have non blank string value for css selector, but was ["
					+ locator + "]");
		}
		return locator;
	}


	/**
	 * Returns the value of classes annotated with XpathSelector, ex: @CssSelector("value")
	 * @param componentClass
	 * @return
	 */
	static String getXpathSelector(Class<? extends MntlComponent> componentClass) {
		MntlComponentXpathSelector annotation = componentClass.getAnnotation(MntlComponentXpathSelector.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Mantle component class " + componentClass + " must have annotation "
				+ MntlComponentXpathSelector.class.getSimpleName());
		}

		String locator = annotation.value();
		if (isBlank(locator)) {
			throw new IllegalArgumentException("Mantle component class " + componentClass + " annotation "
				+ MntlComponentXpathSelector.class.getSimpleName() + " must have non blank string value for xpath selector, but was ["
				+ locator + "]");
		}
		return locator;
	}



	static By getLocator(Class<? extends MntlComponent> componentClass) {
		Annotation[] annotations = componentClass.getAnnotations();


		if (componentClass.isAnnotationPresent(MntlComponentId.class)) {
			return By.cssSelector("[id^=" + getId(componentClass) + "_]");
		} else if (componentClass.isAnnotationPresent(MntlComponentCssSelector.class)) {
			return By.cssSelector(getCssSelector(componentClass));
		} else if (componentClass.isAnnotationPresent(MntlComponentXpathSelector.class)) {
			return By.xpath(getXpathSelector(componentClass));
		} else {
			throw new IllegalArgumentException("Mantle component class " + componentClass + " must have annotation "
					+ MntlComponentId.class.getSimpleName() + " or " + MntlComponentCssSelector.class.getSimpleName());
		}

	}

	public static <T extends MntlComponent> BiFunction<WebDriverExtended, WebElementEx, T> getConstructorFunction(
			Class<T> componentClass) {

		Class<?>[] parameters = new Class[] { WebDriverExtended.class, WebElementEx.class };
		Constructor<T> constructor;
		try {
			constructor = componentClass.getConstructor(parameters);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Mantle component class x must have constructor with parameters: "
					+ parameters, e);
		}

		return new BiFunction<WebDriverExtended, WebElementEx, T>() {
			@Override
			public T apply(WebDriverExtended driver, WebElementEx element) {
				try {
					return constructor.newInstance(driver, element);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new IllegalArgumentException(
							"Error creating mantle component instance for " + componentClass, e);
				}
			}
		};
	}

}