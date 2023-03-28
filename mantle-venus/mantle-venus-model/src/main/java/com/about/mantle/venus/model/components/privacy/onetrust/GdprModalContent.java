package com.about.mantle.venus.model.components.privacy.onetrust;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.components.privacy.optanon.GdprCategoryItems;
import com.about.mantle.venus.model.components.privacy.optanon.GdprDescriptionCenter;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector("div#ot-pc-content")
public class GdprModalContent extends MntlComponent {

    private final Lazy<GdprCategoryItems> categoryItems = lazy(() -> findComponent(GdprCategoryItems.class));
    private final Lazy<GdprDescriptionCenter> descriptionCenter = lazy(() -> findComponent(GdprDescriptionCenter.class));

    public GdprModalContent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

    public GdprCategoryItems categoryItems() {
        return this.categoryItems.get();
    }

    public GdprDescriptionCenter descriptionCenter() {
        return this.descriptionCenter.get();
    }
}