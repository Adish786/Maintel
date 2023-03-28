package com.about.mantle.model.extended.docv2.sc.recipesc;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.SliceableListEx;

public class NutritionAnalysis implements Serializable {
	private static final long serialVersionUID = 1L;

	private Boolean complete;
	private SliceableListEx<Nutrient> nutrients;

	public Boolean getComplete() {
		return complete;
	}

	public void setComplete(Boolean complete) {
		this.complete = complete;
	}

	public SliceableListEx<Nutrient> getNutrients() {
		return nutrients;
	}

	public void setNutrients(SliceableListEx<Nutrient> nutrients) {
		this.nutrients = nutrients;
	}
}
