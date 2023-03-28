package com.about.mantle.venus.model.components.buttons;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-sc-block-commerce")
public class MntlScBlockCommerce extends MntlComponent{
	
	private Lazy<List<WebElementEx>> button;
	private final Lazy<List<WebElementEx>> productButton;
	private final Lazy<List<WebElementEx>> allButtons;

	public MntlScBlockCommerce(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.button = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-commerce__button")));
		this.productButton = lazy(() -> findElements(By.cssSelector(".product-record__commerce-buttons a")));
		this.allButtons = lazy(() -> findElements(By.cssSelector(".product-record__commerce-buttons a, .mntl-sc-block-commerce__button")));
		
	}

	public List<WebElementEx> productButton() {
		return this.productButton.get();
	}
	public List<WebElementEx> buttons() {
		return button.get();
	}
	
	public List<WebElementEx> allButtons() {
		return allButtons.get();
	}
	
	@Validate()
	public WebElementEx button() {
		return button.get().get(0);
	}
	
}
