package com.about.mantle.model.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.docv2.sc.recipesc.Nutrient;
import com.about.mantle.model.extended.docv2.sc.recipesc.NutritionAnalysis;

@Tasks

/**
 * Structured Nutrition Task
 *  
 * These tasks are used for manipulating and formatting nutrition information from nutritionx
 */
public class StructuredNutritionTask {
	private Map<Integer, String> allowedNutrients;
	
	/**
	 * Structured Nutrition Task
	 *  
	 * @param allowedNutrients - mapping of allowed nutrient ids to accepted nutrient names
	 */
	public StructuredNutritionTask(Map<Integer, String> allowedNutrients){
		this.allowedNutrients = allowedNutrients;
	}

	/**
	 * Prune Unused Fields
	 *  
	 * Gathers important nutrition fields to display on front end
	 * @param nutritionAnalysis - Nutrition information returned from nutritionx
	 * @return mapping of accepted nutrient names and their information models
	 */
	@Task(name = "pruneUnusedFields")
	public Map<String, NutritionModel> pruneUnusedFields (@TaskParameter(name = "nutritionAnalysis") NutritionAnalysis nutritionAnalysis){
		if(nutritionAnalysis == null || nutritionAnalysis.getNutrients() == null) return null;

		List<Nutrient> nutrientsFromDocument = nutritionAnalysis.getNutrients().getList();
		Map<String, NutritionModel> nutrientsForApp = new HashMap<>();
		
		for(Nutrient nutrient : nutrientsFromDocument){
			if(allowedNutrients.containsKey(nutrient.getId())){
				nutrientsForApp.put(allowedNutrients.get(nutrient.getId()), new NutritionModel(nutrient.getValue(), nutrient.getUnit()));
			}
		}

		return nutrientsForApp;
	}
	
	public static class NutritionModel {

		private Float quantity;
		private String unit;
		
		public NutritionModel(Float quantity, String unit){
			this.quantity = quantity;
			this.unit = unit;
		}
		
		public Float getQuantity() {
			return quantity;
		}
		public void setQuantity(Float quantity) {
			this.quantity = quantity;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
	}
}
