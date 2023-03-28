package com.about.mantle.venus.model.components.taxonomySC;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".fixedContent")
public class MntlTaxonomySCfixedContentComponent extends MntlComponent {

    private Lazy<MntlSpotlightBlockComponent> spotlightBlock;
    private Lazy<MntlTaxonomySCarticleListComponent> articleList;
    private Lazy<MntlTaxonomySCsiblingNodesComponent> siblingNodes;

    public MntlTaxonomySCfixedContentComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.spotlightBlock = lazy(() -> findComponent(MntlSpotlightBlockComponent.class));
        this.articleList = lazy(() -> findComponent(MntlTaxonomySCarticleListComponent.class));
        this.siblingNodes = lazy(() -> findComponent(MntlTaxonomySCsiblingNodesComponent.class));
    }

    public MntlSpotlightBlockComponent spotlightBlock() { return spotlightBlock.get(); }

    public MntlTaxonomySCarticleListComponent articleList() { return articleList.get(); }

    public MntlTaxonomySCsiblingNodesComponent siblingNodes() { return siblingNodes.get(); }

}
