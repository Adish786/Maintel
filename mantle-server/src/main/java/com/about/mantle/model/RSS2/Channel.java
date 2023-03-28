package com.about.mantle.model.RSS2;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

/**
 * Channel element of rss feed
 */
public class Channel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title;
	private String link;
	private String description;
	private DateTime lastBuildDate;
	private String language;
	private List<Item> items;

	public Channel(String title, String link, String description, DateTime lastBuildDate, List<Item> items) {
		this.title = title;
		this.link = link;
		this.description = description;
		this.lastBuildDate = lastBuildDate;
		this.items = items;
	}

	public Channel() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DateTime getLastBuildDate() {
		return lastBuildDate;
	}

	public void setLastBuildDate(DateTime lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Channel {title=");
		builder.append(title);
		builder.append(", link=");
		builder.append(link);
		builder.append(", description=");
		builder.append(description);
		builder.append(", lastBuildDate=");
		builder.append(lastBuildDate);
		builder.append(", language=");
		builder.append(language);
		builder.append(", items=");
		builder.append(items);
		builder.append("}");
		return builder.toString();
	}
}
