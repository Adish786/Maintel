package com.about.mantle.venus.model.components;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-social-share")
public class MntlSocialShareComponent extends MntlComponent {

    private final Lazy<List<SocialShareItems>> socialShareItems;

    public MntlSocialShareComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.socialShareItems = lazy(() -> findComponents(SocialShareItems.class));
    }

    public List<SocialShareItems> socialShareItems() {
        return socialShareItems.get();
    }

    public WebElementEx socialShareDisplayed() {
        return getElement();
    }

    @MntlComponentCssSelector(".share-item")
    public static class SocialShareItems extends MntlComponent {

        private final Lazy<WebElementEx> shareLink;

        public SocialShareItems(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.shareLink = lazy(() -> findElement(By.cssSelector(".share-link")));
        }

        public WebElementEx shareLink() {
            return shareLink.get();
        }

    }

    public static class Form extends MntlComponent.AbstractForm {

        public Form(WebDriverExtended driver, WebElementEx element, String prefix) {
            super(driver, element, prefix);
        }

        public void networks(String networks) {
            field("networks", networks);
        }

    }
}