package com.about.mantle.model.attributedef.value;

public class AttributeCurrencyValueEx extends AbstractAttributeValueEx {

	private static final long serialVersionUID = 1L;

	private CurrencyValue value;

	public AttributeCurrencyValueEx() {
		setType(AttributeValueTypeEx.CURRENCY.toString());
	}

	@Override
	public CurrencyValue getValue() {
		return value;
	}

	public void setValue(CurrencyValue value) {
		this.value = value;
	}

	public static class CurrencyValue {
		private String code; // ISO 4217 currency code
		private Float value;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public Float getValue() {
			return value;
		}

		public void setValue(Float value) {
			this.value = value;
		}
	}
}
