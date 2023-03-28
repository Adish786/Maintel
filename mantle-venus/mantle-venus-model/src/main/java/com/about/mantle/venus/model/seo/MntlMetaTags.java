package com.about.mantle.venus.model.seo;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector(".head")
public class MntlMetaTags extends MntlComponent {

    private final Lazy<WebElementEx> ogImage;
    private final Lazy<WebElementEx> twitterImage;

    public MntlMetaTags(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.ogImage = lazy(() -> findElement(By.xpath("//meta[@property='og:image']")));
        this.twitterImage = lazy(() -> findElement(By.xpath("//meta[@name='twitter:image']")));
    }

    public WebElementEx ogImage() {
        return ogImage.get();
    }

    public WebElementEx twitterImage() {
        return twitterImage.get();
    }

    public boolean doesMetaOgImageExist() {
        return this.getElement().elementExists("meta[property=\"og:image\"]");
    }

    public boolean doesMetaTwitterImageExist() {
        return this.getElement().elementExists("meta[name=\"twitter:image\"]");
    }
}
