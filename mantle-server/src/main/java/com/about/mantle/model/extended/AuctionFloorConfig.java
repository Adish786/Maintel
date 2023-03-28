package com.about.mantle.model.extended;

import java.io.Serializable;
import java.util.Map;

public class AuctionFloorConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, AuctionFloorConfigMapping> mappings;

	public Map<String, AuctionFloorConfigMapping> getMappings() {
		return mappings;
	}

	public void setMappings(Map<String, AuctionFloorConfigMapping> mappings) {
		this.mappings = mappings;
	}

	public String parseViaMapping(String type, String value) {
		AuctionFloorConfigMapping map = mappings.get(type);

		if (map == null) {
			return value;
		}

		return map.parseValue(value);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("AuctionFloorConfig [");
		str.append(mappings.get("SLOT"));
		str.append("]");
		return str.toString();
	}
}
