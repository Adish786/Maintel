package com.about.mantle.venus.model.components.article;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.components.images.MntlImageComponent;
import com.about.mantle.venus.model.components.navigation.MntlSocialFollowNavComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector("#mntl-dynamic-tooltip")
public class MntlDynamicTooltipComponent extends MntlComponent {

	private final Lazy<MntlBioToolTipComponent> bioToolTip;
    private final Lazy<MntlDynamicTooltipContent> MntlDynamicTooltipContent;

    public MntlDynamicTooltipComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.MntlDynamicTooltipContent = lazy(
            () -> findElement(By.cssSelector(".mntl-dynamic-tooltip--content"), MntlDynamicTooltipContent::new));
        this.bioToolTip = lazy(() -> findComponent(MntlBioToolTipComponent.class));

    }

    public MntlDynamicTooltipContent MntlDynamicTooltipContent() {
        return MntlDynamicTooltipContent.get();
    }
    
    public MntlBioToolTipComponent bioToolTip() {
        return bioToolTip.get();
    }

    public static class MntlDynamicTooltipContent extends MntlDynamicTooltipComponent {

        private final Lazy<WebElementEx> content;
        private final Lazy<WebElementEx> contentLink;
        private final Lazy<TooltipTop> tooltipTop;
        private final Lazy<TooltipBottom> tooltipBottom;

        public MntlDynamicTooltipContent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.content = lazy(() -> findElement(By.tagName("p")));
            this.contentLink = lazy(() -> findElement(By.cssSelector("p > a")));
            this.tooltipTop = lazy(() -> findComponent(TooltipTop.class));
            this.tooltipBottom = lazy(() -> findComponent(TooltipBottom.class));
        }

        public WebElementEx content() {
            return content.get();
        }

        public WebElementEx contentLink() {
            return contentLink.get();
        }

        public TooltipTop tooltipTop() {
            return tooltipTop.get();
        }

        public TooltipBottom tooltipBottom() {
            return tooltipBottom.get();
        }

        @MntlComponentCssSelector(".mntl-author-tooltip__top")
        public static class TooltipTop extends MntlDynamicTooltipContent {

            private final Lazy<MntlImageComponent> image;
            private final Lazy<WebElementEx> name;
            private final Lazy<WebElementEx> date;
            private final Lazy<WebElementEx> bio;
            private final Lazy<WebElementEx> descriptor;
            private final Lazy<MntlSocialFollowNavComponent> socialNavItem;

            public TooltipTop(WebDriverExtended driver, WebElementEx element) {
                super(driver, element);
                this.image = lazy(() -> findElement(By.cssSelector(".img-placeholder img"), MntlImageComponent::new));
                this.name = lazy(() -> findElement(By.cssSelector("a")));
                this.date = lazy(() -> findElement(By.cssSelector(".mntl-attribution__item-date")));
                this.bio = lazy(() -> findElement(By.cssSelector(".mntl-author-tooltip__bio p")));
                this.descriptor = lazy(() -> findElement(By.cssSelector(".mntl-attribution__item-descriptor")));
                this.socialNavItem = lazy(() -> findComponent(MntlSocialFollowNavComponent.class));
            }

            public MntlImageComponent image() { return image.get(); }

            public String nameText() { return name.get().text(); }

            public WebElementEx name() { return name.get(); }

            public WebElementEx date() { return date.get(); }

            public WebElementEx bio() { return bio.get(); }

            public WebElementEx descriptor() { return descriptor.get(); }

            public boolean doesDateExist() { return getElement().elementExists(".mntl-attribution__item-date"); }

            public boolean hasSocialNav() { return getElement().elementExists(".mntl-social-nav"); }

            public MntlSocialFollowNavComponent socialNav() { return socialNavItem.get(); }

        }

        @MntlComponentCssSelector(".mntl-author-tooltip__bottom")
        public static class TooltipBottom extends MntlDynamicTooltipContent {

            private final Lazy<WebElementEx> moreText;
            private final Lazy<WebElementEx> link;

            public TooltipBottom(WebDriverExtended driver, WebElementEx element) {
                super(driver, element);
                this.moreText = lazy(() -> findElement(By.cssSelector(".mntl-author-tooltip__learn-more-text")));
                this.link = lazy(() -> findElement(By.tagName("a")));
            }

            public String moreText() { return moreText.get().text(); }

            public WebElementEx link() { return link.get(); }

            public String linkText() { return link.get().text(); }

        }

    }

}
