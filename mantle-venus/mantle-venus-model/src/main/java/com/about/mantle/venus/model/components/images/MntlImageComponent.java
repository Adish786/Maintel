package com.about.mantle.venus.model.components.images;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

@MntlComponentCssSelector("img")
public class MntlImageComponent extends MntlComponent {

    public MntlImageComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

    public boolean hasWidth() { return getElement().getAttributes().containsKey("width"); }

    public boolean hasHeight() { return getElement().getAttributes().containsKey("height"); }

    public String getWidth() { return getElement().getAttributes().get("width"); }

    public String getHeight() { return getElement().getAttributes().get("height"); }

    public Boolean hasDataSrc() { return getElement().getAttributes().containsKey("data-src"); }

    public String getDataSrc() { return getElement().getAttribute("data-src"); }

}
