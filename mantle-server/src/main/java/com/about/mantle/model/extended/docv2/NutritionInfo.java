package com.about.mantle.model.extended.docv2;

public class NutritionInfo {

	private Long docId;
	private SliceableListEx<Ingredient> ingredients;
	private NutritionFacts nutritionFacts;

	/**
	 * Optional field indicating whether or not the confidence level of the computed nutrition data is high enough to
	 * display on the site. This flag should default to <code>true</code> as there are only a few instances where
	 * confidences is low. <br>
	 * Note : The overall number instances where <code>display = false</code> will converge to zero over time which will
	 * effectively remove this flag from the service
	 */
	private Boolean display;

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public SliceableListEx<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(SliceableListEx<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public NutritionFacts getNutritionFacts() {
		return nutritionFacts;
	}

	public void setNutritionFacts(NutritionFacts nutritionFacts) {
		this.nutritionFacts = nutritionFacts;
	}

	public Boolean getDisplay() {
		return display;
	}

	public void setDisplay(Boolean display) {
		this.display = display;
	}
}