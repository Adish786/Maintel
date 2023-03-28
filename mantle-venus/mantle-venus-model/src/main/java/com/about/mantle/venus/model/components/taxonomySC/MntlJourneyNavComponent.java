package com.about.mantle.venus.model.components.taxonomySC;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-sc-block-journeynav")
public class MntlJourneyNavComponent extends MntlComponent {

    private final Lazy<WebElementEx> journeyNavHeader;
    private final Lazy<List<MntlJourneyNavItemComponent>> journeyNavItems;

    public MntlJourneyNavComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.journeyNavHeader = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-journeynav__heading")));
        this.journeyNavItems = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-journeynav__list li"), MntlJourneyNavItemComponent::new));
    }

    public WebElementEx journeyNavHeader(){ return journeyNavHeader.get(); }

    public boolean isJourneyHeaderPresent() { return getElement().elementExists(".mntl-sc-block-journeynav__heading"); }

    public List<MntlJourneyNavItemComponent> journeyNavItems() { return journeyNavItems.get(); }

    public static class MntlJourneyNavItemComponent extends MntlComponent {

        private final Lazy<WebElementEx> journeyNavLink;

        public MntlJourneyNavItemComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.journeyNavLink = lazy(() -> findElement(By.cssSelector("a")));
        }

        public WebElementEx journeyNavLink() {
            return journeyNavLink.get();
        }

    }

}
