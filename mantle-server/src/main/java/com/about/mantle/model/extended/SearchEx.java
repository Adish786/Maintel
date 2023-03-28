package com.about.mantle.model.extended;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;

public class SearchEx implements Serializable{
	private static final long serialVersionUID = 1L;

	private String title;
	private String description;
	private String url;
	private BaseDocumentEx document;

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public BaseDocumentEx getDocument() {
		return document;
	}	
	public void setDocument(BaseDocumentEx document) {
		this.document = document;
	}
}
