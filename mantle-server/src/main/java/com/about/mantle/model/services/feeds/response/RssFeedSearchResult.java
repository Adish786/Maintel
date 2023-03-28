package com.about.mantle.model.services.feeds.response;

import java.io.Serializable;
import java.util.List;

/**
 * Outermost class used to deserialize JSON consumed from Selene rss endpoint
 */
public class RssFeedSearchResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<RssFeedSearchItem> list;
	private Integer totalSize;

	public List<RssFeedSearchItem> getList() {
		return list;
	}

	public void setList(List<RssFeedSearchItem> list) {
		this.list = list;
	}

	public Integer getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RssFeedSearchResult {list=");
		builder.append(list);
		builder.append(", totalSize=");
		builder.append(totalSize);
		builder.append("}");
		return builder.toString();
	}

}
