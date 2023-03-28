package com.about.mantle.venus.model.pages;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlPage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.utils.Lazy;

import java.util.List;

public class MntlBasePage<T extends MntlComponent> extends MntlPage {
	
	private final Lazy<T> component;
	private final Lazy<List<T>> components;
	private final Class<T> componentClass;

	public MntlBasePage(WebDriverExtended driver, Class<T> componentClass) {
		super(driver);
		this.componentClass = componentClass;
		this.component = lazy(() -> findComponent(componentClass));
		this.components = lazy(() -> findComponents(componentClass));
	}
	
	public T getComponent() {
		return component.get();
	}

	public List<T> getComponents() {
		return components.get();
	}

	public List<T> getComponentsRefreshed() {
		return findComponents(this.componentClass);
	}
}
