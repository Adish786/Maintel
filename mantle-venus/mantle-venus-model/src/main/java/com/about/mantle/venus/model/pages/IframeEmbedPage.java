package com.about.mantle.venus.model.pages;

import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Page;
import com.about.venus.core.utils.Lazy;
import org.openqa.selenium.By;

public class IframeEmbedPage extends Page {
	
	private final Lazy<WebElementEx> embedContent;


	public IframeEmbedPage(WebDriverExtended driver) {
		super(driver);
		this.embedContent = lazy(() -> findElement(By.className("mntl-embed-endpoint-content")));

	}
	
	public WebElementEx getEmbedContent() {
		return embedContent.get();
	}
		
	public String verifyEmbed() {
		 switch(getEmbedContent().getAttribute("data-provider")) {
         case "giphy":
             return getEmbedContent().findElementEx(By.tagName("img")).getAttribute("src");
         case "twitter":
         case "spotify":
         case "vimeo":
         case "instagram":
         case "tiktok":
         case "reddit":
         case "facebook-page":
         case "facebook-video":
         case "facebook-post":	
         case "youtube":
             return getEmbedContent().findElementEx(By.tagName("iframe")).getAttribute("src");

		 };
		 	return null;
	}

}
