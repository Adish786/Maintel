package com.about.mantle.model.extended.docv2;

import java.io.Serializable;


public class AnalysisEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private SliceableListEx<Ingredient> ingredients = SliceableListEx.emptyList();

	public SliceableListEx<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(SliceableListEx<Ingredient> ingredients) {
		this.ingredients = SliceableListEx.emptyIfNull(ingredients);
	}

}
