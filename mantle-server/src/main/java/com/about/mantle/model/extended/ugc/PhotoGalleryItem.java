package com.about.mantle.model.extended.ugc;

import java.io.Serializable;

public class PhotoGalleryItem implements Serializable {
	private static final long serialVersionUID = 1L;

	private String displayName;
	private String profileUrl;
	private Photo photo;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}
}
