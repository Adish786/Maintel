package com.about.mantle.venus.model.components.structuredContent;

import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.components.blocks.MntlScFeatureLinkContentBlock;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-sc-block-featuredlink")
public class MntlFeaturedLinkComponent extends MntlScFeatureLinkContentBlock {

    private Lazy<List<WebElementEx>> links;

    public MntlFeaturedLinkComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.links = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-featuredlink__link")));
    }

    public  List<WebElementEx> links() {
        return links.get();
    }
    
    public  WebElementEx link() {
        return links.get().get(0);
    }
}