package com.about.mantle.venus.model.components.blocks;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector(".mntl-sc-block-digiohembed")
public class MntlDigiohEmbedComponent extends MntlComponent {

    private Lazy<WebElementEx> container;
    private Lazy<WebElementEx> iframe;

    public MntlDigiohEmbedComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.container = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-digiohembed__container")));
        this.iframe = lazy(() -> findElement(By.tagName("iframe")));
    }

    @Validate()
    public WebElementEx iframe() {
        return iframe.get();
    }

    @Validate()
    public WebElementEx container() {
        return container.get();
    }
}
