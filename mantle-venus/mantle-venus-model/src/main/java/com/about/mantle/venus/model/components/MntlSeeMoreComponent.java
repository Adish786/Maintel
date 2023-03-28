package com.about.mantle.venus.model.components;

import java.util.function.BiFunction;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("auto-see-more-1")
public class MntlSeeMoreComponent extends MntlComponent {

	private final Lazy<WebElementEx> seeMoreButton;

	public MntlSeeMoreComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.seeMoreButton = lazy(() -> findElement(By.className("see-more-btn-container")));
	}

	public Component seeMoreButton() {
		return new Component(getDriver(), seeMoreButton.get());
	}

	public Component seeMoreText() {
		return new Component(getDriver(), seeMoreButton.get().findElementEx(By.cssSelector("span.text")));
	}

	public <T extends Content> T seeMoreContent(BiFunction<WebDriverExtended, WebElementEx, T> function) {
		return function.apply(getDriver(), findElement(By.className("see-more-content")));
	}

	public static interface Content {

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