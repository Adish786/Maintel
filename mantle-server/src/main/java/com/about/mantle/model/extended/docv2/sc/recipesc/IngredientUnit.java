package com.about.mantle.model.extended.docv2.sc.recipesc;

import java.io.Serializable;

public class IngredientUnit implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id; 
	private String abbreviation;
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}