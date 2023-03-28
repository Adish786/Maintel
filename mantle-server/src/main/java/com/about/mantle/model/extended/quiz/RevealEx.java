package com.about.mantle.model.extended.quiz;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.ImageEx;

public class RevealEx implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String text;
	private ImageEx image = ImageEx.EMPTY; 

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ImageEx getImage() {
		return image;
	}

	public void setImage(ImageEx image) {
		if (image == null || image.getWidth(0) <= 0 || image.getHeight(0) <= 0) {
			image = ImageEx.EMPTY;
		}
		this.image = image;
	}
}
