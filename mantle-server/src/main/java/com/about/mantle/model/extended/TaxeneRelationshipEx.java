package com.about.mantle.model.extended;

import java.io.Serializable;

public class TaxeneRelationshipEx implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private Float weight;
	private TaxeneNodeEx targetNode;

	public TaxeneRelationshipEx() {
	}

	public TaxeneRelationshipEx(String name, Float weight, TaxeneNodeEx targetNode) {
		this.name = name;
		this.weight = weight;
		this.targetNode = targetNode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public TaxeneNodeEx getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(TaxeneNodeEx targetNode) {
		this.targetNode = targetNode;
	}
}
