package com.about.mantle.utils.selene.version;

import com.google.common.net.MediaType;

public enum Version {
	V1_UTF8(MediaType.JSON_UTF_8.toString()),
	V1(MediaType.create("application", "vnd.abt.v1+json").toString()),
	V2(MediaType.create("application", "vnd.abt.v2+json").toString()),
	V3(MediaType.create("application", "vnd.abt.v3+json").toString());

	private final String contentType;

	private Version(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}
}
