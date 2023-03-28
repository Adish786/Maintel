package com.about.mantle.model.extended.docv2;



public class LinkboxLinkEx {
	private String linkboxHeading;
	private String text;
	private String uri;
	private Boolean external;
	private BaseDocumentEx document;

	public LinkboxLinkEx(String linkboxHeading, String text, String uri, Boolean external, BaseDocumentEx document) {
		this.linkboxHeading = linkboxHeading;
		this.text = text;
		this.uri = uri;
		this.external = external;
		this.document = document;
	}

	@SuppressWarnings("unused")
	private LinkboxLinkEx(){} // for serialization

	public String getLinkboxHeading() {
		return linkboxHeading;
	}

	public void setLinkboxHeading(String linkboxHeading) {
		this.linkboxHeading = linkboxHeading;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public boolean isExternal(){
		return external;
	}
	
	public void setExternal(boolean external){
		this.external = external;
	}

	public BaseDocumentEx getDocument() {
		return document;
	}

	public void setDocument(BaseDocumentEx document) {
		this.document = document;
	}
	
}
