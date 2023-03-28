package com.about.mantle.utils.selene.document.api.recipe.nutrition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class NutritionFacts {
	private Item calories;
	private Item protein;
	private Item carbohydrate;
	private Item dietaryFiber;
	private Item totalSugars;
	private Item addedSugars;
	private Item totalFat;
	private Item saturatedFat;
	private Item cholesterol;
	private Item calcium;
	private Item potassium;
	private Item sodium;
	private Item iron;
	private Item vitaminD;

	@Getter @Builder
	public static class Item {
		private Integer quantity;
		private String unit;
	}
}
