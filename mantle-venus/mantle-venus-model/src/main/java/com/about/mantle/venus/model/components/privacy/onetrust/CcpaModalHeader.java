package com.about.mantle.venus.model.components.privacy.onetrust;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector(".ot-pc-header")
public class CcpaModalHeader extends MntlComponent {

    private final Lazy<WebElementEx> ccpaLogo = lazy(() -> findElement(By.cssSelector("div.ot-pc-logo")));
    private final Lazy<WebElementEx> closeModalBtn = lazy(() -> findElement(By.cssSelector("button[id=close-pc-btn-handler]")));

    public CcpaModalHeader(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

    public WebElementEx ccpaLogo() {
        return this.ccpaLogo.get();
    }

    public WebElementEx closeModalBtn() {
        return this.closeModalBtn.get();
    }
}