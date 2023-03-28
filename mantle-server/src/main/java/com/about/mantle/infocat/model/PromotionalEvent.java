package com.about.mantle.infocat.model;

import java.io.Serializable;

public class PromotionalEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private String type;
	
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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("PromotionalEvent{");
		sb.append("id='").append(id);
		sb.append("', name='").append(name);
		sb.append("', type='").append(type);
		sb.append("'}");
		return sb.toString();
	}
	
}
