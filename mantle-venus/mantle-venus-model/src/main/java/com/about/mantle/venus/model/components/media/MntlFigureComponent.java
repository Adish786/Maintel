package com.about.mantle.venus.model.components.media;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.components.MntlJWVideoPlayerComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

public class MntlFigureComponent extends MntlComponent {

	private final Lazy<WebElementEx> image;
    private final Lazy<WebElementEx> caption;
    private final Lazy<MntlJWVideoPlayerComponent> videoPlayer;

    public MntlFigureComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.image = lazy(() -> findElement(By.tagName("img")));
        this.caption = lazy(() -> findElement(By.tagName("figcaption")));
        this.videoPlayer = lazy(() -> findComponent(MntlJWVideoPlayerComponent.class));
    }

    public MntlJWVideoPlayerComponent videoPlayer() {
        return videoPlayer.get();
    }

    public WebElementEx image() {
        return this.image.get();
    }

    public WebElementEx caption() {
        return this.caption.get();
    }
}
