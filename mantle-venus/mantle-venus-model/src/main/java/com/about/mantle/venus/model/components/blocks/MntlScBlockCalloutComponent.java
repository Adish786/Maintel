package com.about.mantle.venus.model.components.blocks;

import java.util.List;
import java.util.function.Supplier;

import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-sc-block-callout")
public class MntlScBlockCalloutComponent extends MntlComponent {
	private final Lazy<WebElementEx> heading;
	private final Lazy<MntlScBlockCalloutBodyComponent> body;
	private final Lazy<WebElementEx> icon;
	private final Lazy<WebElementEx> paraLink;
	private final Lazy<WebElementEx> phoneButton;
	private final Lazy<WebElementEx> phoneIcon;

	public MntlScBlockCalloutComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.heading = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-callout-heading")));
		this.body = lazy(
				() -> findElement(By.cssSelector(".mntl-sc-block-callout-body"), MntlScBlockCalloutBodyComponent::new));
		this.icon = lazy(() -> findElement(By.cssSelector(".icon")));
		this.paraLink = lazy(() -> findElement(By.cssSelector("p a")));
		this.phoneButton = lazy(() -> findElement(By.cssSelector(".btn")));
		this.phoneIcon = lazy(() -> findElement(By.cssSelector(".btn svg")));
	}

	public WebElementEx heading() {
		return heading.get();
	}
	public boolean hasHeading() {
		return getElement().elementExists(".mntl-sc-block-callout-heading");
	}
	public MntlScBlockCalloutBodyComponent body() {
		return body.get();
	}
	public WebElementEx icon() { return icon.get(); }
	public WebElementEx paraLink() { return paraLink.get(); }
	public WebElementEx phoneButton() { return phoneButton.get(); }
	public WebElementEx phoneIcon() { return phoneIcon.get(); }

	public static class MntlScBlockCalloutBodyComponent extends Component {
		private final Lazy<List<MntlSCParaListComponent>> paraList;
		private final Lazy<List<WebElementEx>> unorderedLists;
		private final Lazy<List<WebElementEx>> orderedLists;

		public MntlScBlockCalloutBodyComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.paraList = lazy(() -> findElements(By.tagName("p"), MntlSCParaListComponent::new));
			this.unorderedLists = lazy(() -> findElements(By.cssSelector("ul li")));
			this.orderedLists = lazy(() -> findElements(By.cssSelector("ol li")));
		}

		public List<MntlSCParaListComponent> paraList() {
			return paraList.get();
		}
		public List<WebElementEx> unorderedLists() {
			return unorderedLists.get();
		}
		public List<WebElementEx> orderedLists() {
			return orderedLists.get();
		}

	}

	public static class MntlSCParaListComponent extends MntlComponent {
		private final Lazy<WebElementEx> categoryHeadline;
		private final Lazy<WebElementEx> productLink;

		public MntlSCParaListComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.categoryHeadline = lazy(() -> findElement(By.cssSelector("strong:first-child")));
			this.productLink = lazy(() -> findElement(By.cssSelector("a")));
		}

		public WebElementEx categoryHeadline() {
			return categoryHeadline.get();
		}
		public WebElementEx productLink() {
			return productLink.get();
		}
	}

}
