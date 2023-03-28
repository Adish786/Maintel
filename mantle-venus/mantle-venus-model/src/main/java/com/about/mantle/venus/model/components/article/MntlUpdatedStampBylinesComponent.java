package com.about.mantle.venus.model.components.article;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

@MntlComponentCssSelector(".mntl-attribution__item-date")
public class MntlUpdatedStampBylinesComponent extends MntlComponent {

    public MntlUpdatedStampBylinesComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

}
