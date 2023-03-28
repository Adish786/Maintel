package com.about.mantle.venus.model.components.images;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector(".mntl-gif__video")
public class MntlGifVideoComponent extends MntlComponent {

    public MntlGifVideoComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }
}
