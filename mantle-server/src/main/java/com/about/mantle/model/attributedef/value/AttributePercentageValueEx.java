package com.about.mantle.model.attributedef.value;

public class AttributePercentageValueEx extends AbstractAttributeValueEx {

	private static final long serialVersionUID = 1L;

	private Float value;

	public AttributePercentageValueEx() {
		setType(AttributeValueTypeEx.PERCENTAGE.toString());
	}

	@Override
	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}
}
