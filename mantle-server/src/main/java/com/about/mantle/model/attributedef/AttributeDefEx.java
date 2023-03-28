package com.about.mantle.model.attributedef;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.about.mantle.model.attributedef.value.AbstractAttributeValueEx.AttributeValueTypeEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class AttributeDefEx implements Serializable {

	private static final long serialVersionUID = 1L;

	// UUID will be generated for each attributedef
	private String id;

	// (name, service) should be unique.
	// this field will also be used as search index field name: only letters, digits and underscore are allowed
	private String name;

	private String displayName;

	// Description / instruction
	private String description;

	// once an AttributeDef is created, the valueType cannot be changed
	private AttributeValueTypeEx valueType;

	// The service this attributedef belong to. e.g. CreditCard
	private String service;

	// For better organization of attributes in a service. e.g. Reward
	private String group;

	/* 
	 * With below annotation weight list will not be serialized to redis and not be cached
	 * and thus flattening of list into map will only occur once while deserializing Selene response
	*/
	@JsonProperty(access = Access.WRITE_ONLY)
	private SliceableListEx<AttributeWeightEx> weights;
	
	//Map that contains flatten weight list
	private Map<String, Float> weightsMap;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AttributeValueTypeEx getValueType() {
		return valueType;
	}

	public void setValueType(AttributeValueTypeEx valueType) {
		this.valueType = valueType;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setWeights(SliceableListEx<AttributeWeightEx> weights) {
		this.weights = weights;
		
		if(this.weights != null && weightsMap == null) {
			this.weightsMap = new HashMap<>(this.weights.getTotalSize());
			for(AttributeWeightEx weight: this.weights.getList()) {
				if(weight.getCategory() != null) {
					weightsMap.putIfAbsent(weight.getCategory(), weight.getWeight());
				}
			}
		}
	}

	
	/**
	 * @return Map of (Category Name, Category Weight) for an attribute.
	 * 			This Map is generated from {@link AttributeDefEx#weights}
	 */
	public Map<String, Float> getWeightsMap() {
		return weightsMap;
	}
	
	public void setWeightsMap(Map<String, Float> weightsMap) {
		this.weightsMap = weightsMap;
	}

}