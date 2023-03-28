package com.about.mantle.model.extended;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.about.mantle.model.extended.docv2.SliceableListEx;

public class SuggestionSearchResultItemEx implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String message;
	private String url;
	private Vertical vertical;
	private Float weight;
	private SliceableListEx<String> tags;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Vertical getVertical() {
		return vertical;
	}

	public void setVertical(Vertical vertical) {
		this.vertical = vertical;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public SliceableListEx<String> getTags() {
		return tags;
	}

	public void setTags(SliceableListEx<String> tags) {
		this.tags = tags;
	}
}
