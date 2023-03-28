package com.about.mantle.infocat.property;

import java.io.Serializable;

import org.apache.commons.lang3.tuple.Pair;

public class NumericalRangeProperty extends Property<Pair<Double,Double>> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private NumericalRangePropertyValue value;
	
	@Override
	public NumericalRangePropertyValue getValue() {
		return value;
	}
	
	public void setValue(NumericalRangePropertyValue value) {
		this.value = value;
	}
	
	public class NumericalRangePropertyValue extends PropertyValue<Pair<Double,Double>> {
		private Double numericalMinValue;
		private Double numericalMaxValue;
		
		public Double getNumericalMinValue() {
			return numericalMinValue;
		}
		
		public void setNumericalMinValue(Double numericalMinValue) {
			this.numericalMinValue = numericalMinValue;
		}
		
		public Double getNumericalMaxValue() {
			return numericalMaxValue;
		}
		
		public void setNumericalMaxValue(Double numericalMaxValue) {
			this.numericalMaxValue = numericalMaxValue;
		}
		
		@Override
	    public Pair<Double,Double> getPrimaryValue() {
	    	return Pair.of(numericalMinValue, numericalMaxValue);
	    }
		
		@Override
		public boolean isEmpty() {
			return getNumericalMinValue() == null && getNumericalMaxValue() == null;
		}
	}
	
}
