package com.about.mantle.venus.model.components.privacy.onetrust;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector(".ot-pc-header")
public class GdprModalHeader extends MntlComponent {

    private final Lazy<WebElementEx> pcLogo = lazy(() -> findElement(By.cssSelector("div.ot-pc-logo")));
    private final Lazy<WebElementEx> pcTitle = lazy(() -> findElement(By.cssSelector("h2[id=ot-pc-title]")));
    private final Lazy<WebElementEx> closeModalBtn = lazy(() -> findElement(By.cssSelector("button[id=close-pc-btn-handler]")));

    public GdprModalHeader(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

    public WebElementEx pcLogo() {
        return this.pcLogo.get();
    }

    public WebElementEx pcTitle() {
        return this.pcTitle.get();
    }

    public WebElementEx closeModalBtn() {
        return this.closeModalBtn.get();
    }
}