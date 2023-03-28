package com.about.mantle.infocat.property;

public class BooleanProperty extends Property<Boolean> {

	private static final long serialVersionUID = 1L;
	
	private BooleanPropertyValue value;
	
	@Override
	public BooleanPropertyValue getValue() {
		return value;
	}
	
	public void setValue(BooleanPropertyValue value) {
		this.value = value;
	}
	
	public class BooleanPropertyValue extends PropertyValue<Boolean> {
		private Boolean booleanValue;

		public Boolean getBooleanValue() {
			return booleanValue;
		}

		public void setBooleanValue(Boolean booleanValue) {
			this.booleanValue = booleanValue;
		}
		
		@Override
	    public Boolean getPrimaryValue() {
	    	return booleanValue;
	    }
	}
}
