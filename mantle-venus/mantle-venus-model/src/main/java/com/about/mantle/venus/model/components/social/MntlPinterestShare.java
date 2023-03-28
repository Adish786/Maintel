package com.about.mantle.venus.model.components.social;

import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Page;
import com.about.mantle.venus.utils.MntlUrl;

public class MntlPinterestShare {

	public static class SharePage extends Page {

		public SharePage(WebDriverExtended driver) {
			super(driver);
		}
		
		public MntlUrl sharedUrl() {
			return new MntlUrl(getParamFromSharedUrl("url"));
		}
		
		public MntlUrl sharedMedia() {
			return new MntlUrl(getParamFromSharedUrl("media"));
		}
		
		private String getParamFromSharedUrl(String param) {
			MntlUrl url = new MntlUrl(getDriver().getCurrentUrl());
			MntlUrl sharedUrl = new MntlUrl(url.parameters().get("next").get(0));
			return sharedUrl.parameters().get(param).get(0);
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