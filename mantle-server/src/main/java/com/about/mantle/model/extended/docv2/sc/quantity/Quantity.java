package com.about.mantle.model.extended.docv2.sc.quantity;

import org.apache.commons.lang3.math.Fraction;

import java.io.Serializable;

public class Quantity implements Serializable {
	private static final long serialVersionUID = 1L;

	// Those 2 fields cannot be null and must be positive (see DocumentTemplateValipare)
	private Integer numerator;
	private Integer denominator;

	public Integer getNumerator() {
		return numerator;
	}

	public void setNumerator(Integer numerator) {
		this.numerator = numerator;
	}

	public Integer getDenominator() {
		return denominator;
	}

	public void setDenominator(Integer denominator) {
		this.denominator = denominator;
	}

	public Float toFloat() {
		return Fraction.getFraction(numerator, denominator).floatValue();
	}

	@Override
	public String toString() {
		return Fraction.getFraction(numerator, denominator).toProperString();
	}
}