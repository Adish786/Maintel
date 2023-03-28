package com.about.mantle.venus.model.components.blocks;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-sponsorships")
public class MntlMultipleSponsorshipComponent extends MntlComponent {

    private final Lazy<WebElementEx> title;
    private final Lazy<List<MntlMultipleSponsorshipComponentItem>> itemLinks;

    public MntlMultipleSponsorshipComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.title = lazy(() -> findElement(By.cssSelector(".sponsorship-item__title")));
        this.itemLinks = lazy(() -> findElements(By.cssSelector(".sponsorship-item__link"), MntlMultipleSponsorshipComponentItem::new));

    }

    public WebElementEx title() {
        return title.get();
    }

    public List<MntlMultipleSponsorshipComponentItem> itemLinks() {
        return itemLinks.get();
    }

    @MntlComponentCssSelector(".sponsorship-item__link")
    public class MntlMultipleSponsorshipComponentItem extends MntlComponent {
        private final Lazy<WebElementEx> logo;
        public MntlMultipleSponsorshipComponentItem(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.logo = lazy(() -> findElement(By.cssSelector(".sponsorship-item__logo")));
        }
        public WebElementEx logo() {
            return logo.get();
        }
    }
}
