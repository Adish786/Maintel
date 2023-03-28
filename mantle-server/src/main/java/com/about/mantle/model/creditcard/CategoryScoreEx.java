package com.about.mantle.model.creditcard;

import java.io.Serializable;

public class CategoryScoreEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private String category;
	private String displayCategory;
	private Float score;

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

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}
}
