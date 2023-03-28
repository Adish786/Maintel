package com.about.mantle.venus.model.components.media;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.Validate;
import com.about.mantle.venus.model.components.MntlJWVideoPlayerComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-sc-block-inlinevideo")
public class MntlInlineVideoComponent extends MntlJWVideoPlayerComponent {

    private final Lazy<WebElementEx> videoTitle;
    private final Lazy<WebElementEx> inlineVideoCaption;
    private final Lazy<WebElementEx> inlineVideoFeaturedLink;
    private final Lazy<MntlJWVideoPlayerComponent> inlineVideoComponent;

    public MntlInlineVideoComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.videoTitle = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-inlinevideo__title")));
        this.inlineVideoCaption = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-inlinevideo__caption")));
        this.inlineVideoFeaturedLink = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-inlinevideo__feature-link-wrapper a")));
        this.inlineVideoComponent = lazy(() -> findComponent(MntlScBlockInlineVideoComponent.class));
    }

    @Validate
    public WebElementEx videoTitle() { return videoTitle.get(); }

    public WebElementEx inlineVideoCaption() { return inlineVideoCaption.get(); }

    public WebElementEx inlineVideoFeaturedLink() { return inlineVideoFeaturedLink.get(); }

    public MntlJWVideoPlayerComponent inlineVideoComponent() { return inlineVideoComponent.get(); }

}