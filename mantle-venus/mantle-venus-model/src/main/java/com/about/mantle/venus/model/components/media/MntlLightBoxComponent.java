package com.about.mantle.venus.model.components.media;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;

public class MntlLightBoxComponent extends Component{

	private final Lazy<WebElementEx> imageLightboxClose;
	private final Lazy<WebElementEx> mntlLightbox;
	private final Lazy<WebElementEx> lightboxNextArrow;
	private final Lazy<WebElementEx> lightboxPrevArrow;
	private final Lazy<WebElementEx> lightboxCaption;
	private final Lazy<WebElementEx> mntlLightboxImg;
	
	public MntlLightBoxComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.imageLightboxClose = lazy(() -> findElement(By.className("mntl-lightbox__close")));
		this.mntlLightbox = lazy(() -> findElement(By.className("mntl-lightbox")));
    	this.lightboxNextArrow = lazy(() -> findElement(By.className("mntl-lightbox__next-arrow")));
    	this.lightboxPrevArrow = lazy(() -> findElement(By.className("mntl-lightbox__prev-arrow"))); 
    	this.lightboxCaption = lazy(() -> findElement(By.cssSelector(".mntl-lightbox__caption")));  
    	this.mntlLightboxImg = lazy(() -> findElement(By.className("mntl-lightbox__img")));
	}

	public WebElementEx imageLightboxClose() {
        return this.imageLightboxClose.get();
    }
	
	
	public WebElementEx mntlLightbox() {
        return this.mntlLightbox.get();
    }
	
	 public WebElementEx lightboxNextArrow() {
	        return this.lightboxNextArrow.get();
	    }
	 
	 public WebElementEx lightboxPrevArrow() {
	        return this.lightboxPrevArrow.get();
	    }
	 
	 public WebElementEx lightboxCaption() {
	        return this.lightboxCaption.get();
	    }
	 
	 public void rightKeyPress() {
		 lightboxNextArrow().sendKeys(Keys.RIGHT);

		}
	 
	 public void leftKeyPress() {
		 lightboxPrevArrow().sendKeys(Keys.LEFT);

		}
	 
	 public WebElementEx mntlLightboxImg() {
	        return this.mntlLightboxImg.get();
	    }
	 
}
