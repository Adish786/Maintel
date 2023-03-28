package com.about.mantle.model.commerce.amazonrss;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;

import com.about.mantle.model.RSS2.Item;
import com.about.mantle.model.extended.docv2.ImageEx;

/**
 * Used to render Amazon RSS feed template
 */
public class AmazonRssItem extends Item implements Serializable {

	private static final long serialVersionUID = 1L;

	private String htmlContent;
	private ImageEx heroImage;
	private String heroImageCaption;
	private String subheading;
	private String introText;
	private List<AmazonRssProduct> products;

	private AmazonRssItem(Builder builder) {
		super(builder.title, builder.link, null, builder.author, builder.pubDate, null);
		this.setHeroImage(builder.heroImage);
		this.setSubheading(builder.subheading);
		this.setIntroText(builder.introText);
		this.setProducts(builder.products);
		this.setHeroImageCaption(builder.heroImageCaption);
		this.setHtmlContent(builder.htmlContent);
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public ImageEx getHeroImage() {
		return heroImage;
	}

	public void setHeroImage(ImageEx heroImage) {
		this.heroImage = heroImage;
	}

	public String getHeroImageCaption() {
		return heroImageCaption;
	}

	public void setHeroImageCaption(String heroImageCaption) {
		this.heroImageCaption = heroImageCaption;
	}

	public String getSubheading() {
		return subheading;
	}

	public void setSubheading(String subheading) {
		this.subheading = subheading;
	}

	public String getIntroText() {
		return introText;
	}

	public void setIntroText(String introText) {
		this.introText = introText;
	}

	public List<AmazonRssProduct> getProducts() {
		return products;
	}

	public void setProducts(List<AmazonRssProduct> products) {
		this.products = products;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AmazonRssItem { ")
		  .append("title: ").append(getTitle()).append(", ")
		  .append("link: ").append(getLink()).append(", ")
		  .append("description: ").append(getDescription()).append(", ")
		  .append("content: ").append(getHtmlContent()).append(", ")
		  .append("author: ").append(getAuthor()).append(", ")
		  .append("pubDate: ").append(getPubDate().toString()).append(", ")
		  .append("products: ").append(getProducts().toString())
		  .append(" }");
		return sb.toString();
	}

	public static class Builder {
		//Fields from parent Item class
		private String title;
		private String link;
		private DateTime pubDate;
		private String author;

		//Fields of this class
		private String htmlContent;
		private ImageEx heroImage;
		private String heroImageCaption;
		private String subheading;
		private String introText;
		private List<AmazonRssProduct> products;

		public Builder() {
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder link(String link) {
			this.link = link;
			return this;
		}

		public Builder pubDate(DateTime pubDate) {
			this.pubDate = pubDate;
			return this;
		}

		public Builder author(String author) {
			this.author = author;
			return this;
		}
		
		public Builder htmlContent(String htmlContent) {
			this.htmlContent = htmlContent;
			return this;
		}

		public Builder heroImage(ImageEx heroImage) {
			this.heroImage = heroImage;
			return this;
		}
		
		public Builder heroImageCaption(String heroImageCaption) {
			this.heroImageCaption = heroImageCaption;
			return this;
		}

		public Builder subheading(String subheading) {
			this.subheading = subheading;
			return this;
		}

		public Builder introText(String introText) {
			this.introText = introText;
			return this;
		}

		public Builder products(List<AmazonRssProduct> products) {
			this.products = products;
			return this;
		}

		public AmazonRssItem build() {
			return new AmazonRssItem(this);
		}
	}

}