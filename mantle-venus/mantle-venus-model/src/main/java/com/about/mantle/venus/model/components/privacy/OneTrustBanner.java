package com.about.mantle.venus.model.components.privacy;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector("#onetrust-consent-sdk")
public class OneTrustBanner extends MntlComponent {

    private final Lazy<WebElementEx> closeBannerBtn = lazy(()-> findElement(By.cssSelector("div[id=onetrust-close-btn-container] > button.onetrust-close-btn-handler")));
    private final Lazy<WebElementEx> policyBannerText = lazy(()-> findElement(By.cssSelector("div[id=onetrust-policy] > p[id=onetrust-policy-text]")));
    private final Lazy<WebElementEx> cookiesSettingsLink = lazy(()-> findElement(By.cssSelector("div[id=onetrust-policy] > a.ot-sdk-show-settings")));
    private final Lazy<WebElementEx> acceptCookiesBtn = lazy(()-> findElement(By.cssSelector("button[id=onetrust-accept-btn-handler]")));
    private final Lazy<WebElementEx> rejectAllBtn = lazy(()-> findElement(By.cssSelector("button[id=onetrust-reject-all-handler]")));
    private final Lazy<WebElementEx> cookiesSettingsBtn = lazy(()-> findElement(By.cssSelector("button[id=onetrust-pc-btn-handler]")));

    public OneTrustBanner(WebDriverExtended driver, WebElementEx element) {
        super(driver, element);
    }

    public WebElementEx closeBannerBtn(){return this.closeBannerBtn.get();}
    public WebElementEx policyBannerText(){return this.policyBannerText.get();}
    public WebElementEx cookiesSettingsLink(){return this.cookiesSettingsLink.get();}
    public WebElementEx acceptCookiesBtn(){return this.acceptCookiesBtn.get();}
    public WebElementEx rejectAllBtn(){return this.rejectAllBtn.get();}
    public WebElementEx cookiesSettingsBtn(){return this.cookiesSettingsBtn.get();}

}