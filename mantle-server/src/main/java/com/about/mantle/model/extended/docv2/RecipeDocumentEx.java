package com.about.mantle.model.extended.docv2;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.about.globe.core.exception.GlobeInvalidTaskParameterException;
import com.about.mantle.htmlslicing.HtmlSlice;
import com.about.mantle.model.extended.docv2.TaggedImage.UsageFlag;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class RecipeDocumentEx extends BaseDocumentEx implements Linkboxed {

	private static final long serialVersionUID = 1L;
	private String ingredient;
	private SliceableListEx<HtmlSlice> instruction = SliceableListEx.emptyList();
	private String yield;
	private String prepTime;
	private String cookTime;
	private CustomTime customTime;
	private Grouping grouping;
	private SliceableListEx<LinkboxEx> linkboxes = SliceableListEx.emptyList();
	private SliceableListEx<String> labels = SliceableListEx.emptyList();
	private AnalysisEx analysis;
	private List<LinkboxLinkEx> linkboxList = Collections.emptyList();
	private Map<String, NutritionFact> nutritionFacts = Collections.emptyMap();
	private Boolean displayNutrition; 

	@Override
	@JsonIgnore
	public int getRemainingLength(int currentBlock, int currentPage, boolean currentPageOnly)
			throws GlobeInvalidTaskParameterException {

		return getRemainingLength(getInstruction().getList(), currentBlock);
	}

	@Override
	@JsonIgnore
	public int calculateImageCount() {
		return isNotBlank(getImageForUsage(UsageFlag.PRIMARY).getUrl()) ? 1 : 0;
	}

	public String getIngredient() {
		return ingredient;
	}

	public void setIngredient(String ingredient) {
		this.ingredient = ingredient;
	}

	public SliceableListEx<HtmlSlice> getInstruction() {
		return instruction;
	}

	public void setInstruction(SliceableListEx<HtmlSlice> instruction) {
		this.instruction = SliceableListEx.emptyIfNull(instruction);
	}

	public String getYield() {
		return yield;
	}

	public void setYield(String yield) {
		this.yield = yield;
	}

	public String getPrepTime() {
		return prepTime;
	}

	public void setPrepTime(String prepTime) {
		this.prepTime = prepTime;
	}

	public String getCookTime() {
		return cookTime;
	}

	public void setCookTime(String cookTime) {
		this.cookTime = cookTime;
	}

	public CustomTime getCustomTime() {
		return customTime;
	}

	public void setCustomTime(CustomTime customTime) {
		this.customTime = customTime;
	}

	public Grouping getGrouping() {
		return grouping;
	}

	public void setGrouping(Grouping grouping) {
		this.grouping = grouping;
	}

	@Override
	public SliceableListEx<LinkboxEx> getLinkboxes() {
		return linkboxes;
	}

	public void setLinkboxes(SliceableListEx<LinkboxEx> linkboxes) {
		this.linkboxes = SliceableListEx.emptyIfNull(linkboxes);
		this.linkboxList = transformLinkboxes();
	}

	@Override
	@JsonIgnore
	public List<LinkboxLinkEx> getLinkboxesAsList() {
		return linkboxList;
	}

	public SliceableListEx<String> getLabels() {
		return labels;
	}

	public void setLabels(SliceableListEx<String> labels) {
		this.labels = SliceableListEx.emptyIfNull(labels);
	}

	public AnalysisEx getAnalysis() {
		return analysis;
	}

	public void setAnalysis(AnalysisEx analysis) {
		this.analysis = analysis;
	}

	public Map<String, NutritionFact> getNutritionFacts() {
		return nutritionFacts;
	}

	public void setNutritionFacts(Map<String, NutritionFact> nutritionFacts) {
		this.nutritionFacts = nutritionFacts;
	}
	
	public Boolean getDisplayNutrition() {
		return displayNutrition;
	}

	public void setDisplayNutrition(Boolean displayNutrition) {
		this.displayNutrition = displayNutrition;
	}
	
	public static class CustomTime implements Serializable {

		private static final long serialVersionUID = 1L;

		private String time;
		private String label;

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
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

		public String getPrimary() {
			return primary;
		}

		public void setPrimary(String primary) {
			this.primary = primary;
		}

		public String getSecondary() {
			return secondary;
		}

		public void setSecondary(String secondary) {
			this.secondary = secondary;
		}

		public SliceableListEx<String> getPreparation() {
			return preparation;
		}

		public void setPreparation(SliceableListEx<String> preparation) {
			this.preparation = preparation;
		}

		public SliceableListEx<String> getCuisine() {
			return cuisine;
		}

		public void setCuisine(SliceableListEx<String> cuisine) {
			this.cuisine = cuisine;
		}

		public SliceableListEx<String> getCourse() {
			return course;
		}

		public void setCourse(SliceableListEx<String> course) {
			this.course = course;
		}

		public SliceableListEx<String> getOccasion() {
			return occasion;
		}

		public void setOccasion(SliceableListEx<String> occasion) {
			this.occasion = occasion;
		}

		public SliceableListEx<String> getSpecial() {
			return special;
		}

		public void setSpecial(SliceableListEx<String> special) {
			this.special = special;
		}
	}
}
