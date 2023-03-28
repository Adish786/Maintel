package com.about.mantle.model.extended.ugc;

import java.io.Serializable;

public class Photo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String url;
	private String mimeType;
	private Integer width;
	private Integer height;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}
}
