package com.about.mantle.venus.model.components.taxonomySC;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.components.MntlJWVideoPlayerComponent;
import com.about.mantle.venus.model.components.MntlScBlockComponent;
import com.about.mantle.venus.model.components.media.MntlInlineVideoComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-taxonomysc-content")
public class MntlTaxonomySCcontentComponent extends MntlComponent {

    private Lazy<List<WebElementEx>> blocks;
    private Lazy<MntlJourneyNavComponent> journeyNav;
    private Lazy<MntlSpotlightBlockComponent> spotlightBlock;
    private Lazy<MntlTaxonomySCFAQBlockComponent> faqBlock;
    private Lazy<List<MntlInlineVideoComponent>> inlineVideoBlocks;

    public MntlTaxonomySCcontentComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.blocks = lazy(() -> findElements(By.cssSelector(".mntl-sc-block")));
        this.journeyNav = lazy(() -> findComponent(MntlJourneyNavComponent.class));
        this.spotlightBlock = lazy(() -> findComponent(MntlSpotlightBlockComponent.class));
        this.faqBlock = lazy(() -> findComponent(MntlTaxonomySCFAQBlockComponent.class));
        this.inlineVideoBlocks = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-inlinevideo"), MntlInlineVideoComponent::new));
    }

    public List<WebElementEx> blocks() { return blocks.get(); }

    public MntlJourneyNavComponent journeyNav() { return journeyNav.get(); }

    public MntlSpotlightBlockComponent spotlightBlock() { return spotlightBlock.get(); }
    
    public MntlTaxonomySCFAQBlockComponent faqBlock() { return faqBlock.get(); }

    public List<MntlInlineVideoComponent> inlineVideoBlocks() { return inlineVideoBlocks.get(); }

}
