package com.about.mantle.model.extended.bookmarks;

import org.apache.commons.lang3.StringUtils;

import com.about.globe.core.exception.GlobeException;

public class BookmarksRequestContext {
	private final String accessToken;
	private final String hashId;
	private final Long docId;

	private BookmarksRequestContext(String accessToken, String hashId, Long docId) {
		this.accessToken = accessToken;
		this.hashId = hashId;
		this.docId = docId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getHashId() {
		return hashId;
	}

	public Long getDocId() {
		return docId;
	}

	public static class Builder {
		private String accessToken;
		private String hashId;
		private Long docId;

		public Builder accessToken(String accessToken) {
			this.accessToken = accessToken;
			return this;
		}

		public Builder hashId(String hashId) {
			this.hashId = hashId;
			return this;
		}

		public Builder docId(Long docId) {
			this.docId = docId;
			return this;
		}

		public Builder docId(String docId) {
			try {
				this.docId = Long.parseLong(docId);
			} catch (NumberFormatException e) {
				throw new GlobeException("docId is invalid", e);
			}
			return this;
		}

		public BookmarksRequestContext build() {
			if (StringUtils.isBlank(accessToken)) {
				throw new GlobeException("accessToken is required");
			}
			if (StringUtils.isBlank(hashId)) {
				throw new GlobeException("hashId is required");
			}
			if (docId == null || docId <= 0) {
				throw new GlobeException("docId is invalid");
			}
			return new BookmarksRequestContext(accessToken, hashId, docId);
		}

	}
}
