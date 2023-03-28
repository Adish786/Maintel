package com.about.mantle.venus.model.components.forms;

import org.openqa.selenium.By;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

import java.util.List;
import java.util.function.Supplier;

@MntlComponentCssSelector(".mntl-privacy-email-form")
public class MntlPrivacyRequestComponent extends MntlComponent {

	private final Supplier<WebElementEx> requestSelect;
	private final Supplier<List<WebElementEx>> requestSelectoptions;
	private final Supplier<WebElementEx> nameInput;
	private final Supplier<WebElementEx> emailInput;
	private final Supplier<WebElementEx> submitButton;
	private final Supplier<WebElementEx> checkBox;
	private final Supplier<WebElementEx> emailFormExample;
	private final Supplier<WebElementEx> sucessMessage;

	public MntlPrivacyRequestComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.requestSelect = () -> findElement(By.cssSelector(".mntl-email-form__select"));
		this.requestSelectoptions = () -> findElements(By.cssSelector(".mntl-email-form__select--options"));
		this.nameInput = () -> findElement(By.cssSelector(".mntl-email-input__name"));
		this.emailInput = () -> findElement(By.cssSelector(".mntl-email-input__email"));
		this.submitButton = () -> findElement(By.cssSelector(".mntl-email-form__button"));
		this.checkBox = () -> findElement(By.cssSelector(".mntl-email-form__checkbox"));
		this.emailFormExample = () -> findElement(By.cssSelector(".mntl-email-form__example"));
		this.sucessMessage = () -> findElement(By.cssSelector(".mntl-email-form__message.mntl-email-form--success"));

	}

	public void submitRequest(String name, String emailAddress) {
		this.nameInputField().sendKeys(name);
		this.emailInputField().sendKeys(emailAddress);
		this.checkBox().click();
		this.clickSubmitButton();
	}

	@Validate
	public List<WebElementEx> requestSelectoptions() {
		return this.requestSelectoptions.get();
	}

	public WebElementEx requestSelect() {
		return this.requestSelect.get();
	}

	@Validate
	public WebElementEx nameInputField() {
		return this.nameInput.get();
	}

	@Validate
	public WebElementEx emailInputField() {
		return this.emailInput.get();
	}

	public WebElementEx emailFormExample() {
		return this.emailFormExample.get();
	}

	@Validate
	public WebElementEx submitButton() {
		return this.submitButton.get();
	}

	public void clickSubmitButton() {
		submitButton().scrollIntoViewCentered();
		waitFor().aMoment();
		submitButton().click();
	}

	@Validate
	public WebElementEx checkBox() {
		return checkBox.get();
	}

	public WebElementEx sucessMessage() {
		return sucessMessage.get();
	}

}
