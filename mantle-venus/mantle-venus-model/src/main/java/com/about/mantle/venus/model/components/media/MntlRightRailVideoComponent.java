package com.about.mantle.venus.model.components.media;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


@MntlComponentId("billboard2-sticky-dynamic")
public class MntlRightRailVideoComponent extends MntlInlineVideoComponent {

    private final Lazy<WebElementEx> videoBox;
    private final Lazy<WebElementEx> videoText;
    private final Lazy<WebElement> button;
    private final Lazy<WebElementEx> rrVideoTitle;
    private final Lazy<WebElementEx> playerTracker;
    private final Lazy<WebElementEx> fullScreen;
    private final Lazy<WebElementEx> ccButton;
    private final Lazy<WebElementEx> nextButton;
    private final Supplier<ShelfComponent> shelfComponent;
    private final Lazy<WebElementEx> horizontalSlider;
    private final Lazy<WebElementEx> knob;
    private final Lazy<WebElementEx> rrVideoControlBar;

    public MntlRightRailVideoComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.videoBox = lazy(() -> findElement(By.cssSelector(".jw-aspect.jw-reset")));
        this.videoText = lazy(() -> findElement(By.cssSelector(".rail-video__featured-text")));
        this.button = lazy(() -> findElement(By.cssSelector("button")));
        this.rrVideoTitle = lazy(() -> findElement(By.cssSelector(".rail-video__title")));
        this.playerTracker = lazy(() -> findElement(By.cssSelector(".jwplayer.jw-reset")));
        this.fullScreen = lazy(() -> findElement(By.cssSelector(".jw-icon-fullscreen")));
        this.ccButton = lazy(() -> findElement(By.cssSelector(".cc-icon")));
        this.nextButton = lazy(() -> findElement(By.cssSelector(".jw-icon.jw-icon-inline.jw-button-color.jw-reset.jw-icon-next")));
        this.shelfComponent = () -> findElement(By.cssSelector(".jw-related-shelf-contents"), ShelfComponent :: new);
        this.horizontalSlider = lazy(() -> findElement(By.cssSelector(".jw-slider-horizontal.jw-reset")));
        this.knob = lazy(() -> findElement(By.cssSelector(".jw-knob.jw-reset")));
        this.rrVideoControlBar = lazy(() -> findElement(By.cssSelector(".jw-reset.jw-button-container")));
    }

    public WebElementEx videoBox(){
        return this.videoBox.get();
    }

    public WebElementEx videoText(){
        return this.videoText.get();
    }

    public WebElement button(){
        return this.button.get();
    }

    public WebElementEx rrVideoTitle(){
        return this.rrVideoTitle.get();
    }

    public WebElementEx playerTracker(){
        return this.playerTracker.get();
    }

    public WebElementEx fullScreen(){
        return this.fullScreen.get();
    }

    public WebElementEx ccButton(){
        return this.ccButton.get();
    }

    public WebElementEx nextButton(){
        return this.nextButton.get();
    }

    public ShelfComponent shelfComponent() { return this.shelfComponent.get(); }

    public WebElementEx horizontalSlider() { return this.horizontalSlider.get(); }

    public WebElementEx knob() { return this.knob.get(); }

    public void loadRRVideo(WebElementEx component){
        component.scrollIntoViewCentered();
        component.waitFor().aMoment(5, TimeUnit.SECONDS);
    }

    public WebElementEx rrVideoControlBar() { return this.rrVideoControlBar.get(); }

    @MntlComponentCssSelector(".jw-related-shelf-contents")
    public static class ShelfComponent extends MntlComponent {
        private final Lazy<List<WebElementEx>> toggles;
        private final Lazy<WebElementEx> moreVideos;
        private final Lazy<WebElementEx> rightArrow;
        private final Lazy<WebElementEx> leftArrow;
        private final Lazy<WebElementEx> videoTitle;

        public ShelfComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.toggles = lazy(() -> findElements(By.cssSelector(".jw-related-shelf-item.jw-related-item-visible")));
            this.moreVideos = lazy(() -> findElement(By.cssSelector(".jw-related-more")));
            this.rightArrow = lazy(() -> findElement(By.cssSelector(".jw-related-control-right")));
            this.leftArrow = lazy(() -> findElement(By.cssSelector(".jw-related-control-left")));
            this.videoTitle = lazy(() -> findElement(By.cssSelector(".jw-related-shelf-item-title")));

        }
        public static final String MORE_VIDEOS = "More Videos";
        public List<WebElementEx> toggles() { return this.toggles.get(); }
        public WebElementEx moreVideos() { return this.moreVideos.get(); }
        public WebElementEx rightArrow() { return this.rightArrow.get(); }
        public WebElementEx lefArrow() { return this.leftArrow.get(); }
        public WebElementEx videoTitle() { return this.videoTitle.get(); }

    }


}


