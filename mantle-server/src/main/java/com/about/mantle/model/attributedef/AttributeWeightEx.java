package com.about.mantle.model.attributedef;

import java.io.Serializable;

public class AttributeWeightEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private String category;
	private String displayCategory;
	private Float weight;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDisplayCategory() {
		return displayCategory;
	}

	public void setDisplayCategory(String displayCategory) {
		this.displayCategory = displayCategory;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}
}
