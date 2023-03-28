package com.about.mantle.venus.model.components.blocks;

import com.about.venus.core.model.Component;
import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

import java.util.List;

@MntlComponentCssSelector(".mntl-sc-block-gallery")
public class MntlScBlockGalleryComponent extends MntlComponent {
	private final Lazy<List<Slide>> slides;
	private final Lazy<WebElementEx> slideCaption;
	private final Lazy<WebElementEx> slideOwner;
	private final Lazy<WebElementEx> slideCountCurrent;
	private final Lazy<WebElementEx> slideCountTotal;
	private final Lazy<WebElementEx> headline;	
	private final Lazy<CarouselSlide> carousel;
	private final Lazy<WebElementEx> rightArrow;
	private final Lazy<WebElementEx> leftArrow;

	public MntlScBlockGalleryComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.slides = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-galleryslide"), Slide::new));
		this.carousel = lazy(() -> findElement(By.cssSelector(".carousel"), CarouselSlide::new));
		this.slideCaption = lazy(() -> findElement(By.cssSelector(".slide-caption__desc")));
		this.slideOwner = lazy(() -> findElement(By.cssSelector(".slide-caption__owner")));
		this.slideCountCurrent = lazy(() -> findElement(By.cssSelector(".slide-counter__current")));
		this.slideCountTotal = lazy(() -> findElement(By.cssSelector(".slide-counter__total")));
		this.headline = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-gallery__heading")));
		this.rightArrow = lazy(() -> findElement(By.cssSelector(".slides__right-arrow")));
		this.leftArrow = lazy(() -> findElement(By.cssSelector(".slides__left-arrow")));
	}

	public List<Slide> slides() {
		return slides.get();
	}
	
	public CarouselSlide carousel() {
		return carousel.get();
	}
	
	public WebElementEx slideCaption() {
		return this.slideCaption.get();
	}
	
	public WebElementEx slideOwner() {
		return this.slideOwner.get();
	}
	
	public WebElementEx slideCountCurrent() {
		return this.slideCountCurrent.get();
	}
	
	public WebElementEx slideCountTotal() {
		return this.slideCountTotal.get();
	}
	
	public WebElementEx headline() {
		return this.headline.get();
	}

	public WebElementEx rightArrow() {
		return rightArrow.get();
	}
	
	public WebElementEx leftArrow() {
		return leftArrow.get();
	}
	
	public static class CarouselSlide extends Component {
		
		private final Lazy<List<WebElementEx>> item;
		private final Lazy<WebElementEx> rightArrow;
		private final Lazy<WebElementEx> leftArrow;
		
		public CarouselSlide(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.item = lazy(() -> findElements(By.cssSelector(".carousel__item")));
			this.rightArrow = lazy(() -> findElement(By.cssSelector(".right-arrow")));
			this.leftArrow = lazy(() -> findElement(By.cssSelector(".left-arrow")));
		}
		
		public List<WebElementEx> getItem() {
			return item.get();
		}
		
		public WebElementEx rightArrow() {
			return rightArrow.get();
		}
		
		public WebElementEx leftArrow() {
			return leftArrow.get();
		}
		
	}

	public static class Slide extends Component {
		
		private final Lazy<WebElementEx> mntlScBlock;

		public Slide(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.mntlScBlock = lazy(() -> findElement(By.cssSelector(".mntl-sc-block")));
		}

		public WebElementEx mntlScBlock() {
			return mntlScBlock.get();
		}
		
		public String getImgSrc() {
			return getElement().findElementEx(By.cssSelector("img")).getAttribute("src");
		}
		
		public String getImgDataOwner() {
			return getElement().findElementEx(By.cssSelector("img")).getAttribute("data-owner");
		}
		
	}
	
}