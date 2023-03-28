package com.about.mantle.venus.model.components.ads;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.DataAttributes;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-sc-sticky-billboard")
public class MntlSCStickyBillboardComponent extends MntlAdComponent {
	
	private final Lazy<WebElementEx> mntlSCStickyBilboardAd; 

	public MntlSCStickyBillboardComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.mntlSCStickyBilboardAd = lazy(() -> findElement(By.cssSelector(".mntl-sc-sticky-billboard-ad")));
	}
	
	public WebElementEx mntlSCStickyBilboardAd() {
		return mntlSCStickyBilboardAd.get();
	}

}
