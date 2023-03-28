package com.about.mantle.model.extended.docv2;

import java.io.Serializable;
import java.math.BigDecimal;

public class Ingredient implements Serializable {

	private static final long serialVersionUID = 1L;

	private BigDecimal quantity;
	private String unit;
	private String text;

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
