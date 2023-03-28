package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class CategoryLinkEx implements Serializable {
	private static final long serialVersionUID = 1L;

	private String heading;
	private String linkHeading;
	private String annotation;
	private LinkEx link;
	private String defaultTrail;
	private String linkType;
	private String documentId;
	
	public String getHeading() {
		return heading;
	}
	public void setHeading(String heading) {
		this.heading = heading;
	}
	public String getLinkHeading() {
		return linkHeading;
	}
	public void setLinkHeading(String linkHeading) {
		this.linkHeading = linkHeading;
	}
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	public LinkEx getLink() {
		return link;
	}
	public void setLink(LinkEx link) {
		this.link = link;
	}
	public String getDefaultTrail() {
		return defaultTrail;
	}
	public void setDefaultTrail(String defaultTrail) {
		this.defaultTrail = defaultTrail;
	}
	public String getLinkType() {
		return linkType;
	}
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
}
