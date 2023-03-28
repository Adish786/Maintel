package com.about.mantle.model.extended.docv2.sc.recipesc;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.sc.quantity.QuantityRange;

public class Ingredient implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;  // id is not required (since we switch from esha to nutritionix)
	private String name;
	private String description;
	private String annotatedIngredient;
	private String originalIngredientText;
	private String meredithId;
	private IngredientUnit unit;
	private QuantityRange quantity;
	private Boolean optional;
	private Boolean garnish;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAnnotatedIngredient() {
		return annotatedIngredient;
	}

	public void setAnnotatedIngredient(String annotatedIngredient) {
		this.annotatedIngredient = annotatedIngredient;
	}

	public String getMeredithId() {
		return meredithId;
	}

	public void setMeredithId(String meredithId) {
		this.meredithId = meredithId;
	}

	public String getOriginalIngredientText() {
		return originalIngredientText;
	}

	public void setOriginalIngredientText(String originalIngredientText) {
		this.originalIngredientText = originalIngredientText;
	}

	public Boolean getGarnish() {
		return garnish;
	}

	public void setGarnish(Boolean garnish) {
		this.garnish = garnish;
	}
	public IngredientUnit getUnit() {
		return unit;
	}

	public void setUnit(IngredientUnit unit) {
		this.unit = unit;
	}

	public QuantityRange getQuantity() {
		return quantity;
	}

	public void setQuantity(QuantityRange quantity) {
		this.quantity = quantity;
	}

	public Boolean getOptional() {
		return optional;
	}

	public void setOptional(Boolean optional) {
		this.optional = optional;
	}
}
