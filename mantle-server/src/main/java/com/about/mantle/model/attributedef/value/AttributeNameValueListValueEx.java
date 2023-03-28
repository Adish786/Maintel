package com.about.mantle.model.attributedef.value;

import java.util.List;

public class AttributeNameValueListValueEx extends AbstractAttributeValueEx {

	private static final long serialVersionUID = 1L;

	private List<NameValuePair> value;
	
	public AttributeNameValueListValueEx() {
		setType(AttributeValueTypeEx.NAME_VALUE_LIST.toString());
	}
	
	@Override
	public List<NameValuePair> getValue() {
		return value;
	}
	
	public void setValue(List<NameValuePair> value) {
		this.value = value;
	}

	public static class NameValuePair {
		private String name;
		private String value;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
