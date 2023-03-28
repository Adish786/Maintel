package com.about.mantle.venus.model.components.global;

import java.util.List;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("footer")
public class MntlFooterComponent extends MntlComponent {

    private Lazy<List<WebElementEx>> links;

    public MntlFooterComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

    public List<WebElementEx> links() {
        return links.get();
    }

}