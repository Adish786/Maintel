package com.about.mantle.venus.model.components.forms;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-newsletter-signup")
public class MntlNewsletterSignupComponent extends MntlComponent {

	private final Lazy<WebElementEx> heading;
	private final Lazy<WebElementEx> emailInputLabel;
	private final Lazy<WebElementEx> emailInputField;
	private final Lazy<WebElementEx> submitButton;
	private final Lazy<WebElementEx> oneTapButton;
	private final Lazy<WebElementEx> successMessage;
	private final Lazy<WebElementEx> errorMessage;

	public MntlNewsletterSignupComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.heading = lazy(() -> findElement(By.className("mntl-newsletter-signup__heading")));
		this.emailInputLabel = lazy(() -> findElement(By.className("mntl-newsletter-submit__label")));
		this.emailInputField = lazy(() -> findElement(By.className("mntl-newsletter-submit__input")));
		this.submitButton = lazy(() -> findElement(By.className("mntl-newsletter-submit__button")));
		this.oneTapButton = lazy(() -> findElement(By.className("mntl-newsletter-one-tap")));
		this.successMessage = lazy(() -> findElement(By.className("mntl-newsletter-signup__success")));
		this.errorMessage = lazy(() -> findElement(By.className("mntl-newsletter-signup__error")));
	}

	public void subscribe(String emailAddress) {
		this.emailInputField().sendKeys(emailAddress);
		this.submitButton().click();
	}

	public WebElementEx heading() {
		return this.heading.get();
	}

	public WebElementEx emailInputLabel() {
		return this.emailInputLabel.get();
	}

	public WebElementEx emailInputField() {
		return this.emailInputField.get();
	}

	public WebElementEx submitButton() {
		return this.submitButton.get();
	}

	public WebElementEx signupSuccess() {
		return successMessage.get();
	}

	public WebElementEx signupError() {
		return errorMessage.get();
	}

	public WebElementEx oneTap() {
		return oneTapButton.get();
	}
}