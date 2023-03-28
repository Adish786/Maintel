package com.about.mantle.infocat.property;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StringProperty extends Property<String> implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
	private StringPropertyValue value;
	
	@Override
	public StringPropertyValue getValue() {
		return value;
	}
	
	public void setValue(StringPropertyValue value) {
		this.value = value;
	}
	
	public class StringPropertyValue extends PropertyValue<String> {
		private String stringValue;

		public String getStringValue() {
			return stringValue;
		}

		public void setStringValue(String stringValue) {
			this.stringValue = stringValue;
		}
		
		@Override
		@JsonIgnore
	    public String getPrimaryValue() {
	    	return stringValue;
	    }
	}
}
