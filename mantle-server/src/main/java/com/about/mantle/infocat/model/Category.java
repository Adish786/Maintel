package com.about.mantle.infocat.model;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private List<Category> ancestors;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Category> getAncestors() {
		return ancestors;
	}
	public void setAncestors(List<Category> ancestors) {
		this.ancestors = ancestors;
	}
}
