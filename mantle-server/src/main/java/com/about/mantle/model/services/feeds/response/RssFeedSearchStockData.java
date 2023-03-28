package com.about.mantle.model.services.feeds.response;

import java.io.Serializable;

/**
 * This class represents a single ticker contained in individual search items
 * consumed from Selene rss endpoint
 */
public class RssFeedSearchStockData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String symbol;
	private String exchange;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RssFeedSearchStockData {symbol=");
		builder.append(symbol);
		builder.append(", exchange=");
		builder.append(exchange);
		builder.append("}");
		return builder.toString();
	}
}
