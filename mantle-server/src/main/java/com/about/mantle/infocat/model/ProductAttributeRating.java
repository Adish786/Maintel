package com.about.mantle.infocat.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductAttributeRating implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal value;
	private Attribute attribute;
	private String attributeId;
	
	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}

	public class Attribute implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		
	}
}
