package com.about.mantle.model.attributedef.value;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, visible = true, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", defaultImpl = AttributeUnknownValueEx.class)
@JsonSubTypes({
@JsonSubTypes.Type(value = AttributeBooleanValueEx.class, name = "BOOLEAN"),
@JsonSubTypes.Type(value = AttributeIntegerValueEx.class, name = "INTEGER"),
@JsonSubTypes.Type(value = AttributeFloatValueEx.class, name = "FLOAT"),
@JsonSubTypes.Type(value = AttributeStringValueEx.class, name = "STRING"),
@JsonSubTypes.Type(value = AttributeStringListValueEx.class, name = "STRING_LIST"),
@JsonSubTypes.Type(value = AttributePercentageValueEx.class, name = "PERCENTAGE"),
@JsonSubTypes.Type(value = AttributeCurrencyValueEx.class, name = "CURRENCY"),
@JsonSubTypes.Type(value = AttributePeriodValueEx.class, name = "PERIOD"),
@JsonSubTypes.Type(value = AttributePercentageRangeValueEx.class, name = "PERCENTAGE_RANGE"),
@JsonSubTypes.Type(value = AttributeCurrencyRangeValueEx.class, name = "CURRENCY_RANGE"),
@JsonSubTypes.Type(value = AttributeNameValueListValueEx.class, name = "NAME_VALUE_LIST")
})
public abstract class AbstractAttributeValueEx implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public abstract Object getValue();
	
	public enum AttributeValueTypeEx {
		BOOLEAN,
		INTEGER,
		FLOAT,
		STRING,
		STRING_LIST,
		PERCENTAGE,
		CURRENCY,
		PERIOD,
		PERCENTAGE_RANGE,
		CURRENCY_RANGE,
		NAME_VALUE_LIST
	}
}
