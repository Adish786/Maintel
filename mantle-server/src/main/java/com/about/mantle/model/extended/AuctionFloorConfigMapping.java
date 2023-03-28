package com.about.mantle.model.extended;

import java.io.Serializable;
import java.util.Map;

public class AuctionFloorConfigMapping implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String defaultValue;
	private Map<String, String> valueMapping;

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Map<String, String> getValueMapping() {
		return valueMapping;
	}

	public void setValueMapping(Map<String, String> valueMapping) {
		this.valueMapping = valueMapping;
	}

	public String parseValue(String value) {
		if (value == null) {
			return defaultValue;
		}

		return valueMapping.getOrDefault(value.toLowerCase(), defaultValue);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("AuctionFloorConfigMapping [");
		str.append("defaultValue=").append(defaultValue).append(", ");
		str.append("valueMapping=").append(valueMapping).append("]");
		return str.toString();
	}
}
