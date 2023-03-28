package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class SectionEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private String heading;
	private String description;
	private ImageEx image = ImageEx.EMPTY;
	private SliceableListEx<LinkEx> links = SliceableListEx.<LinkEx> emptyList();

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ImageEx getImage() {
		return image;
	}

	public void setImage(ImageEx image) {
		this.image = ImageEx.emptyIfNull(image);
	}

	public SliceableListEx<LinkEx> getLinks() {
		return links;
	}

	public void setLinks(SliceableListEx<LinkEx> links) {
		this.links = SliceableListEx.emptyIfNull(links);
	}

}
