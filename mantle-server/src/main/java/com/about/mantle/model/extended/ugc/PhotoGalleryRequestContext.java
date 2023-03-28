package com.about.mantle.model.extended.ugc;

import com.about.globe.core.exception.GlobeException;

public class PhotoGalleryRequestContext {
	private static final int DEFAULT_OFFSET = 0;
	private static final int DEFAULT_LIMIT = 10;
	private static final int MAX_LIMIT = 1000;

	private final Long docId;
	private final Integer offset;
	private final Integer limit;

	private PhotoGalleryRequestContext(Builder builder) {
		docId = builder.docId;
		offset = builder.offset == null ? DEFAULT_OFFSET : builder.offset;
		limit = builder.limit == null ? DEFAULT_LIMIT : ((builder.limit > MAX_LIMIT) ? MAX_LIMIT : builder.limit);

		if (docId == null) {
			throw new GlobeException("A docId is required.");
		}
	}

	public Long getDocId() {
		return docId;
	}

	public Integer getOffset() {
		return offset;
	}

	public Integer getLimit() {
		return limit;
	}

	@Override
	public String toString() {
		return "PhotoGalleryRequestContext [docId=" + docId + ", limit=" + limit + ", offset=" + offset + "]";
	}

	public static class Builder {
		private Long docId;
		private Integer offset;
		private Integer limit;

		public Builder docId(Long docId) {
			this.docId = docId;
			return this;
		}

		public Builder offset(Integer offset) {
			this.offset = offset;
			return this;
		}

		public Builder limit(Integer limit) {
			this.limit = limit;
			return this;
		}

		public PhotoGalleryRequestContext build() {
			return new PhotoGalleryRequestContext(this);
		}
	}
}
