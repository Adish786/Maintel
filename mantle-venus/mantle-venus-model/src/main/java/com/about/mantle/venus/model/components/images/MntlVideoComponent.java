package com.about.mantle.venus.model.components.images;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

@MntlComponentCssSelector("video")
public class MntlVideoComponent extends MntlComponent {

    public MntlVideoComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

}