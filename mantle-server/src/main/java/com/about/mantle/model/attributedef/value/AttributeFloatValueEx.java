package com.about.mantle.model.attributedef.value;

public class AttributeFloatValueEx extends AbstractAttributeValueEx {

	private static final long serialVersionUID = 1L;

	private Float value;

	public AttributeFloatValueEx() {
		setType(AttributeValueTypeEx.FLOAT.toString());
	}

	@Override
	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}
}
