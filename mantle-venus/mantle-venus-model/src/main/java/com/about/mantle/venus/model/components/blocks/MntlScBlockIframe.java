package com.about.mantle.venus.model.components.blocks;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-sc-block-iframe")
public class MntlScBlockIframe extends MntlComponent {
	
	private Lazy<WebElementEx> iFrameCaption;
	private Lazy<WebElementEx> iFrame;

	public MntlScBlockIframe(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.iFrame = lazy(() -> findElement(By.className("mntl-sc-block-iframe__uri")));
		this.iFrameCaption = lazy(() -> findElement(By.className("mntl-sc-block-iframe__caption")));
		
	}
	
	@Validate()
	public WebElementEx iFrameCaption() {
		return iFrameCaption.get();
	}
	
	@Validate()
	public WebElementEx iFrame() {
		return iFrame.get(); 
	}

}
