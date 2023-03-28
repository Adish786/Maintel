package com.about.mantle.model;

public class IconValue {
	private String src;
	private String type;
	private String sizes;

	public IconValue(String src, String type, String sizes) {
		this.src = src;
		this.type = type;
		this.sizes = sizes;
	}
	
	public String getSrc() {
		return this.src;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getSizes() {
		return this.sizes;
	}
}
