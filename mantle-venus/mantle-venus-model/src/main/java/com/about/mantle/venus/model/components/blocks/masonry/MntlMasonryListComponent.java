package com.about.mantle.venus.model.components.blocks.masonry;

import java.util.List;
import java.util.function.BiFunction;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;

public class MntlMasonryListComponent extends MntlComponent {

	public MntlMasonryListComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}

	public <T extends MntlComponent> T item(Class<T> componentClass) {
		return findComponent(componentClass);
	}
	
	public <T extends Component> T item(By by, BiFunction<WebDriverExtended, WebElementEx, T> function) {
		return findElement(by,function);
	}
	
	public <T extends MntlComponent> List<T> items(Class<T> componentClass) {
		return findComponents(componentClass);
	}
	
	public <T extends Component> List<T> items(By by, BiFunction<WebDriverExtended, WebElementEx, T> function) {
		return findElements(by,function);
	}
}
