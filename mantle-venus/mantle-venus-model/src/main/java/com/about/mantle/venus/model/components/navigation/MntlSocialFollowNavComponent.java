package com.about.mantle.venus.model.components.navigation;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-social-nav")
public class MntlSocialFollowNavComponent extends MntlComponent {

    private Lazy<List<MntlSocialNavItemComponent>> socialNavItems;

    public MntlSocialFollowNavComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.socialNavItems = lazy(() -> findComponents(MntlSocialNavItemComponent.class));
    }

    public List<MntlSocialNavItemComponent> socialNavItems() { return socialNavItems.get(); }

    @MntlComponentCssSelector(".social-nav__link")
    public static class MntlSocialNavItemComponent extends MntlComponent {

        private Lazy<WebElementEx> icon;
        private Lazy<WebElementEx> label;

        public MntlSocialNavItemComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.icon = lazy(() -> findElement(By.tagName("svg")));
            this.label = lazy(() -> findElement(By.tagName("span")));
        }

        public WebElementEx icon() {
            return icon.get();
        }

        public WebElementEx label() {
            return label.get();
        }

    }

}
