package com.about.mantle.venus.model.components.forms;

import java.util.function.Supplier;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-email-form__manual-instructions")
public class MntlEmailFormManualInstructionsComponent extends MntlComponent {
	
	//lazy is not used due to dynamic value change
	private final Supplier<WebElementEx> tMogValue;
	private final Lazy<WebElementEx> emailForm;

	public MntlEmailFormManualInstructionsComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.emailForm = lazy(() -> findElement(By.cssSelector(".mntl-email-form__example")));
		this.tMogValue = () -> findElement(By.cssSelector(".mntl-email-form__text--tmog"));
	}

	public WebElementEx optionText() {
		return emailFormText().findElementEx(By.cssSelector(".mntl-option__text"));
	}

	public WebElementEx tMogValue() {
		return this.tMogValue.get();
	}

	public WebElementEx emailFormText() {
		return this.emailForm.get();
	}

}
