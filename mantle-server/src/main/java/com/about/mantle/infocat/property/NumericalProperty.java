package com.about.mantle.infocat.property;

import java.io.Serializable;

public class NumericalProperty extends Property<Double> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private NumericalPropertyValue value;
	
	@Override
	public NumericalPropertyValue getValue() {
		return value;
	}
	
	public void setValue(NumericalPropertyValue value) {
		this.value = value;
	}
	
	public class NumericalPropertyValue extends PropertyValue<Double> {
		private Double numberValue;
		
		public Double getNumberValue() {
			return numberValue;
		}
		public void setNumberValue(Double numberValue) {
			this.numberValue = numberValue;
		}
		
		@Override
		public Double getPrimaryValue() {
	    	return numberValue;
	    }
	}
}