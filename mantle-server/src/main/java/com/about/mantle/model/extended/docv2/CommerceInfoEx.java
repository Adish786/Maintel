package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

import com.about.mantle.model.commerce.CommerceModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class CommerceInfoEx implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id; // url of the link
	private String type;
	
	/* 
	 * With below annotation commerceModel list will not be deserialized from redis and will only
	 * be set after commerce info was deserialized, 
	 * e.g. via the task (which pulls it from a separate cache)
	*/
	@JsonProperty(access = Access.READ_ONLY)
	private CommerceModel commerceModel; // not populated by selene

	public CommerceInfoEx() {
		
	}
	
	public CommerceInfoEx(CommerceInfoEx commerceInfo) {
		this.id = commerceInfo.getId();
		this.type = commerceInfo.getType();
		this.commerceModel = commerceInfo.getCommerceModel();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CommerceModel getCommerceModel() {
		return commerceModel;
	}

	public void setCommerceModel(CommerceModel commerceModel) {
		this.commerceModel = commerceModel;
	}

	@Override
	public String toString() {
		return "CommerceInfoEx [id=" + id + ", type=" + type + ", commerceModel=" + commerceModel + "]";
	}
	
}
