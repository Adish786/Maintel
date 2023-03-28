package com.about.mantle.venus.model.components;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("sidebar_1")
public class MntlSidebarComponent extends MntlComponent {

	private final Lazy<WebElementEx> h1;

	public MntlSidebarComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.h1 = lazy(() -> findElement(By.tagName("h1")));
	}

	public String header() {
		return h1.get().getText();
	}

	public static class Form extends MntlComponent.AbstractForm {

		protected Form(WebDriverExtended driver, WebElementEx element, String prefix) {
			super(driver, element, prefix);
		}

		public void height(int height) {
			field("height", height);
		}

	}
}