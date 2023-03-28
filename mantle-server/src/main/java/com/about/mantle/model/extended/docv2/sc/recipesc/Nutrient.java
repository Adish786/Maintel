package com.about.mantle.model.extended.docv2.sc.recipesc;

import java.io.Serializable;

public class Nutrient implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private String abbreviation;
	private String unit;
	private Float value;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}
}