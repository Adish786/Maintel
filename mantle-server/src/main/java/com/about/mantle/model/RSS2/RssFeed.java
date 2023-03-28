package com.about.mantle.model.RSS2;

import java.io.Serializable;

import com.about.mantle.model.feeds.datamodel.Feed;

/**
 * Rss feed's root element
 */
public class RssFeed implements Feed, Serializable {

	private static final long serialVersionUID = 1L;

	private Channel channel;

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RssFeed {channel=");
		builder.append(channel);
		builder.append("}");
		return builder.toString();
	}
}