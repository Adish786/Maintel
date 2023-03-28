package com.about.mantle.model.extended.docv2;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.about.globe.core.exception.GlobeInvalidTaskParameterException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Iterables;

public class FlexibleArticleDocumentEx extends BaseDocumentEx implements Linkboxed {

	private static final long serialVersionUID = 1L;

	private String rating;
	private SliceableListEx<LinkboxEx> linkboxes = SliceableListEx.emptyList();
	private SliceableListEx<PageEx> pages = SliceableListEx.emptyList();

	private List<ImageEx> images = Collections.emptyList();
	private List<LinkboxLinkEx> linkboxList;

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
	public boolean isReview() {
		return !isBlank(rating) && !rating.equals("0");
	}

	@Override
	@JsonIgnore
	public int calculateImageCount() {
		return images.size();
	}
	
	@Override
	public String getHeading() {
		return getPages().getList().size() > 0 ? getPages().getList().get(0).getHeading() : super.getHeading();
	}
	
	@Override
	@JsonIgnore
	public String getSubheading(){
		return getPages().getList().size() > 0 ? getPages().getList().get(0).getSubheading() : super.getSubheading();
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	@Override
	public SliceableListEx<LinkboxEx> getLinkboxes() {
		return linkboxes;
	}

	public void setLinkboxes(SliceableListEx<LinkboxEx> linkboxes) {
		this.linkboxes = SliceableListEx.emptyIfNull(linkboxes);
		this.linkboxList = transformLinkboxes();
	}

	@Override
	@JsonIgnore
	public List<LinkboxLinkEx> getLinkboxesAsList() {
		return linkboxList;
	}
	
	public SliceableListEx<PageEx> getPages() {
		return pages;
	}

	public void setPages(SliceableListEx<PageEx> pages) {
		this.pages = SliceableListEx.emptyIfNull(pages);

		if (this.pages.isEmpty()) this.images = Collections.emptyList();
		else this.images = this.pages.getList().get(0).getImages().getList().stream()
				.filter(image -> image != null && isNotBlank(image.getUrl())).collect(Collectors.toList());
	}
}
