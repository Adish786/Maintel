package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class FurtherReadingEx implements Serializable {
	private static final long serialVersionUID = 1L;

	private String subheading;
	private SliceableListEx<LinkEx> links = SliceableListEx.emptyList();

	public String getSubheading() {
		return subheading;
	}

	public void setSubheading(String subheading) {
		this.subheading = subheading;
	}

	public SliceableListEx<LinkEx> getLinks() {
		return links;
	}

	public void setLinks(SliceableListEx<LinkEx> links) {
		this.links = SliceableListEx.emptyIfNull(links);
	}
}
