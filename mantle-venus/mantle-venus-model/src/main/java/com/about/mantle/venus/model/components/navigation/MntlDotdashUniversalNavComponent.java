package com.about.mantle.venus.model.components.navigation;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-dotdash-universal-nav")
public class MntlDotdashUniversalNavComponent extends MntlComponent {

    private final Lazy<WebElementEx> dotdashLogo;
    private final Lazy<WebElementEx> title;
    private final Lazy<WebElementEx> titleLink;

    public MntlDotdashUniversalNavComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.dotdashLogo = lazy(() -> findElement(By.cssSelector(".mntl-dotdash-universal-nav__logo")));
        this.title = lazy(() -> findElement(By.cssSelector(".mntl-dotdash-universal-nav__text")));
        this.titleLink = lazy(() -> findElement(By.cssSelector(".mntl-dotdash-universal-nav__text--link")));
    }

    @Validate
    public WebElementEx titleLink() {
        return titleLink.get();
    }

    @Validate
    public WebElementEx title() { return title.get(); }

    @Validate
    public WebElementEx dotdashLogo() {
        return dotdashLogo.get();
    }
}
