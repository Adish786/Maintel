package com.about.mantle.model.extended.docv2.sc.quantity;

import java.io.Serializable;

public class QuantityRange implements Serializable {
	private static final long serialVersionUID = 1L;

	private Quantity min;
	private Quantity max;

	public Quantity getMin() {
		return min;
	}

	public void setMin(Quantity min) {
		this.min = min;
	}

	public Quantity getMax() {
		return max;
	}

	public void setMax(Quantity max) {
		this.max = max;
	}
}
