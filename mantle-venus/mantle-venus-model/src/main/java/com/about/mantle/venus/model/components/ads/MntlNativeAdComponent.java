package com.about.mantle.venus.model.components.ads;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

@MntlComponentCssSelector(".mntl-native")
public class MntlNativeAdComponent extends MntlComponent {

	private Lazy<WebElementEx> mntlNativeAdUnit;

	public MntlNativeAdComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.mntlNativeAdUnit = lazy(() -> findElement(By.cssSelector(".mntl-native__adunit")));
	}

	public WebElementEx mntlNativeAdUnit() {
		return mntlNativeAdUnit.get();
	}

}