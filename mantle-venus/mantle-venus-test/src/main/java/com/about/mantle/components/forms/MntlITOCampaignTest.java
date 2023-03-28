package com.about.mantle.components.forms;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.model.components.forms.MntlITOComponent;
import com.about.mantle.venus.model.pages.MntlBasePage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.model.Component;
import com.google.common.base.Predicate;
import org.junit.Assert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.emptyOrNullString;

public class MntlITOCampaignTest extends MntlVenusTest implements MntlCommonTestMethods {

    public static class TestData {
        private long timeoutTime;
        private boolean hasSubscriptionItems = false;
        private boolean hasErrorMessageClose = false;
        private boolean hasErrorMessageIcon = false;
        private boolean hasNoThanksButton = false;
        private String heading = "";
        private String title = "";
        private String subTitle = "";
        private String description = "";
        private String successMessage = "";
        private ArrayList<String> subscriptionText;
        private Class<? extends Component> suppressionComponent;

        public TestData() { }

        public TestData timeoutTime(long timeoutTime) {
            this.timeoutTime = timeoutTime;
            return this;
        }

        public TestData hasSubscriptionItems(boolean hasSubscriptionItems) {
            this.hasSubscriptionItems = hasSubscriptionItems;
            return this;
        }

        public TestData hasErrorMessageClose(boolean hasErrorMessageClose) {
            this.hasErrorMessageClose = hasErrorMessageClose;
            return this;
        }

        public TestData hasErrorMessageIcon(boolean hasErrorMessageIcon) {
            this.hasErrorMessageIcon = hasErrorMessageIcon;
            return this;
        }

        public TestData hasNoThanksButton(boolean hasNoThanksButton) {
            this.hasNoThanksButton = hasNoThanksButton;
            return this;
        }

        public TestData heading(String heading) {
            this.heading = heading;
            return this;
        }

        public TestData title(String title) {
            this.title = title;
            return this;
        }

        public TestData subTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public TestData description(String description) {
            this.description = description;
            return this;
        }

        public TestData successMessage(String successMessage) {
            this.successMessage = successMessage;
            return this;
        }

        public TestData subscriptionText(ArrayList<String> subscriptionText) {
            this.subscriptionText = subscriptionText;
            return this;
        }

        public TestData suppressionComponent(Class<? extends Component> suppressionComponent) {
            this.suppressionComponent = suppressionComponent;
            return this;
        }
    }

    public void testIto(MntlITOComponent itoComponent, TestData data, MntlRunner runner){
        WebDriverExtended driver = runner.driver();
        MntlITOComponent finalItoComponent1 = itoComponent;
        driver.waitFor((Predicate<WebDriver>) webDriver -> finalItoComponent1.displayed(), (int) data.timeoutTime);
        driver.waitFor((Predicate<WebDriver>) webDriver -> finalItoComponent1.dialogSubmit().isDisplayed(), (int) data.timeoutTime);

        positiveDialogTest(finalItoComponent1, data, runner);

        driver.manage().deleteCookie(driver.manage().getCookieNamed("ddmCampaignSession"));
        driver.manage().deleteCookie(driver.manage().getCookieNamed("ddmCampaignExtended"));
        driver.manage().deleteCookie(driver.manage().getCookieNamed("hid"));
        driver.waitFor(3, TimeUnit.SECONDS);

        runner.onComponent(MntlITOComponent.class).withITO().loadUrl();
        itoComponent = (MntlITOComponent) runner.component();
        MntlITOComponent finalItoComponent2 = itoComponent;

        driver.waitFor((Predicate<WebDriver>) webDriver -> finalItoComponent2.displayed(), (int) data.timeoutTime);
        driver.waitFor((Predicate<WebDriver>) webDriver -> finalItoComponent2.dialogSubmit().isDisplayed(), (int) data.timeoutTime);
        negativeDialogTest(finalItoComponent2, data);
    }

