package com.about.mantle.model;

import static org.apache.commons.lang3.StringUtils.stripToNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Due to PCR-337 `currency` is still a field on the selene object, but it is hidden from editors
 * and is always USD
 */
public class PriceInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Pattern DECIMAL_STYLE_PATTERN = Pattern.compile("[^\\d.]");
	private static final NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);

	private String price;
	private String currency;

	static {
		// PCR-337 decided that currency would always be USD
		formatter.setCurrency(Currency.getInstance("USD"));
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	private String getPriceStrippedToDecimals(String price) {
		return DECIMAL_STYLE_PATTERN.matcher(price).replaceAll("");
	}

	/**
	 * Formats the price for viewing on a document. See examples in {@link PriceInfoTest}
	 */
	@JsonIgnore
	public String getDisplayText() {
		if (stripToNull(price) == null) return null;
		String strippedPrice = getPriceStrippedToDecimals(price);
		try {
			return formatter.format(new BigDecimal(strippedPrice));
		} catch (NumberFormatException e) {
			return price;
		}
	}

	/**
	 * Formats the price for schema. See examples in {@link PriceInfoTest}
	 */
	@JsonIgnore
	public String getSimpleDisplayText() {
		if (stripToNull(price) == null) return null;
		String strippedPrice = getPriceStrippedToDecimals(price);
		try {
			return String.format("%.2f", new BigDecimal(strippedPrice));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("PriceInfo{");
		sb.append("price=").append(price);
		sb.append(", currency=").append(currency);
		sb.append('}');
		return sb.toString();
	}
}