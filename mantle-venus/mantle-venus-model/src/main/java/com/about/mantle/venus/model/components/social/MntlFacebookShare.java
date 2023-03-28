package com.about.mantle.venus.model.components.social;

import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Page;
import com.about.mantle.venus.utils.MntlUrl;

public class MntlFacebookShare {
	public static class SharePage extends Page {

		public SharePage(WebDriverExtended driver) {
			super(driver);
		}
		
		public MntlUrl sharedUrl() {
			MntlUrl url = new MntlUrl(getDriver().getCurrentUrl());
			String formattedUrl  = url.parameters().get("next").get(0);
			
			String sharedUrl = new MntlUrl(formattedUrl).parameters().get("href").get(0);
			return new MntlUrl(sharedUrl);
		}
		
		public String appId() {
			MntlUrl url = new MntlUrl(getDriver().getCurrentUrl());
			return url.parameters().get("api_key").get(0);
		}
		
	}
	
	public static class ShareButton extends ClickShareActions<SharePage> {

		public ShareButton(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
		}

		@Override
		public SharePage createPage() {
			return new SharePage(getDriver());
		}
	}

}
