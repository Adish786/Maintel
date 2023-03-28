package com.about.mantle.venus.model.components.blocks;

import java.util.List;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

@MntlComponentCssSelector(".mntl-sc-block-keyterms")
public class MntlKeyTermsBlock extends MntlComponent{

	private final Lazy<WebElementEx> title;
	private final Lazy<List<WebElementEx>> navItems;
	private final Lazy<MntlKeyTermsCarouselComponent> carouselComponent;
	
	public MntlKeyTermsBlock(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.title = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-keyterms__title")));
		this.navItems = lazy(() -> findElements(By.tagName("button")));
		this.carouselComponent = lazy(() -> findElement
				(By.cssSelector(".mntl-sc-block-keyterms__carousel"),MntlKeyTermsCarouselComponent::new));
	}
	
	public WebElementEx title() {
		return title.get();
	}
	
	public List<WebElementEx> navItems() {
		return navItems.get();
	}
	
	public MntlKeyTermsCarouselComponent carouselComponent() {
		return carouselComponent.get();
	}
	
	@MntlComponentCssSelector(".mntl-sc-block-keyterms__carousel")
	public static class MntlKeyTermsCarouselComponent extends MntlComponent{
		private final Lazy<List<WebElementEx>> carouselTitles;
		private final Lazy<WebElementEx> carouselCard;
		private final Lazy<WebElementEx> rightArrow;
		private final Lazy<WebElementEx> leftArrow;
		private final Lazy<List<WebElementEx>> links;
		private final Lazy<List<WebElementEx>> body;

		public MntlKeyTermsCarouselComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.carouselTitles = lazy(() -> findElements(By.cssSelector(".carousel-card__title")));
			this.rightArrow = lazy(() -> findElement(By.cssSelector(".mntl-carousel__arrow--right")));
			this.carouselCard = lazy(() -> findElement(By.cssSelector(".carousel-card")));
			this.leftArrow = lazy(() -> findElement(By.cssSelector(".mntl-carousel__arrow--left")));
			this.links = lazy(() -> findElements(By.cssSelector(".carousel-card__link-wrapper")));
			this.body = lazy(() -> findElements(By.tagName("p")));
		}
		
		public List<WebElementEx> carouselTitles() {
			return carouselTitles.get();
		}
		
		public WebElementEx rightArrow() {
			return rightArrow.get();
		}
		
		public WebElementEx leftArrow() {
			return leftArrow.get();
		}
		
		public WebElementEx carouselCard() {
			return carouselCard.get();
		}
		
		public List<WebElementEx> links() {
			return links.get();
		}
		
		public List<WebElementEx> body() {
			return body.get();
		}
		
	}
}
