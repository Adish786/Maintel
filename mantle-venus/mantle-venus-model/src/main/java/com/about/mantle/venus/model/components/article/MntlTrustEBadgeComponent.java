package com.about.mantle.venus.model.components.article;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.MntlPage;
import com.about.mantle.venus.model.components.buttons.ClickActions;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector(".mntl-truste-badge")
public class MntlTrustEBadgeComponent extends MntlComponent {

    private final Lazy<WebElementEx> trustEImage;

    public MntlTrustEBadgeComponent(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
        this.trustEImage = lazy(() -> findElement(By.cssSelector(".badge-image")));
    }

    public TrustEBadge trustEBadgeImage(){
        return new TrustEBadge(getDriver(), trustEImage.get());
    }

    public static class TrustEBadgePage extends MntlPage {
        public TrustEBadgePage(WebDriverExtended driver) {
            super(driver);
        }
    }

    public static class TrustEBadge extends ClickActions<TrustEBadgePage> {
        public TrustEBadge(WebDriverExtended driver, WebElementEx element) {
            super(driver, element);
        }

        @Override
        public TrustEBadgePage createPage() {
            return new TrustEBadgePage(getDriver());
        }
    }
}
