package com.about.mantle.infocat.property;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PropertyValue<T> {
	
	@SuppressWarnings("rawtypes")
	public static final PropertyValue EMPTY = new PropertyValue() {

	};
	
	@JsonIgnore
    public T getPrimaryValue() {
    	return null;
    }
	
	@JsonIgnore
    public static boolean isEmpty(PropertyValue<?> value) {
    	return value == null || value.isEmpty();
    }
	
	@JsonIgnore
	public boolean isEmpty() {
		return ObjectUtils.isEmpty(getPrimaryValue());
	}
	
}
