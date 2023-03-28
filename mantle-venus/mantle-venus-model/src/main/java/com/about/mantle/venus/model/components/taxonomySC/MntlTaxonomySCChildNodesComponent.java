package com.about.mantle.venus.model.components.taxonomySC;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-taxonomysc-child-nodes")
public class MntlTaxonomySCChildNodesComponent extends MntlComponent {

    private Lazy<WebElementEx> heading;
    private Lazy<WebElementEx> moreButton;
    private Lazy<List<WebElementEx>> childNodes;

    public MntlTaxonomySCChildNodesComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.heading = lazy(() -> findElement(By.tagName("h3")));
        this.moreButton = lazy(() -> findElement(By.tagName("button")));
        this.childNodes = lazy(() -> findElements(By.cssSelector("a")));
    }

    public WebElementEx heading(){
        return heading.get();
    }
    
    public WebElementEx moreButton(){
        return moreButton.get();
    }

    public List<WebElementEx> childNodes(){
        return childNodes.get();
    }
}
