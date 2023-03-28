package com.about.mantle.model.extended.docv2.sc;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.sc.quantity.QuantityRange;

public class MaterialEx implements Serializable {

	private static final long serialVersionUID = 1L;
	private QuantityRange quantityRange;
	private String unit;
	private String description;

	public QuantityRange getQuantityRange() {
		return quantityRange;
	}

	public void setQuantityRange(QuantityRange quantityRange) {
		this.quantityRange = quantityRange;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
