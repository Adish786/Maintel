package com.about.mantle.venus.utils;
public class DocType  {
	
	private String id;
	private String name;

	public String id() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "DocType [id=" + id + ", name=" + name + "]";
	}

}
