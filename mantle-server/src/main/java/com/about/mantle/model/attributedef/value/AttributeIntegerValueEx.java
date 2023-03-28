package com.about.mantle.model.attributedef.value;

public class AttributeIntegerValueEx extends AbstractAttributeValueEx {

	private static final long serialVersionUID = 1L;

	private Integer value;

	public AttributeIntegerValueEx() {
		setType(AttributeValueTypeEx.INTEGER.toString());
	}

	@Override
	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
