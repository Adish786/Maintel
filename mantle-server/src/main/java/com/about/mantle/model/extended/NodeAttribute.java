package com.about.mantle.model.extended;

/**
 * An enum that represents the keys used for {@link TaxeneNodeEx} attributes
 */
public enum NodeAttribute {

	SHORT_HEADING("shortHeading");

	private String key;

	private NodeAttribute(String key) {
		this.key = key;
	}

	public String key() {
		return this.key;
	}
}
