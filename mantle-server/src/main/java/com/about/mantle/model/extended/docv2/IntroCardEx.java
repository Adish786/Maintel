package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.Sponsor;


public class IntroCardEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private String heading;
	private String description;
	private ImageRenditionsEx imageRenditions = ImageRenditionsEx.EMPTY;
	private Sponsor sponsor;

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

	public ImageRenditionsEx getImageRenditions() {
		return imageRenditions;
	}

	public void setImageRenditions(ImageRenditionsEx imageRenditions) {
		this.imageRenditions = ImageRenditionsEx.emptyIfNull(imageRenditions);
	}

	public Sponsor getSponsor() {
		return sponsor;
	}

	public void setSponsor(Sponsor sponsor) {
		this.sponsor = sponsor;
	}
}
