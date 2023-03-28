package com.about.mantle.infocat.property;

import java.io.Serializable;

import org.apache.commons.lang3.tuple.Pair;

public class CurrencyRangeProperty extends Property<Pair<Long,Long>> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private CurrencyRangePropertyValue value;
	
	@Override
	public CurrencyRangePropertyValue getValue() {
		return value;
	}
	
	public void setValue(CurrencyRangePropertyValue value) {
		this.value = value;
	}
	
	public class CurrencyRangePropertyValue extends PropertyValue<Pair<Long,Long>> {
		private Long currencyMinValue;

	    private Long currencyMaxValue;

	    private String currencyCode;

		public Long getCurrencyMinValue() {
			return currencyMinValue;
		}

		public void setCurrencyMinValue(Long currencyMinValue) {
			this.currencyMinValue = currencyMinValue;
		}

		public Long getCurrencyMaxValue() {
			return currencyMaxValue;
		}

		public void setCurrencyMaxValue(Long currencyMaxValue) {
			this.currencyMaxValue = currencyMaxValue;
		}

		public String getCurrencyCode() {
			return currencyCode;
		}

		public void setCurrencyCode(String currencyCode) {
			this.currencyCode = currencyCode;
		}
		
		@Override
	    public Pair<Long,Long> getPrimaryValue() {
	    	return Pair.of(currencyMinValue, currencyMaxValue);
	    }

		@Override
		public boolean isEmpty() {
			return getCurrencyMinValue() == null && getCurrencyMaxValue() == null;
		}
	}
		
}
