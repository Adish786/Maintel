package com.about.mantle.infocat.model;

import java.io.Serializable;

import com.about.mantle.model.commerce.CommerceModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class Retailer implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String url;
	private String type;
	private String cta;
	private boolean outOfStock;
	
	/* 
	 * With below annotation commerceModel list will not be deserialized from redis and will only
	 * be set after the retailer was deserialized, 
	 * e.g. via the task (which pulls it from a separate cache)
	*/
	@JsonProperty(access = Access.READ_ONLY)
	private CommerceModel commerceModel; // not populated by selene

	public Retailer() {
		
	}
	
	public Retailer(Retailer retailer) {
		this.id = retailer.getId();
		this.url = retailer.getUrl();
		this.type = retailer.getType();
		this.outOfStock = retailer.isOutOfStock();
		this.commerceModel = retailer.getCommerceModel();
		this.cta = retailer.getCta();
	}

	public Retailer(String url, String cta) {
		this.url = url;
		this.cta = cta;
		this.type = "OTHER";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getCta() {
		return cta;
	}
	
	public void setCta(String cta) {
		this.cta = cta;
	}

	public boolean isOutOfStock() {
		return outOfStock;
	}

	public void setOutOfStock(boolean outOfStock) {
		this.outOfStock = outOfStock;
	}
	
	public CommerceModel getCommerceModel() {
		return commerceModel;
	}

	public void setCommerceModel(CommerceModel commerceModel) {
		this.commerceModel = commerceModel;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Retailer{");
		sb.append("id='").append(id);
		sb.append("', url='").append(url);
		sb.append("', type='").append(type);
		sb.append("', outOfStock='").append(outOfStock);
		sb.append("', commerceModel='").append(commerceModel);
		sb.append("'}");
		return sb.toString();
	}
}
