package com.about.mantle.schema;

import java.util.List;
import java.util.Map;

public class RecipeSchemaTest extends ArticleSchemaTest {

	@Override
	protected List<String> getBasicStructure() {
		List<String> fieldsToVerify = super.getBasicStructure();
		fieldsToVerify.add("name");
		fieldsToVerify.add("cookTime");
		fieldsToVerify.add("prepTime");
		fieldsToVerify.add("recipeIngredient");
		fieldsToVerify.add("recipeInstructions");
		fieldsToVerify.add("recipeYield");
		fieldsToVerify.add("totalTime");
		fieldsToVerify.remove("articleSection");
		return fieldsToVerify;
	}

	@Override
	protected Map<String, Object> getAdditionalFieldsToVerify() {
		Map<String, Object> fieldsToVerify = super.getAdditionalFieldsToVerify();
		fieldsToVerify.put("@type", "Recipe");
		return fieldsToVerify;
	}

}