package com.about.mantle.venus.model.components.widgets;

import java.util.List;

import org.openqa.selenium.By;
import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.Validate;
import com.about.mantle.venus.model.components.buttons.ClickActions;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.model.Page;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("commerce-widget")
public class MntlCommerceWidgetComponent extends MntlComponent {

	private final Lazy<WebElementEx> commerceWidgetImage;
	private final Lazy<List<CommerceButtonList>> commerceButtonList;
	private final Lazy<CommerceWidgetHeading> commerceWidgetHeading;

	public MntlCommerceWidgetComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.commerceWidgetImage = lazy(() -> findElement(By.cssSelector(".mntl-commerce-widget__image")));
		this.commerceButtonList = lazy(
				() -> findElements(By.cssSelector(".mntl-commerce-btn-list"), CommerceButtonList::new));
		this.commerceWidgetHeading = lazy(
				() -> findElement(By.cssSelector(".commerce-widget__heading"), CommerceWidgetHeading::new));
	}

	public WebElementEx commerceWidgetImage() {
		return commerceWidgetImage.get();
	}

	public List<CommerceButtonList> commerceWidgetButtonList() {
		return commerceButtonList.get();
	}

	public CommerceWidgetHeading commerceWidgetHeading() {
		return commerceWidgetHeading.get();
	}
	
	@Validate()
	public WebElementEx button() {
		return commerceButtonList.get().get(0).commerceButtonListItems.get().get(0).commerceButtonText();
	}

	public class CommerceWidgetHeading extends Component {

		private final Lazy<WebElementEx> commerceHeaderLink;

		public CommerceWidgetHeading(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.commerceHeaderLink = lazy(() -> findElement(By.cssSelector(".list__header h3")));

		}

		public WebElementEx commerceHeaderLink() {
			return commerceHeaderLink.get();
		}

	}

	public static class CommerceButtonList extends Component {

		private final Lazy<List<CommerceButtonListItems>> commerceButtonListItems;

		public CommerceButtonList(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.commerceButtonListItems = lazy(() -> findElements(
					By.cssSelector(".mntl-commerce-btn-list__btn:not([href=''])"), CommerceButtonListItems::new));
		}

		public List<CommerceButtonListItems> commerceWidgetButtonListItems() {
			return commerceButtonListItems.get();
		}

	}

	public static class CommerceButtonListItems extends Component {

		private final Lazy<WebElementEx> commerceWidgetPriceTag;
		private final Lazy<WebElementEx> commerceButtonText;

		public CommerceButtonListItems(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.commerceWidgetPriceTag = lazy(() -> findElement(By.cssSelector(".commerce-widget__price-tag")));
			this.commerceButtonText = lazy(() -> findElement(By.className("link__wrapper")));

		}

		public WebElementEx commerceWidgetPriceTag() {
			return commerceWidgetPriceTag.get();
		}

		public WebElementEx commerceButtonText() {
			return commerceButtonText.get();
		}

	}

	public Amazon amazon() {
		return new Amazon(getDriver(), getElement());
	}

	public static class AmazonPage extends Page {

		public AmazonPage(WebDriverExtended driver) {
			super(driver);
		}
	}

	public static class Amazon extends ClickActions<AmazonPage> {

		public Amazon(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
		}

		@Override
		public AmazonPage createPage() {
			return new AmazonPage(getDriver());
		}
	}
	
	public Commerce commerce() {
		return new Commerce(getDriver(), getElement());
	}

	public static class CommercePage extends Page {

		public CommercePage(WebDriverExtended driver) {
			super(driver);
		}
	}

	public static class Commerce extends ClickActions<CommercePage> {

		public Commerce(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
		}

		@Override
		public CommercePage createPage() {
			return new CommercePage(getDriver());
		}
	}

}
