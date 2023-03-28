package com.about.mantle.venus.model.components.taxonomySC;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

import java.util.List;

@MntlComponentCssSelector(".mntl-taxonomysc-header")
public class MntlTaxonomySCheaderComponent extends MntlComponent {

    private final Lazy<HeaderTopComponent> headerTop;
    private final Lazy<HeaderBottomComponent> headerBottom;

    public MntlTaxonomySCheaderComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.headerTop = lazy(() -> findComponent(HeaderTopComponent.class));
        this.headerBottom = lazy(() -> findComponent(HeaderBottomComponent.class));
    }

    public HeaderTopComponent headerTop() {
        return headerTop.get();
    }

    public HeaderBottomComponent headerBottom() {
        return headerBottom.get();
    }

    @MntlComponentCssSelector(".mntl-taxonomysc-header__top")
    public static class HeaderTopComponent extends MntlComponent {

        private Lazy<WebElementEx> headerTopImage;
        private Lazy<List<Breadcrumb>> breadcrumbs;
        private Lazy<WebElementEx> heading;
        private Lazy<WebElementEx> subHeading;
        private Lazy<WebElementEx> headerInfo;

        public HeaderTopComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.headerTopImage = lazy(() -> findElement(By.cssSelector(".mntl-taxonomysc-image")));
            this.breadcrumbs = lazy(() -> findElements(By.cssSelector(".mntl-breadcrumbs"), Breadcrumb::new));
            this.heading = lazy(() -> findElement(By.cssSelector(".mntl-taxonomysc-heading")));
            this.subHeading = lazy(() -> findElement(By.cssSelector(".mntl-taxonomysc-subheading")));
            this.headerInfo = lazy(() -> findElement(By.cssSelector(".mntl-updated-stamp__text")));
        }

        public String headerTopImageSrc() {
            return headerTopImage.get().getAttribute("src");
        }

        public List<Breadcrumb> breadcrumbs() { return breadcrumbs.get(); }

        public String headingText() { return heading.get().getText(); }

        public String subHeadingText() { return subHeading.get().getText(); }

        public boolean hasSubheading() { return getElement().elementExists(".mntl-taxonomysc-subheading"); }

        public boolean hasBreadcrumbs() { return getElement().elementExists(".mntl-breadcrumbs a"); }

        public String headerInfoStampText() { return headerInfo.get().getText(); }

        public static class Breadcrumb extends Component {

            private Lazy<WebElementEx> breadcrumbItem;

            public Breadcrumb(WebDriverExtended driver, WebElementEx element) {
                super(driver, element);
                this.breadcrumbItem = lazy(() -> findElement(By.cssSelector(".mntl-breadcrumbs__item a")));
            }

            public String linkHrefText() {
                return breadcrumbItem.get().getAttribute("href");
            }

        }

    }

    @MntlComponentCssSelector(".mntl-taxonomysc-header__bottom")
    public static class HeaderBottomComponent extends MntlComponent {

        private Lazy<WebElementEx> taxonomyIntro;

        public HeaderBottomComponent(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
            this.taxonomyIntro = lazy(() -> findElement(By.cssSelector(".mntl-taxonomysc-intro p")));
        }

        public String taxonomyIntroText() {
            return taxonomyIntro.get().getText();
        }

    }

}
