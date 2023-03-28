package com.about.mantle.components.frames;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector(".mntl-sc-block-cheetahembed")
public class MntlCheetahembedIframe extends MntlComponent {

    private Lazy<WebElementEx> iframe;

    public MntlCheetahembedIframe(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.iframe = lazy(() -> findElement(By.className("mntl-sc-block-cheetahembed__iframe")));
    }

    @Validate()
    public WebElementEx iframe() {
        return iframe.get();
    }
}
