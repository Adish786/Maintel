package com.about.mantle.utils.selene.document.api.recipe;

import com.about.mantle.utils.selene.api.common.Audit;
import com.about.mantle.utils.selene.api.common.SliceableList;
import com.about.mantle.utils.selene.document.api.ContentBlock;
import com.about.mantle.utils.selene.document.api.Document;
import com.about.mantle.utils.selene.document.api.Page;
import com.about.mantle.utils.selene.document.api.recipe.nutrition.Ingredient;
import com.about.mantle.utils.selene.document.api.recipe.nutrition.NutritionFacts;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class RecipeSCDocument extends Document {
	@Builder.Default
	private String key = "3a51a5e5f4000086be0035f3";

	@Builder.Default
	private String authorKey = "41033";

	@Builder.Default
	private String templateType = "RECIPESC";

	@Builder.Default
	private String lastEditingAuthorId = "41033";

	@Builder.Default
	private String lastEditingUserId = "157531902117689";

	@Builder.Default
	private String title = "auto-title";

	@Builder.Default
	private String socialTitle = "auto-social-title";

	@Builder.Default
	private String heading = "auto-heading";

	@Builder.Default
	private String description = "auto-description";

	@Builder.Default
	private String metaDescription = "auto-meta-description";

	private String url;
	private String rootUrl;
	private SliceableList<String> labels;

	private SliceableList<String> ingredient;

	@Builder.Default
	private String yield = "test-yield";

	@Builder.Default
	private String prepTime = "1200000";

	@Builder.Default
	private String cookTime = "900000";
	private Audit audit;
	private Grouping grouping;
	private SliceableList<String> keywords;

	private SliceableList<ContentBlock> instruction;

	private SliceableList<Ingredient> ingredients;

	private NutritionFacts nutritionFacts;

	private SliceableList<Page> pages;

}
