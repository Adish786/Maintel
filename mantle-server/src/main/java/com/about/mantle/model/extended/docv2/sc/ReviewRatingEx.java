package com.about.mantle.model.extended.docv2.sc;

import java.io.Serializable;
import java.math.BigDecimal;

public class ReviewRatingEx implements Serializable {

	private static final long serialVersionUID = 1L;
	private BigDecimal value;

	public BigDecimal getValue() { return value; }

	public void setValue(BigDecimal value) {
		this.value = value;
	}
}
