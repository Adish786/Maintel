package com.about.mantle.model.attributedef.value;

public class AttributeBooleanValueEx extends AbstractAttributeValueEx {

	private static final long serialVersionUID = 1L;

	private Boolean value;

	public AttributeBooleanValueEx() {
		setType(AttributeValueTypeEx.BOOLEAN.toString());
	}

	@Override
	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}
}
