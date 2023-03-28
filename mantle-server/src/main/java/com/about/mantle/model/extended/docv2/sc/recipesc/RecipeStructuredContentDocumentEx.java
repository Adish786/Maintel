package com.about.mantle.model.extended.docv2.sc.recipesc;

import com.about.mantle.model.extended.docv2.NutritionInfo;
import com.about.mantle.model.extended.docv2.QuantityWithUnit;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.TimeRangeEx;
import com.about.mantle.model.extended.docv2.TypedTimeRange;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.MaterialGroup;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Recipe SC document. {@link #intro}, {@link #instruction}, and {@link #fromTheEditors} are list of SC
 * blocks.
 */

public class RecipeStructuredContentDocumentEx extends StructuredContentBaseDocumentEx {

	private static final long serialVersionUID = 1L;

	private SliceableListEx<String> ingredient;
	private SliceableListEx<AbstractStructuredContentContentEx<?>> intro = SliceableListEx.emptyList();
	private SliceableListEx<AbstractStructuredContentContentEx<?>> instruction = SliceableListEx.emptyList();
	private SliceableListEx<AbstractStructuredContentContentEx<?>> fromTheEditors = SliceableListEx.emptyList();

	private String yield;
	/**
	 * @deprecated Use {@link #prepTimeRange} instead
	 */
	@Deprecated
	private Long prepTime;
	private TimeRangeEx prepTimeRange;
	/**
	 * @deprecated Use {@link #cookTimeRange} instead
	 */
	@Deprecated
	private Long cookTime;
	private TimeRangeEx cookTimeRange;
	/**
	 * @deprecated Use {@link #customTimeRange} instead
	 */
	@Deprecated
	private CustomTime customTime;
	private CustomTimeRange customTimeRange;
	private SliceableListEx<TypedTimeRange> typedTimeRanges;
	private Grouping grouping;
	private NutritionInfo nutritionInfo;
	private String mealType;
	private String preparationMethod;
	private SliceableListEx<String> keywords;
	private SliceableListEx<IngredientGroup> ingredientGroups;
	private SliceableListEx<MaterialGroup> equipmentGroups;
	private NutritionAnalysis nutritionAnalysis;
	private QuantityWithUnit recipeServing;
	private QuantityWithUnit recipeYield;
	private String note;
	private String legacyId;

	/**
	 * Returns combined stream of {@link #intro}, {@link #instruction}, and {@link #fromTheEditors} in that
	 * order.
	 */
	@JsonIgnore
	@Override
	public Stream<AbstractStructuredContentContentEx<?>> getContentsStream() {

		return Stream.of(ensureSCStream.apply(intro),
		                 ensureSCStream.apply(instruction),
		                 ensureSCStream.apply(fromTheEditors))
		             .flatMap(Function.identity())
		             .filter(onNonNullSCData);
	}

	/**
	 * Returns combined stream of {@link #intro}
	 */
	@JsonIgnore
	public SliceableListEx<AbstractStructuredContentContentEx<?>> getIntroContents() {
		return intro;
	}

	/**
	 * Returns combined stream of {@link #instruction}
	 */
	@JsonIgnore
	public SliceableListEx<AbstractStructuredContentContentEx<?>> getInstructionContents() {
		return instruction;
	}

	public SliceableListEx<String> getIngredient() {
		return ingredient;
	}

	public void setIngredient(SliceableListEx<String> ingredient) {
		this.ingredient = ingredient;
	}

	public SliceableListEx<AbstractStructuredContentContentEx<?>> getIntro() {
		return intro;
	}

	public void setIntro(SliceableListEx<AbstractStructuredContentContentEx<?>> intro) {
		this.intro = intro;
	}

	public SliceableListEx<AbstractStructuredContentContentEx<?>> getInstruction() {
		return instruction;
	}

	public void setInstruction(SliceableListEx<AbstractStructuredContentContentEx<?>> instruction) {
		this.instruction = instruction;
	}

	public SliceableListEx<AbstractStructuredContentContentEx<?>> getFromTheEditors() {
		return fromTheEditors;
	}

	public void setFromTheEditors(SliceableListEx<AbstractStructuredContentContentEx<?>> fromTheEditors) {
		this.fromTheEditors = fromTheEditors;
	}

	public String getYield() {
		return yield;
	}

	public void setYield(String yield) {
		this.yield = yield;
	}

	public QuantityWithUnit getRecipeServing() {
		return recipeServing;
	}

	public void setRecipeServing(QuantityWithUnit recipeServing) {
		this.recipeServing = recipeServing;
	}

	public Long getPrepTime() {
		return prepTime;
	}

	public void setPrepTime(Long prepTime) {
		this.prepTime = prepTime;
	}

	public Long getCookTime() {
		return cookTime;
	}

	public void setCookTime(Long cookTime) {
		this.cookTime = cookTime;
	}

	public CustomTime getCustomTime() {
		return customTime;
	}

	public void setCustomTime(CustomTime customTime) {
		this.customTime = customTime;
	}

	public TimeRangeEx getPrepTimeRange() {
		return prepTimeRange;
	}

	public void setPrepTimeRange(TimeRangeEx prepTimeRange) {
		this.prepTimeRange = prepTimeRange;
	}

	public TimeRangeEx getCookTimeRange() {
		return cookTimeRange;
	}

	public void setCookTimeRange(TimeRangeEx cookTimeRange) {
		this.cookTimeRange = cookTimeRange;
	}

	public CustomTimeRange getCustomTimeRange() {
		return customTimeRange;
	}

	public void setCustomTimeRange(CustomTimeRange customTimeRange) {
		this.customTimeRange = customTimeRange;
	}

	public SliceableListEx<TypedTimeRange> getTypedTimeRanges() {
		return typedTimeRanges;
	}

	public void setTypedTimeRanges(SliceableListEx<TypedTimeRange> typedTimeRanges) {
		this.typedTimeRanges = typedTimeRanges;
	}

	public Grouping getGrouping() {
		return grouping;
	}

	public void setGrouping(Grouping grouping) {
		this.grouping = grouping;
	}

	public NutritionInfo getNutritionInfo() {
		return nutritionInfo;
	}

	public void setNutritionInfo(NutritionInfo nutritionInfo) {
		this.nutritionInfo = nutritionInfo;
	}

	public String getMealType() {
		return mealType;
	}

	public void setMealType(String mealType) {
		this.mealType = mealType;
	}

	public String getPreparationMethod() {
		return preparationMethod;
	}

	public void setPreparationMethod(String preparationMethod) {
		this.preparationMethod = preparationMethod;
	}

	@Override
	public String toString() {
		return "RecipeStructuredContentDocumentEx{parent=" + super.toString() + "}";
	}

	@Override
	public SliceableListEx<String> getKeywords() {
		return keywords;
	}

	@Override
	public void setKeywords(SliceableListEx<String> keywords) {
		this.keywords = keywords;
	}

	public SliceableListEx<IngredientGroup> getIngredientGroups() {
		return ingredientGroups;
	}


	public void setIngredientGroups(SliceableListEx<IngredientGroup> ingredientGroups) {
		this.ingredientGroups = ingredientGroups;
	}

	public SliceableListEx<MaterialGroup> getEquipmentGroups() {
		return equipmentGroups;
	}

	public void setEquipmentGroups(SliceableListEx<MaterialGroup> equipmentGroups) {
		this.equipmentGroups = equipmentGroups;
	}

	public NutritionAnalysis getNutritionAnalysis() {
		return nutritionAnalysis;
	}

	public void setNutritionAnalysis(NutritionAnalysis nutritionAnalysis) {
		this.nutritionAnalysis = nutritionAnalysis;
	}

	public QuantityWithUnit getRecipeYield() {
		return recipeYield;
	}

	public void setRecipeYield(QuantityWithUnit recipeYield) {
		this.recipeYield = recipeYield;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getLegacyId() {
		return legacyId;
	}

	public void setLegacyId(String legacyId) {
		this.legacyId = legacyId;
	}

	/**
	 * @deprecated Use {@link #customTimeRange} instead
	 */
	@Deprecated
	public static class CustomTime implements Serializable {

		private static final long serialVersionUID = 1L;

		private Long time;
		private String label;

		public CustomTime() {
		}

		public void setTime(Long time) {
			this.time = time;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Long getTime() {
			return time;
		}

		public String getLabel() {
			return label;
		}
	}

	public static class CustomTimeRange implements Serializable {
		private static final long serialVersionUID = 1L;

		private TimeRangeEx time;
		private String label;

		public CustomTimeRange () {
		}

		public TimeRangeEx getTime() {
			return time;
		}

		public void setTime(TimeRangeEx time) {
			this.time = time;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}
	}

	public static class Grouping implements Serializable {
		private static final long serialVersionUID = 1L;

		private String primary;
		private String secondary;
		private SliceableListEx<String> preparation;
		private SliceableListEx<String> cuisine;
		private SliceableListEx<String> course;
		private SliceableListEx<String> occasion;
		private SliceableListEx<String> special;
		private SliceableListEx<String> dish;
		private SliceableListEx<String> diet;

		private Boolean easy;
		private Boolean quick;
		private Boolean healthy;


		public Grouping() {
		}

		public void setPrimary(String primary) {
			this.primary = primary;
		}

		public void setSecondary(String secondary) {
			this.secondary = secondary;
		}

		public void setPreparation(SliceableListEx<String> preparation) {
			this.preparation = preparation;
		}

		public void setCuisine(SliceableListEx<String> cuisine) {
			this.cuisine = cuisine;
		}

		public void setCourse(SliceableListEx<String> course) {
			this.course = course;
		}

		public void setOccasion(SliceableListEx<String> occasion) {
			this.occasion = occasion;
		}

		public void setSpecial(SliceableListEx<String> special) {
			this.special = special;
		}

		public void setDish(SliceableListEx<String> dish) {
			this.dish = dish;
		}

		public void setDiet(SliceableListEx<String> diet) {
			this.diet = diet;
		}

		public void setEasy(Boolean easy) {
			this.easy = easy;
		}

		public void setQuick(Boolean quick) {
			this.quick = quick;
		}

		public void setHealthy(Boolean healthy) {
			this.healthy = healthy;
		}

		public String getPrimary() {
			return primary;
		}

		public String getSecondary() {
			return secondary;
		}

		public SliceableListEx<String> getPreparation() {
			return preparation;
		}

		public SliceableListEx<String> getCuisine() {
			return cuisine;
		}

		public SliceableListEx<String> getCourse() {
			return course;
		}

		public SliceableListEx<String> getOccasion() {
			return occasion;
		}

		public SliceableListEx<String> getSpecial() {
			return special;
		}

		public SliceableListEx<String> getDish() {
			return dish;
		}

		public SliceableListEx<String> getDiet() {
			return diet;
		}

		public Boolean getEasy() {
			return easy;
		}

		public Boolean getQuick() {
			return quick;
		}

		public Boolean getHealthy() {
			return healthy;
		}
	}
}
