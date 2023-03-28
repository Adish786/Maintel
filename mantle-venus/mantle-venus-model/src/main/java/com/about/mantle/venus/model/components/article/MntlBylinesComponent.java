package com.about.mantle.venus.model.components.article;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

@MntlComponentCssSelector(".mntl-bylines")
public class MntlBylinesComponent extends MntlComponent {

    public MntlBylinesComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

}

