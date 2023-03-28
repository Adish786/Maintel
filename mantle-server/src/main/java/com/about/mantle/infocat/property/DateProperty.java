package com.about.mantle.infocat.property;

import java.io.Serializable;

public class DateProperty extends Property<Long> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private DatePropertyValue value;
	
	@Override
	public DatePropertyValue getValue() {
		return value;
	}
	
	public void setValue(DatePropertyValue value) {
		this.value = value;
	}
	
	public class DatePropertyValue extends PropertyValue<Long> {
		private Long dateValue;

		public Long getDateValue() {
			return dateValue;
		}

		public void setDateValue(Long dateValue) {
			this.dateValue = dateValue;
		}
		
		@Override
		public Long getPrimaryValue() {
	    	return dateValue;
	    }
	}
	
}
