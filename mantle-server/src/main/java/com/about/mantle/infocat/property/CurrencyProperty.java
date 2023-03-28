package com.about.mantle.infocat.property;

import java.math.BigDecimal;
import java.util.Currency;
import java.text.NumberFormat;
import java.math.RoundingMode;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CurrencyProperty extends Property<String> implements Serializable {
	private static final long serialVersionUID = 1L;

	private CurrencyPropertyValue value;

	@Override
	public CurrencyPropertyValue getValue() {
		return value;
	}

	public void setValue(CurrencyPropertyValue value) {
		this.value = value;
	}

	public class CurrencyPropertyValue extends PropertyValue<String> {
		private String currencyCode;
		private String currencyValue;

		public String getCurrencyCode() {
			return currencyCode;
		}

		public void setCurrencyCode(String currencyCode) {
			this.currencyCode = currencyCode;
		}

		public String getCurrencyValue() {
			return currencyValue;
		}

		public void setCurrencyValue(String currencyValue) {
			this.currencyValue = currencyValue;
		}

		@Override
		public String getPrimaryValue() {
			return formatCurrency(currencyValue, currencyCode);
		}

		@JsonIgnore
		public String getFormattedCurrencyValue() {
			BigDecimal answer = formatCurrencyValue(currencyValue);
			if (answer != null) {
				return answer.toString();
			}
			return null;
		}

		private BigDecimal formatCurrencyValue(String currencyValue) {
			if (currencyValue == null)
				return null;

			return new BigDecimal(currencyValue).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
		}

		private String formatCurrency(String currencyValue, String currencyCode) {
			if (currencyValue == null)
				return null;

			NumberFormat formatter = NumberFormat.getCurrencyInstance();
			formatter.setCurrency(Currency.getInstance(currencyCode));

			return formatter.format(formatCurrencyValue(currencyValue));
		}
	}

}