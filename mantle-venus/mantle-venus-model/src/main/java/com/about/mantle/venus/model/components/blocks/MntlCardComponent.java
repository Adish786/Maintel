package com.about.mantle.venus.model.components.blocks;
import org.openqa.selenium.By;

import com.about.mantle.venus.model.MntlComponent;
import com.about.mantle.venus.model.MntlComponentId;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;
import com.about.venus.core.model.Component;
import com.about.venus.core.utils.Lazy;

@MntlComponentId("mntl-card")
public class MntlCardComponent extends MntlComponent {
	
	private final Lazy<CardMediaComponent> media;
	private final Lazy<CardContentComponent> content;
	private final Lazy<WebElementEx> label;
	private final Lazy<WebElementEx> cardFooter;
	private final Lazy<WebElementEx> cardLink;

	public MntlCardComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
		this.media = lazy(() -> findElement(By.className("card__media"), CardMediaComponent::new));
		this.content = lazy(() -> findElement(By.className("card__content"), CardContentComponent::new));
		this.label = lazy(() -> findElement(By.className("card__label")));
		this.cardFooter = lazy(() -> findElement(By.className("card__footer")));
		this.cardLink = lazy(() -> findElement(By.cssSelector("a")));
	}
	
	public CardMediaComponent media() {
		return media.get();
	}
	public CardContentComponent content() {
		return content.get();
	}
	public WebElementEx footer() {
		return cardFooter.get();
	}
	public WebElementEx label() {
		return label.get();
	}
	public WebElementEx cardLink() { return cardLink.get(); }
	
	public static class CardMediaComponent extends Component {

		private final Lazy<WebElementEx> img;
		private final Lazy<WebElementEx> imgTag;
		
		public CardMediaComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.img = lazy(() -> findElement(By.tagName("img")));
			this.imgTag = lazy(() -> findElement(By.className("card-img-tag")));
		}
		
		public WebElementEx img() {
			return img.get();
		}
		public WebElementEx imgTag() {
			return imgTag.get();
		}
	}
	
	public static class CardContentComponent extends Component {

		private final Lazy<WebElementEx> contentHeader;
		private final Lazy<WebElementEx> tag;
		private final Lazy<WebElementEx> title;
		private final Lazy<WebElementEx> excerpt;
		private final Lazy<WebElementEx> cardMeta;
		private final Lazy<WebElementEx> cardDescription;
		private final Lazy<WebElementEx> cardAuthorPrefix;
		private final Lazy<WebElementEx> cardAuthorName;

		public CardContentComponent(WebDriverExtended driver, WebElementEx element) {
			super(driver, element);
			this.contentHeader = lazy(() -> findElement(By.className("card__header")));
			this.tag = lazy(() -> findElement(By.className("card__tag")));
			this.title = lazy(() -> findElement(By.className("card__title")));
			this.excerpt = lazy(() -> findElement(By.className("card__excerpt")));
			this.cardMeta = lazy(() -> findElement(By.className("card__meta")));
			this.cardDescription = lazy(() -> findElement(By.cssSelector(".card__description")));
			this.cardAuthorPrefix = lazy(() -> findElement(By.cssSelector(".card__author-prefix")));
			this.cardAuthorName = lazy(() -> findElement(By.cssSelector(".card__author-name")));
		}
		
		public WebElementEx contentHeader() {
			return contentHeader.get();
		}
		public WebElementEx tag() {
			return tag.get();
		}
		public WebElementEx title() {
			return title.get();
		}
		public WebElementEx excerpt() {
			return excerpt.get();
		}
		public WebElementEx cardMeta() { return cardMeta.get(); }
		public WebElementEx cardDescription() { return cardDescription.get(); }
		public WebElementEx cardAuthorPrefix() { return cardAuthorPrefix.get(); }
		public WebElementEx cardAuthorName() { return cardAuthorName.get(); }
	}
}