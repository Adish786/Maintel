package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class LinkEx implements Serializable {
	private static final long serialVersionUID = 1L;

	private String text;
	private String uri;
	private String frame;
	private Boolean external;
	private BaseDocumentEx document;

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

	public String getFrame() {
		return frame;
	}

	public void setFrame(String frame) {
		this.frame = frame;
	}

	public Boolean getExternal() {
		return external;
	}

	public void setExternal(Boolean external) {
		this.external = external;
	}

	public BaseDocumentEx getDocument() {
		return document;
	}

	public void setDocument(BaseDocumentEx document) {
		this.document = document;
	}
}
