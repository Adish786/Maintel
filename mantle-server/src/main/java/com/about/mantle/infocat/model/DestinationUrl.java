package com.about.mantle.infocat.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class DestinationUrl implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String url;
	private String type;
	private String domain;
	private boolean monetized;
	
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

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public boolean isMonetized() {
		return monetized;
	}

	public void setMonetized(boolean monetized) {
		this.monetized = monetized;
	}
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("DestinationUrl{");
		sb.append("id='").append(id);
		sb.append("', url='").append(url);
		sb.append("', type='").append(type);
		sb.append("', domain='").append(domain);
		sb.append("', monetized='").append(monetized);
		sb.append("'}");
		return sb.toString();
	}
}
