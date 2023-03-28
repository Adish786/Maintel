package com.about.mantle.venus.model.components.images;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-sc-block-image,.mntl-universal-image")
public class MntlScBlockImageComponent extends MntlComponent {

    private Lazy<MntlImageComponent> image;
    private Lazy<MntlVideoComponent> video;

    public MntlScBlockImageComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.image = lazy(() -> findComponent(MntlImageComponent.class));
        this.video = lazy(() -> findComponent(MntlVideoComponent.class));

    }

    public MntlImageComponent image() {
        return image.get();
    }

    public MntlVideoComponent video() {
        return video.get();
    }

    public boolean isVideo() {
        return getElement().elementExists("video");
    }

}