package com.about.mantle.model.services.embeds;

import java.io.Serializable;

public class EmbedContent implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Type { HTML, IMG }

	private Type type;
	private String provider; // e.g. twitter, giphy, etc.
	private String content;  // varies with type
	// Strings are used for width and height as TikTok provides "100%" as its oembed dimensions
	private String width;
	private String height;

	public EmbedContent() {
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

}
