package com.about.mantle.venus.model.pages;

import com.about.mantle.venus.model.MntlPage;
import com.about.mantle.venus.model.components.structuredContent.MntlFeaturedLinkComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.utils.Lazy;

import java.util.List;

public class MntlFeaturedLinkPage extends MntlPage {

    private final Lazy<List<MntlFeaturedLinkComponent>> featuredLinkContents;

    public MntlFeaturedLinkPage(WebDriverExtended driver) {
        super(driver);
        this.featuredLinkContents = lazy(() -> findComponents(MntlFeaturedLinkComponent.class));
    }

    public List<MntlFeaturedLinkComponent> featuredLinkContent() {
        return featuredLinkContents.get();
    }

}
