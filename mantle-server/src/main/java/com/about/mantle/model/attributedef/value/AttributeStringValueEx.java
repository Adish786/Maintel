package com.about.mantle.model.attributedef.value;

public class AttributeStringValueEx extends AbstractAttributeValueEx {

	private static final long serialVersionUID = 1L;

	private String value;

	public AttributeStringValueEx() {
		setType(AttributeValueTypeEx.STRING.toString());
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

}
