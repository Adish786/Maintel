package com.about.mantle.venus.model.components.taxonomySC;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-taxonomysc-sibling-nodes")
public class MntlTaxonomySCsiblingNodesComponent extends MntlComponent {

    private Lazy<WebElementEx> title;
    private Lazy<List<WebElementEx>> nodes;

    public MntlTaxonomySCsiblingNodesComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.title = lazy(() -> findElement(By.cssSelector(".mntl-taxonomysc-sibling-nodes__title")));
        this.nodes = lazy(() -> findElements(By.cssSelector(".mntl-taxonomysc-sibling-node")));
    }

    public WebElementEx title() { return title.get(); }

    public List<WebElementEx> nodes() {return nodes.get(); }

}
