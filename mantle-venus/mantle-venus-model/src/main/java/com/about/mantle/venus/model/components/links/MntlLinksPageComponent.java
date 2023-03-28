package com.about.mantle.venus.model.components.links;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.MntlComponentXpathSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentXpathSelector("//a")
public class MntlLinksPageComponent extends MntlComponent {

    private final Lazy<WebElementEx> image;

    public MntlLinksPageComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.image = lazy(() -> findElement(By.tagName("img")));
    }

    public Boolean hasImage() { return getElement().elementExists("img"); }

    public WebElementEx image() {
        return image.get();
    }

}
