package com.about.mantle.venus.model.components.lists;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.components.blocks.MntlCardComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

public class MntlMasonryListComponent extends MntlComponent {
	
	private final Lazy<List<MntlCardComponent>> cards;

	public MntlMasonryListComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.cards = lazy(() -> findElements(By.className("mntl-card"), MntlCardComponent::new));
	}
	
	public List<MntlCardComponent> cards() {
		return cards.get();
	}

}
