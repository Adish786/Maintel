package com.about.mantle.model.feeds.datamodel.rss;

import java.io.Serializable;
import java.util.List;

import com.about.mantle.model.RSS2.Item;
import com.about.mantle.model.feeds.datamodel.FeedItem;

/**
 * Additional fields for the Item element of channel element
 */
public class RssFeedItem extends Item implements FeedItem, Serializable {

	private static final long serialVersionUID = 1L;

	private String guid;
	private String creator;
	private List<String> categories;
	private String content;

	public RssFeedItem() {}

	public RssFeedItem(RssFeedItem item) {
		super(item);
		this.guid = item.getGuid();
		this.creator = item.getCreator();
		this.categories = item.getCategories();
		this.content = item.getContent();
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RssFeedItem {title=");
		builder.append(getTitle());
		builder.append(", link=");
		builder.append(getLink());
		builder.append(", description=");
		builder.append(getDescription());
		builder.append(", enclosure=");
		builder.append(getEnclosure());
		builder.append(", pubDate=");
		builder.append(getPubDate());
		builder.append(", guid=");
		builder.append(guid);
		builder.append(", creator=");
		builder.append(creator);
		builder.append(", categories=");
		builder.append(categories);
		builder.append(", content=");
		builder.append(content);
		builder.append("}");
		return builder.toString();
	}
}
