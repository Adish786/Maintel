package com.about.mantle.venus.model.components.buttons;

import java.util.List;

import org.openqa.selenium.By;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Page;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-sc-block-commerce__button")
public class MntlScBLockCommerceButton extends MntlComponent{
	
	private Lazy<WebElementEx> span;
	private Lazy<List<WebElementEx>> priceTags;

	public MntlScBLockCommerceButton(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.span = lazy(() -> findElement(By.tagName("span")));
		this.priceTags = lazy(() -> findElements(By.className("commerce-widget__price-tag")));
	}
	
	public WebElementEx span() {
		return span.get();
	}
	
	public WebElementEx priceTag() {
		return priceTags().get(0);
	}
	
	public List<WebElementEx> priceTags() {
		return priceTags.get();
	}

	public boolean hasPrice() {
		return this.getWebElement().getAttributes().containsKey("data-commerce-price");
	}

	public boolean isPrime() {
		return this.className().contains("mntl-commerce-btn--is-prime");
	}
	
	public Retailer retailer(WebElementEx element) {
		return new Retailer(getDriver(), element);
	}
	
	public static class RetailerPage extends Page {

		public RetailerPage(WebDriverExtended driver) {
			super(driver);
		}
	}
	
	public boolean hasRedirect() {
		   return this.getWebElement().getAttributes().containsKey("data-click-href");
		}

	public static class Retailer extends ClickActions<RetailerPage> {

		public Retailer(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
		}

		@Override
		public RetailerPage createPage() {
			return new RetailerPage(getDriver());
		}
	}


}
