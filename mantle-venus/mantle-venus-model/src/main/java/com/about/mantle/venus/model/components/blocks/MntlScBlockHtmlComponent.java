package com.about.mantle.venus.model.components.blocks;

import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

/**
 * Component has id starting with mntl-sc-block and the class mntl-sc-block-html
 */
@MntlComponentCssSelector(".mntl-sc-block-html")
public class MntlScBlockHtmlComponent extends MntlComponent {
    private final Lazy<List<WebElementEx>> links;

    public MntlScBlockHtmlComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);

        this.links = lazy(() -> findElements(By.tagName("a")));
    }

    public List<WebElementEx> links(){
        return links.get();
    }
}
