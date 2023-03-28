package com.about.mantle.model.attributedef.value;

public class AttributeCurrencyRangeValueEx extends AbstractAttributeValueEx {

	private static final long serialVersionUID = 1L;

	private CurrencyRangeValue value;

	public AttributeCurrencyRangeValueEx() {
		setType(AttributeValueTypeEx.CURRENCY_RANGE.toString());
	}

	@Override
	public CurrencyRangeValue getValue() {
		return value;
	}

	public void setValue(CurrencyRangeValue value) {
		this.value = value;
	}

	public static class CurrencyRangeValue {
		private String code; // ISO 4217 currency code
		private Float min;
		private Float max;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public Float getMin() {
			return min;
		}

		public void setMin(Float min) {
			this.min = min;
		}

		public Float getMax() {
			return max;
		}

		public void setMax(Float max) {
			this.max = max;
		}
	}
}
