package com.about.mantle.model.extended.docv2;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.List;

import com.about.globe.core.exception.GlobeInvalidTaskParameterException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class StepByStepDocumentEx extends BaseDocumentEx implements Linkboxed {

	private static final long serialVersionUID = 1L;

	private SliceableListEx<PageEx> pages = SliceableListEx.emptyList();
	private SliceableListEx<LinkboxEx> linkboxes = SliceableListEx.emptyList();
	private SliceableListEx<String> labels = SliceableListEx.emptyList();
	private Boolean isImageGallery = false;

	private List<ImageEx> images = Collections.emptyList();
	private List<LinkboxLinkEx> linkboxList = Collections.emptyList();

	@Override
	@JsonIgnore
	public int getRemainingLength(int currentBlock, int currentPage, boolean currentPageOnly)
			throws GlobeInvalidTaskParameterException {

		int length = 0;

		if (currentPageOnly) {
			PageEx page = getPages().getList().get(currentPage);
			length += getRemainingLength(page.getIntro().getList(), currentBlock);
			length += getRemainingLength(page.getContent().getList(), currentBlock);
		} else {
			for (PageEx page : Iterables.skip(getPages().getList(), currentPage)) {
				length += getRemainingLength(page.getIntro().getList(), currentBlock);
				length += getRemainingLength(page.getContent().getList(), currentBlock);
			}
		}

		return length;
	}

	@Override
	@JsonIgnore
	public List<LinkboxLinkEx> getLinkboxesAsList() {
		return linkboxList;
	}

	@Override
	@JsonIgnore
	public int calculateImageCount() {
		return images.size();
	}
	
	@JsonIgnore
	public List<ImageEx> getImages(){
		return images;
	}

	public SliceableListEx<PageEx> getPages() {
		return pages;
	}

	public void setPages(SliceableListEx<PageEx> pages) {
		this.pages = pages != null ? pages : SliceableListEx.<PageEx> emptyList();

		ImmutableList.Builder<ImageEx> builder = ImmutableList.builder();
		for (PageEx page : this.pages.getList()) {
			for (ImageEx image : page.getImages().getList()) {
				if (image != null && isNotBlank(image.getUrl())) builder.add(image);
			}
		}
		this.images = builder.build();
	}

	@Override
	public SliceableListEx<LinkboxEx> getLinkboxes() {
		return linkboxes;
	}

	public void setLinkboxes(SliceableListEx<LinkboxEx> linkboxes) {
		this.linkboxes = SliceableListEx.emptyIfNull(linkboxes);
		this.linkboxList = transformLinkboxes();
	}

	public SliceableListEx<String> getLabels() {
		return labels;
	}

	public void setLabels(SliceableListEx<String> labels) {
		this.labels = SliceableListEx.emptyIfNull(labels);
	}

	public Boolean getIsImageGallery() {
		return isImageGallery;
	}

	public void setIsImageGallery(Boolean isImageGallery) {
		this.isImageGallery = isImageGallery;
	}
}
