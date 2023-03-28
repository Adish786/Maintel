package com.about.mantle.model.extended.docv2.sc.recipesc;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.SliceableListEx;

public class IngredientGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	private String heading;
	private SliceableListEx<Ingredient> ingredients;

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public SliceableListEx<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(SliceableListEx<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}
}