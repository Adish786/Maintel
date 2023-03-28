package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class TaggedImage implements Serializable {

	private static final long serialVersionUID = 1L;

	private ImageEx image;
	private SliceableListEx<String> tags;

	@Deprecated
	public static enum UsageFlag {
		FACEBOOK, PINTEREST, PRIMARY, RECIRC, TWITTER, SCHEMA, FINALPROJECT
	}

	public ImageEx getImage() {
		return image;
	}

	public void setImage(ImageEx image) {
		this.image = image;
	}

	public SliceableListEx<String> getTags() {
		return tags;
	}

	public void setTags(SliceableListEx<String> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TaggedImage{");
		sb.append("image=").append(image);
		sb.append(", tags=").append(tags);
		sb.append('}');
		return sb.toString();
	}

}