package com.about.mantle.model.feeds.datamodel.rss;

import com.about.mantle.model.RSS2.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Additional fields for a feed that would like to have multiple enclosures in the feed.
 */
public class MultiEnclosureRssFeedItem extends RssFeedItem {

	private static final long serialVersionUID = 1L;

	private List<Enclosure> enclosures;

	public MultiEnclosureRssFeedItem(RssFeedItem item) {
		super(item);
	}

	public List<Enclosure> getEnclosures() {
		return enclosures;
	}

	public void setEnclosures(List<Enclosure> enclosures) {
		this.enclosures = enclosures;
	}

	@Override
	/**
	 * We are overriding this method because we do not actually care about the single enclosure
	 * in {@link com.about.mantle.model.RSS2.Item}
	 * This way, the isValid methods in BaseXmlFeedBuilder will check to make sure our
	 * enclosures list has at least one item
	 */
	public Enclosure getEnclosure() {
		if (enclosures != null && !enclosures.isEmpty()) {
			return enclosures.get(0);
		}
		return null;
	}

	@Override
	public void setEnclosure(Item.Enclosure enclosure) {
		if (enclosure == null) return;
		if (enclosures == null) enclosures = new ArrayList<>();
		enclosures.add(0, enclosure);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MultiEnclosureRssFeedItem {title=");
		builder.append(getTitle());
		builder.append(", link=");
		builder.append(getLink());
		builder.append(", description=");
		builder.append(getDescription());
		builder.append(", enclosures=");
		builder.append(enclosures);
		builder.append("}");
		return builder.toString();
	}
}
