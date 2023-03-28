package com.about.mantle.utils.selene.document.api.recipe;

import com.about.mantle.utils.selene.api.common.SliceableList;
import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Grouping {
	@Builder.Default
	private String primary = "Chicken";
	@Builder.Default
	private String secondary = "Butter";
	@Builder.Default
	private Boolean easy = true;
	@Builder.Default
	private Boolean quick = true;
	@Builder.Default
	private Boolean healthy = true;
	@Builder.Default
	private SliceableList<String> preparation = SliceableList.<String>builder()
			.list(ImmutableList.of("Assemble", "Simmer"))
			.totalSize(2)
			.build();
	@Builder.Default
	private SliceableList<String> cuisine = SliceableList.<String>builder()
			.list(ImmutableList.of("Mexican", "Tex-Mex"))
			.totalSize(2)
			.build();
	@Builder.Default
	private SliceableList<String> course = SliceableList.<String>builder()
			.list(ImmutableList.of("Beverage", "Breakfast", "Brunch"))
			.totalSize(3)
			.build();
	@Builder.Default
	private SliceableList<String> occasion = SliceableList.<String>builder()
			.list(ImmutableList.of("Back to School", "Fall", "Party"))
			.totalSize(3)
			.build();
	@Builder.Default
	private SliceableList<String> special = SliceableList.<String>builder()
			.list(ImmutableList.of("Easy", "For Kids"))
			.totalSize(2)
			.build();
	@Builder.Default
	private SliceableList<String> dish = SliceableList.<String>builder()
			.list(ImmutableList.of("breakfast", "lunch", "dinner"))
			.totalSize(3)
			.build();
	@Builder.Default
	private SliceableList<String> waysToServe = SliceableList.<String>builder()
			.list(ImmutableList.of("take out", "stay", "etc"))
			.totalSize(3)
			.build();
	@Builder.Default
	private SliceableList<String> glassTypes = SliceableList.<String>builder()
			.list(ImmutableList.of("type1", "type2", "type3"))
			.totalSize(3)
			.build();
	@Builder.Default
	private SliceableList<String> flavorProfiles = SliceableList.<String>builder()
			.list(ImmutableList.of("profile1", "profile2", "profile3"))
			.totalSize(3)
			.build();
	@Builder.Default
	private SliceableList<String> strengths = SliceableList.<String>builder()
			.list(ImmutableList.of("strengths1", "strengths2", "strengths3"))
			.totalSize(3)
			.build();
	@Builder.Default
	private SliceableList<String> complexities = SliceableList.<String>builder()
			.list(ImmutableList.of("complexitie1", "complexitie2", "complexitie3"))
			.totalSize(3)
			.build();
	@Builder.Default
	private SliceableList<String> hours = SliceableList.<String>builder()
			.list(ImmutableList.of("hours1", "hours2", "hours3"))
			.totalSize(3)
			.build();
	@Builder.Default
	private SliceableList<String> themes = SliceableList.<String>builder()
			.list(ImmutableList.of("theme1", "theme2", "theme3"))
			.totalSize(3)
			.build();
	@Builder.Default
	private SliceableList<String> bases = SliceableList.<String>builder()
			.list(ImmutableList.of("base1", "base2", "base3"))
			.totalSize(3)
			.build();
	@Builder.Default
	private SliceableList<String> recipeTypes = SliceableList.<String>builder()
			.list(ImmutableList.of("recipeType1", "recipeType2", "recipeType3"))
			.totalSize(3)
			.build();
}
