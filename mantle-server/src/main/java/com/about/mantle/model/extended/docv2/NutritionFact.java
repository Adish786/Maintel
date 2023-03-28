package com.about.mantle.model.extended.docv2;

public class NutritionFact {
	public NutritionFact() {
	}

	public NutritionFact(Double quantity, Integer dailyValue, String unit) {
		this.quantity = quantity;
		this.dailyValue = dailyValue;
		this.unit = unit;
	}

	private Double quantity;
	private Integer dailyValue;
	private String unit;

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	
	public Integer getDailyValue() {
		return dailyValue;
	}

	public void setDailyValue(Integer dailyValue) {
		this.dailyValue = dailyValue;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
