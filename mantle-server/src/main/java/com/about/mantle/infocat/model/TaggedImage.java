package com.about.mantle.infocat.model;

import com.about.mantle.model.extended.docv2.ImageEx;

import java.io.Serializable;
import java.util.List;

/**
 * InfoCat models cannot use selene's TaggedImage class. Selene stores image tags as a SliceableList,
 * while InfoCat uses a regular list. This class also uses String instead of an enum for tags.
 */
public class TaggedImage implements Serializable {
	private static final long serialVersionUID = 1L;

	private ImageEx image;
	private List<String> tags;

	public ImageEx getImage() {
		return image;
	}

	public void setImage(ImageEx image) {
		this.image = image;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TaggedImage{");
		sb.append("image='").append(image);
		sb.append("', tags='").append(tags);
		sb.append("'}");
		return sb.toString();
	}
}
