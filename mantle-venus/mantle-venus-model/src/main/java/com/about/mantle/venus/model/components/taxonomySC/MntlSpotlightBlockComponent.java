package com.about.mantle.venus.model.components.taxonomySC;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.components.blocks.MntlCardComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-sc-block-spotlight")
public class MntlSpotlightBlockComponent extends MntlComponent {

    private final Lazy<WebElementEx> spotlightBlockHeader;
    private final Lazy<List<MntlCardComponent>> spotlightBlockItems;

    public MntlSpotlightBlockComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.spotlightBlockHeader = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-spotlight__heading")));
        this.spotlightBlockItems = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-spotlight__articles a"), MntlCardComponent::new));
    }

    public WebElementEx spotlightBlockHeader() { return spotlightBlockHeader.get(); }

    public List<MntlCardComponent> spotlightBlockItems() { return spotlightBlockItems.get(); }

}
