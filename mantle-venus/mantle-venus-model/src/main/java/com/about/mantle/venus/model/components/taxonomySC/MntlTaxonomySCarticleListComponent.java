package com.about.mantle.venus.model.components.taxonomySC;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.components.blocks.MntlCardComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-taxonomysc-article-list")
public class MntlTaxonomySCarticleListComponent extends MntlComponent {

    private Lazy<WebElementEx> chopButton;
    private Lazy<List<MntlCardComponent>> cards;

    public MntlTaxonomySCarticleListComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.chopButton = lazy(() -> findElement(By.cssSelector(".mntl-card-list__button")));
        this.cards = lazy(() -> findElements(By.cssSelector(".mntl-card-list-items"), MntlCardComponent::new));
    }

    public WebElementEx chopButton() { return chopButton.get(); }

    public List<MntlCardComponent> articles() { return cards.get(); }

}
