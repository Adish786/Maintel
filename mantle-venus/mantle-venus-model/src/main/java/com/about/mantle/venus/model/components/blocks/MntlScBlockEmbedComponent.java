package com.about.mantle.venus.model.components.blocks;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.pages.IframeEmbedPage;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

@MntlComponentCssSelector(".mntl-sc-block-embed")
public class MntlScBlockEmbedComponent extends MntlComponent{
	private WebDriverExtended driver;
	public MntlScBlockEmbedComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.driver = driver;
	}
	
	public IframeEmbedPage switchToIframe() {
		String iframeId = getIframeId();
		return new IframeEmbedPage(this.driver.switchToFrame(By.id(iframeId)));
	}

	private String getIframeId() {
		String iframeId = getWebElement().getAttribute("id") + "-iframe";
		return iframeId;
	}
	
	public int iframeSize() {
		String iframeId = getIframeId();
		return findElement(By.id(iframeId)).getSize().height;
		
	}

	public WebDriverExtended switchBackFromIframe() {
		return driver.switchToDefaultContent();
		
	}	
	
	
}
