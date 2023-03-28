package com.about.mantle.model.extended.quiz;

import java.io.Serializable;

import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.LinkboxEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;

public class QuizResultEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String title;
	private String description;
	private ImageEx image = ImageEx.EMPTY;
	private SliceableListEx<DimensionEx> dimensions = SliceableListEx.emptyList();
	private SliceableListEx<LinkboxEx> linkboxes = SliceableListEx.emptyList();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public SliceableListEx<DimensionEx> getDimensions() {
		return dimensions;
	}

	public void setDimensions(SliceableListEx<DimensionEx> dimensions) {
		this.dimensions = SliceableListEx.emptyIfNull(dimensions);
	}

	public SliceableListEx<LinkboxEx> getLinkboxes() {
		return linkboxes;
	}

	public void setLinkboxes(SliceableListEx<LinkboxEx> linkboxes) {
		this.linkboxes = SliceableListEx.emptyIfNull(linkboxes);
	}
}
