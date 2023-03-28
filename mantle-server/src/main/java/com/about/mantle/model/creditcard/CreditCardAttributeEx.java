package com.about.mantle.model.creditcard;

import java.io.Serializable;

import com.about.mantle.model.attributedef.AttributeDefEx;
import com.about.mantle.model.attributedef.value.AbstractAttributeValueEx;

public class CreditCardAttributeEx implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String attributeDefId;
	private AbstractAttributeValueEx value;
	private Float score;
	private AttributeDefEx attributeDef;
	
	public String getAttributeDefId() {
		return attributeDefId;
	}

	public void setAttributeDefId(String attributeDefId) {
		this.attributeDefId = attributeDefId;
	}

	public AbstractAttributeValueEx getValue() {
		return value;
	}

	public void setValue(AbstractAttributeValueEx value) {
		this.value = value;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public AttributeDefEx getAttributeDef() {
		return attributeDef;
	}

	public void setAttributeDef(AttributeDefEx attributeDef) {
		this.attributeDef = attributeDef;
	}
}
