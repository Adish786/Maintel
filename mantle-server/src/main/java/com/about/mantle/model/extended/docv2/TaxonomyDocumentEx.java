package com.about.mantle.model.extended.docv2;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.about.globe.core.exception.GlobeInvalidTaskParameterException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Iterables;

public class TaxonomyDocumentEx extends BaseDocumentEx {
	private static final long serialVersionUID = 1L;

    private ImageEx thumbImage;
    private SliceableListEx<PageEx> pages = SliceableListEx.emptyList();
 
    private List<ImageEx> images = Collections.emptyList();
    
    public TaxonomyDocumentEx() {
	}
    
    public TaxonomyDocumentEx(TaxonomyDocumentEx document) {
		super(document);
		this.thumbImage = document.getThumbImage();
		setPages(document.getPages());
	}

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
	public int calculateImageCount() {
		return images.size();
	}

    public ImageEx getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(ImageEx thumbImage) {
        this.thumbImage = thumbImage;
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
