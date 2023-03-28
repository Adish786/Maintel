package com.about.mantle.model.extended.docv2;

import java.io.Serializable;

public class ItemEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private LinkEx link;
	private String linkType;
	private LinkEx reviewLink;
	private String description;
	private ImageEx image = ImageEx.EMPTY;
	private SliceableListEx<CommerceInfoEx> commerceInfo;

	public LinkEx getLink() {
		return link;
	}

	public void setLink(LinkEx link) {
		this.link = link;
	}

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public LinkEx getReviewLink() {
		return reviewLink;
	}

	public void setReviewLink(LinkEx reviewLink) {
		this.reviewLink = reviewLink;
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

	public SliceableListEx<CommerceInfoEx> getCommerceInfo() {
		return commerceInfo;
	}

	public void setCommerceInfo(SliceableListEx<CommerceInfoEx> commerceInfo) {
		this.commerceInfo = commerceInfo;
	}

}
