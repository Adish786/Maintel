package com.about.mantle.venus.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.openqa.selenium.By;

import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

public class PatternLibFormComponent extends MntlComponent {

	protected String partId;
	private final Lazy<WebElementEx> update;

	public PatternLibFormComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		getElement().scrollIntoView();
		this.partId = "pl-edit";
		this.update = lazy(() -> findElement(By.cssSelector("input.pl-button")));
	}

	public <T extends AbstractForm> T form(Class<T> formClass) {

		@SuppressWarnings("unchecked")
		Class<? extends MntlComponent> componentClass = (Class<? extends MntlComponent>) formClass.getEnclosingClass();
		String id = MntlUtils.getId(componentClass);

		try {
			Constructor<T> constructor = formClass.getConstructor(new Class[] { WebDriverExtended.class,
					WebElementEx.class, String.class });
			return constructor.newInstance(getDriver(), getElement(), partId + "_" + id);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public WebElementEx updateButton() {
		return update.get();
	}
	
	public void update() {
		update.get().submit();
	}
}
