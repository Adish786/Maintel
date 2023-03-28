package com.about.mantle.venus.model.components.forms;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-dialog--campaign")
public class MntlITOComponent extends MntlComponent {
    private final Lazy<WebElementEx> dialogHeading;
    private final Lazy<WebElementEx> dialogLogo;
    private final Lazy<WebElementEx> dialogTitle;
    private final Lazy<WebElementEx> dialogSubTitle;
    private final Lazy<WebElementEx> dialogDescription;
    private final Lazy<WebElementEx> dialogEmailInput;
    private final Lazy<List<SubscriptionItems>> dialogSubscriptionItems;
    private final Lazy<WebElementEx> dialogSubmit;
    private final Lazy<WebElementEx> dialogClose;
    private final Lazy<WebElementEx> dialogNoThanksButton;
    private final Lazy<WebElementEx> dialogSuccessMessage;
    private final Lazy<WebElementEx> dialogSuccessButton;
    private final Lazy<WebElementEx> emailInputErrorMessage;
    private final Lazy<WebElementEx> emailInputErrorIcon;
    private final Lazy<WebElementEx> emailInputErrorClose;
    private final Lazy<WebElementEx> subscriptionErrorMessage;
    private final Lazy<WebElementEx> subscriptionErrorIcon;
    private final Lazy<WebElementEx> subscriptionErrorClose;

    public MntlITOComponent(WebDriverExtended driver, WebElementEx element){
        super(driver, element);
        this.dialogHeading = lazy(() -> findElement(By.cssSelector(".dialog__title")));
        this.dialogLogo = lazy(() -> findElement(By.cssSelector(".newsletter__logo svg")));
        this.dialogTitle = lazy(() -> findElement(By.cssSelector(".newsletter__form .newsletter__title")));
        this.dialogSubTitle = lazy(() -> findElement(By.cssSelector(".newsletter__form .newsletter__subtitle")));
        this.dialogDescription = lazy(() -> findElement(By.cssSelector(".newsletter__form .newsletter__description")));
        this.dialogEmailInput = lazy(() -> findElement(By.cssSelector(".newsletter__email-address-input")));
        this.dialogSubscriptionItems = lazy(() -> findElements(By.cssSelector(".newsletter__subscriptions-item"), SubscriptionItems::new));
        this.dialogSubmit = lazy(() -> findElement(By.cssSelector(".newsletter__email-address-button")));
        this.dialogClose = lazy(() -> findElement(By.cssSelector(".dialog__close")));
        this.dialogNoThanksButton = lazy(() -> findElement(By.cssSelector(".newsletter__close-button")));
        this.dialogSuccessMessage = lazy(() -> findElement(By.cssSelector(".newsletter__form-success-message")));
        this.dialogSuccessButton = lazy(() -> findElement(By.cssSelector(".newsletter__form-success-button")));
        this.emailInputErrorMessage = lazy(() -> findElement(By.cssSelector(".js-email-input-error .message-banner--error .message-banner__text")));
        this.emailInputErrorIcon = lazy(() -> findElement(By.cssSelector(".js-email-input-error .message-banner--error .message-banner__prefix-icon")));
        this.emailInputErrorClose = lazy(() -> findElement(By.cssSelector(".js-email-input-error .message-banner--error .message-banner__close-button")));
        this.subscriptionErrorMessage = lazy(() -> findElement(By.cssSelector(".js-subscriptions-error .message-banner--error .message-banner__text")));
        this.subscriptionErrorIcon = lazy(() -> findElement(By.cssSelector(".js-subscriptions-error .message-banner--error .message-banner__prefix-icon")));
        this.subscriptionErrorClose = lazy(() -> findElement(By.cssSelector(".js-subscriptions-error .message-banner--error .message-banner__close-button")));
    }

    public WebElementEx dialogHeading(){
        return this.dialogHeading.get();
    }

    public WebElementEx dialogLogo(){
        return this.dialogLogo.get();
    }

    public WebElementEx dialogTitle(){
        return this.dialogTitle.get();
    }

    public WebElementEx dialogSubTitle(){
        return this.dialogSubTitle.get();
    }

    public WebElementEx dialogDescription(){
        return this.dialogDescription.get();
    }

    public WebElementEx dialogEmailInput(){
        return this.dialogEmailInput.get();
    }

    public List<SubscriptionItems> dialogSubscriptionItems(){
        return this.dialogSubscriptionItems.get();
    }

    public WebElementEx dialogSubmit(){
        return this.dialogSubmit.get();
    }

    public WebElementEx dialogClose(){
        return this.dialogClose.get();
    }

    public WebElementEx dialogNoThanksButton(){
        return this.dialogNoThanksButton.get();
    }

    public WebElementEx dialogSuccessMessage(){
        return this.dialogSuccessMessage.get();
    }

    public WebElementEx dialogSuccessButton(){
        return this.dialogSuccessButton.get();
    }

    public WebElementEx emailInputErrorMessage(){
        return this.emailInputErrorMessage.get();
    }

    public WebElementEx emailInputErrorIcon(){
        return this.emailInputErrorIcon.get();
    }

    public WebElementEx emailInputErrorClose(){
        return this.emailInputErrorClose.get();
    }

    public WebElementEx subscriptionErrorMessage(){
        return this.subscriptionErrorMessage.get();
    }

    public WebElementEx subscriptionErrorIcon(){
        return this.subscriptionErrorIcon.get();
    }

    public WebElementEx subscriptionErrorClose (){
        return this.subscriptionErrorClose .get();
    }


    public static class SubscriptionItems extends Component {
        private final Lazy<WebElementEx> subscriptionLabel;
        private final Lazy<WebElementEx> subscriptionCheckbox;

        public SubscriptionItems(WebDriverExtended driver, WebElementEx element){
            super(driver, element);
            this.subscriptionLabel = lazy(() -> findElement(By.cssSelector(".newsletter__label ")));
            this.subscriptionCheckbox = lazy(() -> findElement(By.cssSelector(".newsletter__checkbox")));
        }

        public WebElementEx subscriptionLabel(){
            return this.subscriptionLabel.get();
        }

        public WebElementEx subscriptionCheckbox(){
            return this.subscriptionCheckbox.get();
        }
    }
}