    public void positiveDialogTest(MntlITOComponent itoComponent, TestData data, MntlRunner runner){
        WebDriverExtended driver = runner.driver();
        checkVisibility(itoComponent, true, data);
        checkMainText(itoComponent, data);

        itoComponent.dialogEmailInput().sendKeys("test@test.com");
        itoComponent.waitFor().aMoment();
        itoComponent.dialogSubmit().scrollIntoView();
        itoComponent.dialogSubmit().click();
        driver.waitFor((Predicate<WebDriver>) webDriver -> itoComponent.dialogSuccessButton().isDisplayed(), 10);

        if(!data.successMessage.isEmpty()) {
            collector.checkThat("Dialog success message not visible", itoComponent.dialogSuccessMessage().isDisplayed(), is(true));
            collector.checkThat("Dialog success message text not matching expected", itoComponent.dialogSuccessMessage().text(), containsString(data.successMessage));
        }
        collector.checkThat("Dialog success button not visible", itoComponent.dialogSuccessButton().isDisplayed(), is(true));

        itoComponent.dialogSuccessButton().click();
        checkVisibility(itoComponent, false, data);
    }

    public void negativeDialogTest(MntlITOComponent itoComponent, TestData data){
        itoComponent.waitFor().aMoment();
        checkVisibility(itoComponent, true, data);
        checkMainText(itoComponent, data);

        if(data.hasSubscriptionItems){
            for(int i=0; i<itoComponent.dialogSubscriptionItems().size(); i++){
                itoComponent.dialogSubscriptionItems().get(i).subscriptionCheckbox().click();
                itoComponent.dialogSubmit().click();
                itoComponent.waitFor().aMoment();
            }

            collector.checkThat("Subscription error message not visible", itoComponent.subscriptionErrorMessage().isDisplayed(), is(true));

            if(data.hasErrorMessageIcon){
                collector.checkThat("Subscription error message icon not visible", itoComponent.subscriptionErrorIcon().isDisplayed(), is(true));
            }

            if(data.hasErrorMessageClose) {
                collector.checkThat("Subscription error message close button not visible", itoComponent.subscriptionErrorClose().isDisplayed(), is(true));
                collector.checkThat("Subscription error message close button is empty (aria-label attribute is null or empty)", itoComponent.subscriptionErrorClose().getAttribute("aria-label"), not(emptyOrNullString()));
                itoComponent.subscriptionErrorClose().click();
                itoComponent.waitFor().aMoment();
                collector.checkThat("Subscription error message still visible after close button click", itoComponent.subscriptionErrorMessage().isDisplayed(), is(false));
                collector.checkThat("Error message close button still visible after click", itoComponent.subscriptionErrorClose().isDisplayed(), is(false));

                if(data.hasErrorMessageIcon){
                    collector.checkThat("Subscription error message icon still visible after close button click", itoComponent.subscriptionErrorIcon().isDisplayed(), is(false));
                }
            }
        }else{
            itoComponent.dialogSubmit().click();
            itoComponent.waitFor().aMoment();
        }

        testEmailInputError(itoComponent, data);

        itoComponent.dialogEmailInput().sendKeys("incorrect email");
        itoComponent.dialogSubmit().click();

        testEmailInputError(itoComponent, data);

        if(data.hasNoThanksButton){
            itoComponent.dialogNoThanksButton().click();
        }else{
            itoComponent.dialogClose().click();
        }
        checkVisibility(itoComponent, false, data);
    }

    public void checkVisibility(MntlITOComponent itoComponent, boolean isVisible, TestData data){
        boolean hasSubscriptionItems = data.hasSubscriptionItems;
        if(!data.heading.isEmpty()) {
            collector.checkThat("Dialog heading visibility failed to match expected", itoComponent.dialogHeading().isDisplayed(), is(isVisible));
        }

        if(!data.title.isEmpty()) {
            collector.checkThat("Dialog title visibility failed to match expected", itoComponent.dialogTitle().isDisplayed(), is(isVisible));
        }

        if(!data.subTitle.isEmpty()) {
            collector.checkThat("Dialog subtitle visibility failed to match expected", itoComponent.dialogSubTitle().isDisplayed(), is(isVisible));
        }

        if(!data.description.isEmpty()) {
            collector.checkThat("Dialog description visibility failed to match expected", itoComponent.dialogDescription().isDisplayed(), is(isVisible));
        }

        collector.checkThat("Dialog email input visibility failed to match expected", itoComponent.dialogEmailInput().isDisplayed(), is(isVisible));

        if(hasSubscriptionItems) {
            for (int i = 0; i < itoComponent.dialogSubscriptionItems().size(); i++) {
                MntlITOComponent.SubscriptionItems subscriptionItem = itoComponent.dialogSubscriptionItems().get(i);

                collector.checkThat("Subscription checkbox visibility input failed to match expected", subscriptionItem.subscriptionCheckbox().isDisplayed(), is(isVisible));
                collector.checkThat("Subscription label visibility failed to match expected", subscriptionItem.subscriptionLabel().isDisplayed(), is(isVisible));
            }
        }

        collector.checkThat("Dialog email submit button visibility failed to match expected", itoComponent.dialogSubmit().isDisplayed(), is(isVisible));
        collector.checkThat("Dialog close button visibility failed to match expected", itoComponent.dialogClose().isDisplayed(), is(isVisible));
        collector.checkThat("Dialog close button is empty (aria-label attribute is null or empty)", itoComponent.dialogClose().getAttribute("aria-label"), not(emptyOrNullString()));
    }

