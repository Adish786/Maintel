package com.about.mantle.utils.selene.document.api.recipe.nutrition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Ingredient {
	@Builder.Default
	private Number quantity = 19.0;

	@Builder.Default
	private String unit = "cup";

	@Builder.Default
	private String text = "flour";
}
