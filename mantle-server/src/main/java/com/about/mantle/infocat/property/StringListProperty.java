package com.about.mantle.infocat.property;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StringListProperty extends Property<List<String>> implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
	private StringListPropertyValue value;
	
	@Override
	public StringListPropertyValue getValue() {
		return value;
	}
	
	public void setValue(StringListPropertyValue value) {
		this.value = value;
	}
	
	public class StringListPropertyValue extends PropertyValue<List<String>> {
		private List<String> stringValues;

		public List<String> getStringValues() {
			return stringValues;
		}

		public void setStringValues(List<String> stringValues) {
			this.stringValues = stringValues;
		}
		
		@Override
		@JsonIgnore
	    public List<String> getPrimaryValue() {
	    	return stringValues;
	    }
	}
}
