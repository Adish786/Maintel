package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class MustReadsEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private SliceableListEx<LinkEx> links = SliceableListEx.emptyList();

	public SliceableListEx<LinkEx> getLinks() {
		return links;
	}

	public void setLinks(SliceableListEx<LinkEx> links) {
		this.links = SliceableListEx.emptyIfNull(links);
	}

}
