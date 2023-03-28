package com.about.mantle.venus.model.components.sections.conversation;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-conversation")
public class MntlConversationComponent extends MntlComponent {

    public Lazy<List<ConvoSectionComponent>> convoSections;

    public MntlConversationComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        convoSections = lazy(() -> findComponents(ConvoSectionComponent.class));
    }

    public List<ConvoSectionComponent> convoSections() {
        return convoSections.get();
    }

    @MntlComponentCssSelector(".convo-section")
    public static class ConvoSectionComponent extends MntlComponent {

        private Lazy<ConvoAdviceComponent> convoAdvice;
        private Lazy<List<ConvoResponseComponent>> convoResponses;
        private Lazy<ConvoChoicesComponent> convoChoices;
        private Lazy<ConvoTakeawayComponent> convoTakeaway;

        public ConvoSectionComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            convoAdvice = lazy(() -> findComponent(ConvoAdviceComponent.class));
            convoResponses = lazy(() -> findComponents(ConvoResponseComponent.class));
            convoChoices = lazy(() -> findComponent(ConvoChoicesComponent.class));
            convoTakeaway = lazy(() -> findComponent(ConvoTakeawayComponent.class));
        }

        public ConvoAdviceComponent convoAdvice() {
            return convoAdvice.get();
        }

        public List<ConvoResponseComponent> convoResponses() {
            return convoResponses.get();
        }

        public ConvoChoicesComponent convoChoices() {
            return convoChoices.get();
        }

        public ConvoTakeawayComponent convoTakeaway() {
            return convoTakeaway.get();
        }
    }

    @MntlComponentCssSelector(".convo__advice")
    public static class ConvoAdviceComponent extends MntlComponent {

        private Lazy<WebElementEx> adviceSpeaker;
        private Lazy<WebElementEx> adviceSpeakerImage;
        private Lazy<WebElementEx> adviceText;

        public ConvoAdviceComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            adviceSpeaker = lazy(() -> findElement(By.cssSelector(".advice__speaker")));
            adviceSpeakerImage = lazy(() -> findElement(By.cssSelector(".advice__speaker-img")));
            adviceText = lazy(() -> findElement(By.cssSelector(".advice__text")));
        }

        public WebElementEx adviceSpeaker() {
            return adviceSpeaker.get();
        }

        public WebElementEx adviceSpeakerImage() {
            return adviceSpeakerImage.get();
        }

        public WebElementEx adviceText() {
            return adviceText.get();
        }
    }

    @MntlComponentCssSelector(".convo__response")
    public static class ConvoResponseComponent extends MntlComponent {

        private Lazy<WebElementEx> responseSpeaker;
        private Lazy<WebElementEx> responseText;

        public ConvoResponseComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            responseSpeaker = lazy(() -> findElement(By.cssSelector(".convo__speaker-name")));
            responseText = lazy(() -> findElement(By.cssSelector(".convo__text-bubble")));
        }

        public WebElementEx responseSpeaker() {
            return responseSpeaker.get();
        }

        public WebElementEx responseText() {
            return responseText.get();
        }
    }

    @MntlComponentCssSelector(".convo__choices")
    public static class ConvoChoicesComponent extends MntlComponent {

        private Lazy<WebElementEx> choiceHeading;
        private Lazy<List<WebElementEx>> choicebuttons;

        public ConvoChoicesComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            choiceHeading = lazy(() -> findElement(By.cssSelector(".choice__heading")));
            choicebuttons = lazy(() -> findElements(By.cssSelector(".choice__button")));
        }

        public WebElementEx choiceHeading() {
            return choiceHeading.get();
        }

        public List<WebElementEx> choicebuttons() {
            return choicebuttons.get();
        }
    }

    @MntlComponentCssSelector(".convo__takeaway")
    public static class ConvoTakeawayComponent extends MntlComponent {

        private Lazy<WebElementEx> takeawayHeading;
        private Lazy<WebElementEx> takeawayText;
        private Lazy<List<WebElementEx>> takeawayButtons;

        public ConvoTakeawayComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            takeawayHeading = lazy(() -> findElement(By.cssSelector(".takeaway__heading")));
            takeawayText = lazy(() -> findElement(By.cssSelector(".takeaway__text")));
            takeawayButtons = lazy(() -> findElements(By.cssSelector(".takeaway__button")));
        }

        public WebElementEx takeawayHeading() {
            return takeawayHeading.get();
        }

        public WebElementEx takeawayText() {
            return takeawayText.get();
        }

        public List<WebElementEx> takeawayButtons() {
            return takeawayButtons.get();
        }
    }
}
