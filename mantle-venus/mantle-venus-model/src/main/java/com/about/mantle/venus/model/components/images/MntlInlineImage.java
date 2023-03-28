package com.about.mantle.venus.model.components.images;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-sc-block-image")
public class MntlInlineImage extends MntlComponent {

    private Lazy<MntlImageComponent> image;
    private final Lazy<List<WebElementEx>> inlineImages;
    private Lazy<WebElementEx> caption;

    public MntlInlineImage(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.image = lazy(() -> findComponent(MntlImageComponent.class));
        this.inlineImages = lazy(() -> findElements(By.cssSelector(".img-placeholder img")));
        this.caption = lazy(() -> findElement(By.tagName("figcaption")));
    }

    public MntlImageComponent image() {
        return image.get();
    }

    public List<WebElementEx> inlineImages() {
        return this.inlineImages.get();
    }

    public WebElementEx caption() {
        return this.caption.get();
    }
}