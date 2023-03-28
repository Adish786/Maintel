package com.about.mantle.model.attributedef.value;

import java.util.List;

public class AttributeStringListValueEx extends AbstractAttributeValueEx {
	private static final long serialVersionUID = 1L;

	private List<String> value;

	public AttributeStringListValueEx() {
		setType(AttributeValueTypeEx.STRING_LIST.toString());
	}

	@Override
	public List<String> getValue() {
		return value;
	}

	public void setValue(List<String> value) {
		this.value = value;
	}
}
