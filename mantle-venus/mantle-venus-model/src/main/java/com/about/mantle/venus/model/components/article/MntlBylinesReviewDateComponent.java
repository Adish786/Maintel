package com.about.mantle.venus.model.components.article;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

@MntlComponentCssSelector(".mntl-attribution__item-date--review")
public class MntlBylinesReviewDateComponent extends MntlComponent {

    public MntlBylinesReviewDateComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

}
