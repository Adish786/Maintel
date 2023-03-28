package com.about.mantle.venus.model.components.guideCrumb;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;

import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import java.util.List;
import org.openqa.selenium.By;

@MntlComponentCssSelector(".guide-crumb")
public class MntlGuideCrumbComponent extends MntlComponent{
    private Lazy<WebElementEx> preText;
    private Lazy<WebElementEx> guideCrumbMain;
    private Lazy<WebElementEx> guideCrumbButton;
    private Lazy<WebElementEx> guideCrumbButtonIcon;
    private Lazy<WebElementEx> guideCrumbDropDown;
    private Lazy<List<JourneyNavSublist>> journeyNavSublistItems;
    private Lazy<List<WebElementEx>> journeyLinks;

    public MntlGuideCrumbComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.preText = lazy(() -> findElement(By.cssSelector(".guide-crumb__pre-text")));
        this.guideCrumbMain = lazy(() -> findElement(By.cssSelector(".mntl-guide-crumb")));
        this.guideCrumbButton = lazy(() -> findElement(By.cssSelector(".mntl-guide-crumb__button")));
        this.guideCrumbButtonIcon = lazy(() -> findElement(By.cssSelector(".mntl-button__icon")));
        this.guideCrumbDropDown = lazy(() -> findElement(By.cssSelector(".mntl-guide-crumb__dropdown")));
        this.journeyNavSublistItems = lazy(() -> findComponents(JourneyNavSublist.class));
        this.journeyLinks = lazy(() -> findElements(By.cssSelector("a")));
    }

    public WebElementEx preText() { return preText.get(); }

    public WebElementEx guideCrumbMain() { return guideCrumbMain.get(); }

    public WebElementEx guideCrumbButton() { return guideCrumbButton.get(); }

    public WebElementEx guideCrumbButtonIcon() { return guideCrumbButtonIcon.get(); }

    public WebElementEx guideCrumbDropDown() { return guideCrumbDropDown.get(); }

    public List<JourneyNavSublist> journeyNavSublistItems() { return journeyNavSublistItems.get(); }

    public List<WebElementEx> journeyLinks() { return journeyLinks.get(); }


    @MntlComponentCssSelector(".journey-nav__sublist-item")
    public static class JourneyNavSublist extends MntlComponent {

        private Lazy<WebElementEx> journeyNavSublistAnchor;

        public JourneyNavSublist(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.journeyNavSublistAnchor = lazy(() -> findElement(By.cssSelector(".journey-nav__sublist-item > a")));
        }

        public WebElementEx journeyNavSublistAnchor() { return journeyNavSublistAnchor.get(); }
    }
}
