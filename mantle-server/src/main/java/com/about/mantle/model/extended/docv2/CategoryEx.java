package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class CategoryEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String color;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

}
