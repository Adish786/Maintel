package com.about.mantle.model.attributedef.value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeUnknownValueEx extends AbstractAttributeValueEx {

	private static final long serialVersionUID = 1L;
	
	private static final String UNKNOWN = "UNKNOWN";

	public AttributeUnknownValueEx() {
		setType(UNKNOWN);
	}
	
	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public String getType() {
		return UNKNOWN;
	}
	
}
