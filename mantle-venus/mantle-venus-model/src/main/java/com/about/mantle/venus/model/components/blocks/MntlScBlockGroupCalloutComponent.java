package com.about.mantle.venus.model.components.blocks;

import com.about.venus.core.model.Component;
import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentCssSelector;
import com.about.mantle.venus.model.Validate;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.utils.Lazy;

import java.util.List;

@MntlComponentCssSelector(".mntl-sc-block-groupcallout")
public class MntlScBlockGroupCalloutComponent extends MntlComponent {

	private final Lazy<List<Callout>> callouts;
	private final Lazy<WebElementEx> title;
	private final Lazy<WebElementEx> subheading;

	public MntlScBlockGroupCalloutComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.callouts = lazy(() -> findElements(By.cssSelector(".mntl-sc-block-groupcallout-items"), Callout::new));
		this.title = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-groupcallout__title")));
		this.subheading = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-groupcallout__subheading")));
	}

	public WebElementEx title() {return  title.get(); }
	public WebElementEx subheading() {return  subheading.get(); }
	public boolean hasTitle() {
		return getElement().elementExists(".mntl-sc-block-groupcallout__title");
	}
	public boolean hasSubHeading() {
		return getElement().elementExists(".mntl-sc-block-groupcallout__subheading");
	}

	@Validate
	public List<Callout> callouts() {
		return callouts.get();
	}

	public static class Callout extends Component {
		private final Lazy<WebElementEx> heading;
		private final Lazy<WebElementEx> body;
		private final Lazy<WebElementEx> image;
		private final Lazy<WebElementEx> date;
		private final Lazy<WebElementEx> button;

		public Callout(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.heading = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-groupcallout-heading")));
			this.body = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-groupcallout-body")));
			this.image = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-groupcallout-image img")));
			this.date = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-groupcallout-date")));
			this.button = lazy(() -> findElement(By.cssSelector(".mntl-sc-block-groupcallout-button")));
		}

		public WebElementEx heading() {
			return heading.get();
		}

		public boolean hasHeading() {
			return getElement().elementExists(".mntl-sc-block-groupcallout-heading");
		}

		public WebElementEx body() {
			return body.get();
		}
		public boolean hasBody() {
			return getElement().elementExists(".mntl-sc-block-groupcallout-body");
		}

		public WebElementEx image() {
			return image.get();
		}

		public boolean hasImage() {
			return getElement().elementExists(".mntl-sc-block-groupcallout-image img");
		}

		public WebElementEx date() {
			return date.get();
		}

		public boolean hasDate() {
			return getElement().elementExists(".mntl-sc-block-groupcallout-date");
		}

		public WebElementEx button() {
			return button.get();
		}

		public boolean hasButton() {
			return getElement().elementExists(".mntl-sc-block-groupcallout-button");
		}

	}
}
