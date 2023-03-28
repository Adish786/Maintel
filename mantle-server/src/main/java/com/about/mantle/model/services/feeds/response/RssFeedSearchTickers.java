package com.about.mantle.model.services.feeds.response;

import java.io.Serializable;
import java.util.List;

/**
 * This class is used to deserialize JSON objects representing list of stock
 * tickers contained in individual search items consumed from Selene rss
 * endpoint
 */
public class RssFeedSearchTickers implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<RssFeedSearchStockData> list;

	public List<RssFeedSearchStockData> getList() {
		return list;
	}

	public void setList(List<RssFeedSearchStockData> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RssFeedSearchStockDataList {list=");
		builder.append(list);
		builder.append("}");
		return builder.toString();
	}

}
