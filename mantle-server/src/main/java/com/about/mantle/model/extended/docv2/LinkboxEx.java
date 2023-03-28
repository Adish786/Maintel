package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class LinkboxEx implements Serializable {
	private static final long serialVersionUID = 1L;

	private String heading;
	private SliceableListEx<LinkEx> links = SliceableListEx.emptyList();
	private String webtag;

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getWebtag() {
		return webtag;
	}

	public void setWebtag(String webtag) {
		this.webtag = webtag;
	}

	public SliceableListEx<LinkEx> getLinks() {
		return links;
	}

	public void setLinks(SliceableListEx<LinkEx> links) {
		this.links = SliceableListEx.emptyIfNull(links);
	}
}