    public void checkMainText(MntlITOComponent itoComponent, TestData data){
        if(!data.heading.isEmpty()){
            collector.checkThat("Heading text doesn't match", itoComponent.dialogHeading().text(), containsString(data.heading));
        }

        if(!data.title.isEmpty()){
            collector.checkThat("Title text doesn't match", itoComponent.dialogTitle().text(), containsString(data.title));
        }

        if(!data.subTitle.isEmpty()){
            collector.checkThat("Subtitle text doesn't match", itoComponent.dialogSubTitle().text(), containsString(data.subTitle));
        }

        if(!data.description.isEmpty()){
            collector.checkThat("Description text doesn't match", itoComponent.dialogDescription().text(), containsString(data.description));
        }

        if(data.hasSubscriptionItems){
            checkSubscriptionsText(itoComponent, data);
        }
    }

    public void checkSubscriptionsText(MntlITOComponent itoComponent, TestData data){
        for (int i = 0; i<itoComponent.dialogSubscriptionItems().size(); i++){
            collector.checkThat("Subscription text doesn't match", itoComponent.dialogSubscriptionItems().get(i).text(), containsString(data.subscriptionText.get(i)));
        }
    }

    public void testEmailInputError(MntlITOComponent itoComponent, TestData data){
        collector.checkThat("Error message not visible", itoComponent.emailInputErrorMessage().isDisplayed(), is(true));

        if(data.hasErrorMessageIcon){
            collector.checkThat("Error message icon not visible", itoComponent.emailInputErrorIcon().isDisplayed(), is(true));
        }

        if(data.hasErrorMessageClose) {
            collector.checkThat("Error message close button not visible", itoComponent.emailInputErrorClose().isDisplayed(), is(true));
            collector.checkThat("Error message close button is empty (aria-label attribute is null or empty)", itoComponent.emailInputErrorClose().getAttribute("aria-label"), is(emptyOrNullString()));
            itoComponent.emailInputErrorClose().click();
            itoComponent.waitFor().aMoment();
            collector.checkThat("Error message still visible after close button click", itoComponent.emailInputErrorMessage().isDisplayed(), is(false));
            collector.checkThat("Error message close button still visible after click", itoComponent.emailInputErrorClose().isDisplayed(), is(false));

            if(data.hasErrorMessageIcon){
                collector.checkThat("Error message icon still visible after close button click", itoComponent.emailInputErrorIcon().isDisplayed(), is(false));
            }
        }
    }

    public void testItoSuppression(MntlITOComponent itoComponent, TestData data, MntlRunner runner){
        if(data.suppressionComponent != null) {
            try {
                MntlBasePage<? extends Component> basePage = new MntlBasePage(runner.driver(), data.suppressionComponent);
                Component component = basePage.getComponent();
                if (component.displayed()) {
                    component.scrollIntoViewCentered();
                }

            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        }

        try {
            runner.driver().waitFor((Predicate<WebDriver>) webDriver -> itoComponent.className().contains("dialog--visible"), (int) data.timeoutTime);
        }catch (TimeoutException ignored){

        }

        collector.checkThat("ITO component not being suppressed and still visible.", itoComponent.className().contains("dialog--visible"), is(false));
    }

    public Consumer<MntlRunner> testIto = (runner) -> {
        MntlITOComponent itoComponent = (MntlITOComponent) runner.component();
        TestData data = (TestData) runner.testData();
        testIto(itoComponent, data, runner);
    };

    public Consumer<MntlRunner> testItoSuppression = (runner) -> {
        MntlITOComponent itoComponent = (MntlITOComponent) runner.component();
        TestData data = (TestData) runner.testData();
        testItoSuppression(itoComponent, data, runner);
    };
}
