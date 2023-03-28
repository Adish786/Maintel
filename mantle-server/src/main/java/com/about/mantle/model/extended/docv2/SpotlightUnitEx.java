package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class SpotlightUnitEx implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private LinkEx heading;
	private String description;
	private ImageEx image = ImageEx.EMPTY;
	private FurtherReadingEx furtherReading;

	public LinkEx getHeading() {
		return heading;
	}

	public void setHeading(LinkEx heading) {
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

	public FurtherReadingEx getFurtherReading() {
		return furtherReading;
	}

	public void setFurtherReading(FurtherReadingEx furtherReading) {
		this.furtherReading = furtherReading;
	}
}
