package com.about.mantle.model.creditcard;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class CreditCardAttributeView implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String name;
	private String displayName;
	private String valueType;
	private Object value;
	private Float score;
	private Map<String, Float> weightsMap;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return this.displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getValueType() {
		return this.valueType;
	}
	
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	
	public Object getValue() {
		return this.value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public Float getScore() {
		return score;
	}
	
	public void setScore(Float score) {
		this.score = score;
	}

	public Map<String, Float> getWeightsMap() {
		return this.weightsMap;
	}
	
	public void setWeightsMap(Map<String, Float> weightsMap) {
		this.weightsMap = weightsMap;
	}

	public Float getWeight(String category) {
		return Objects.nonNull(this.weightsMap) ? weightsMap.get(category) : null;
	}
}
