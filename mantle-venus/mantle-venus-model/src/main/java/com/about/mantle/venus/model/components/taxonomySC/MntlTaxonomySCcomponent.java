package com.about.mantle.venus.model.components.taxonomySC;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.components.blocks.MntlCardComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-taxonomysc")
public class MntlTaxonomySCcomponent extends MntlComponent {

    private final Lazy<MntlTaxonomySCheaderComponent> header;
    private final Lazy<List<MntlCardComponent>> articles;
    private final Lazy<MntlTaxonomySCcontentComponent> content;
    private final Lazy<MntlTaxonomySCfixedContentComponent> fixedContent;
    private Lazy<WebElementEx> chopButton;

    public MntlTaxonomySCcomponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.fixedContent = lazy(() -> findComponent(MntlTaxonomySCfixedContentComponent.class));
        this.header = lazy(() -> findComponent(MntlTaxonomySCheaderComponent.class));
        this.articles = lazy(() -> findElements(By.cssSelector(".mntl-taxonomysc-article-list a"), MntlCardComponent::new));
        this.content = lazy(() -> findComponent(MntlTaxonomySCcontentComponent.class));
        this.chopButton = lazy(() -> findElement(By.cssSelector(".mntl-card-list__button")));
    }

    public MntlTaxonomySCheaderComponent header() {
        return header.get();
    }

    public List<MntlCardComponent> articles() {
        return articles.get();
    }

    public MntlTaxonomySCcontentComponent content() {
        return content.get();
    }

    public WebElementEx chopButton() { return chopButton.get(); }

    public Boolean isChopButtonPresent() { return getElement().elementExists(".mntl-card-list__button");}

    public MntlTaxonomySCfixedContentComponent fixedContent() { return fixedContent.get(); }

}