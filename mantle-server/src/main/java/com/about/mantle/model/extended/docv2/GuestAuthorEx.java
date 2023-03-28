package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class GuestAuthorEx implements Serializable{

	private static final long serialVersionUID = 1L;

	private LinkEx link;
	private String shortBio;
	
	public LinkEx getLink() {
		return link;
	}
	public void setLink(LinkEx link) {
		this.link = link;
	}
	public String getShortBio() {
		return shortBio;
	}
	public void setShortBio(String shortBio) {
		this.shortBio = shortBio;
	}
}
