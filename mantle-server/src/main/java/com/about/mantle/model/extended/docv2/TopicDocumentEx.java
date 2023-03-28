package com.about.mantle.model.extended.docv2;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TopicDocumentEx extends BaseDocumentEx {

	private static final long serialVersionUID = 1L;

	private SliceableListEx<ImageEx> images = SliceableListEx.emptyList();

	public SliceableListEx<ImageEx> getImages() {
		return images;
	}
	
	public void setImages(SliceableListEx<ImageEx> images) {
		if (images == null) this.images = SliceableListEx.emptyList();
		this.images = images;
	}
	
	@Override
	@JsonIgnore
	public int calculateImageCount() {
		return images.getTotalSize();
	}
	
	@Override
	public String getMetaDescription() {

		if (isNotBlank(super.getMetaDescription())) return super.getMetaDescription();

		return getSummary().getDescription();
	}
}
