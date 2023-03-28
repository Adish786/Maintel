package com.about.mantle.model.contentgraph;

/**
 * Used for storing/serving public assets from legacy Meredith's infrastructure.
 */
public class PresignedUrl {
	private String uri; // where to upload the asset
	private String publicUri; // where to access the asset once uploaded

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getPublicUri() {
		return publicUri;
	}

	public void setPublicUri(String publicUri) {
		this.publicUri = publicUri;
	}

	@Override
	public String toString() {
		return "PresignedUrl [publicUri=" + publicUri + ", uri=" + uri + "]";
	}
}
