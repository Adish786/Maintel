package com.about.mantle.venus.model.components.ads;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.DataAttributes;
import com.about.venus.core.utils.Lazy;

public class MntlAdComponent extends MntlComponent {
	
	private final Lazy<DataAttributes> dataAttributes;
	private final Lazy<WebElementEx> google_ads_iframe;

	public MntlAdComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.dataAttributes = lazy(() -> new DataAttributes(element));
		this.google_ads_iframe = lazy(() -> findElement(By.tagName("iframe")));
	}
	
	@Override
	public boolean displayed() {
		String adHeight = dataAttributes.get().data("ad-height");
    	if(adHeight != null ) {
    		int height = Integer.parseInt(dataAttributes.get().data("ad-height"));
    		if(height < 10) {
    			return true;
    		} else {
    			return getElement().isDisplayed();
    		}
    	} else {
    		return getElement().isDisplayed();
    	}
	}
	
	public WebElementEx google_ads_iframe() {
		return google_ads_iframe.get();
	}

}
