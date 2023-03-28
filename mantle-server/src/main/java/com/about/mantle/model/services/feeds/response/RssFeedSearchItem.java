package com.about.mantle.model.services.feeds.response;

import java.io.Serializable;
import java.util.Comparator;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.ImageEx;

/**
 * This class is used to deserialize individual search items in JSON consumed
 * from Selene rss endpoint
 */
public class RssFeedSearchItem implements Comparable<RssFeedSearchItem>, Serializable {

	private static final long serialVersionUID = 1L;

	private String title;
	private String url;
	private String description;
	private ImageEx image;
	private Long lastPublished;
	private String authorName;
	private RssFeedSearchTickers tickers;
	private BaseDocumentEx document;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ImageEx getImage() {
		return image;
	}

	public void setImage(ImageEx image) {
		this.image = image;
	}

	public Long getLastPublished() {
		return lastPublished;
	}

	public void setLastPublished(Long lastPublished) {
		this.lastPublished = lastPublished;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public RssFeedSearchTickers getTickers() {
		return tickers;
	}

	public void setTickers(RssFeedSearchTickers tickers) {
		this.tickers = tickers;
	}

	public BaseDocumentEx getDocument() {
		return document;
	}

	public void setDocument(BaseDocumentEx document) {
		this.document = document;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RssFeedSearchItem {title=");
		builder.append(title);
		builder.append(", url=");
		builder.append(url);
		builder.append(", description=");
		builder.append(description);
		builder.append(", image=");
		builder.append(image);
		builder.append(", lastPublished=");
		builder.append(lastPublished);
		builder.append(", authorName=");
		builder.append(authorName);
		builder.append(", tickers=");
		builder.append(tickers);
		builder.append(", document=");
		builder.append(document);
		builder.append("}");
		return builder.toString();
	}

	/**
	 * Sort descending by last published
	 * 
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(RssFeedSearchItem o) {
		return Comparator.comparing(RssFeedSearchItem::getLastPublished).reversed()
				.thenComparing(RssFeedSearchItem::getTitle).compare(this, o);
	}

	/**
	 * Note: Do not replace this method implementation with an auto-generated one
	 * from the IDE. Equality check for this object comprise of title and url only.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	/**
	 * Note: Do not replace this method implementation with an auto-generated one
	 * from the IDE. Equality check for this object comprise of title and url only.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		RssFeedSearchItem other = (RssFeedSearchItem) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

}
