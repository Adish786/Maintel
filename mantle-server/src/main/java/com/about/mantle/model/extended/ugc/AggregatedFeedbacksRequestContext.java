package com.about.mantle.model.extended.ugc;

import com.about.globe.core.exception.GlobeException;

public class AggregatedFeedbacksRequestContext {
	private final Long docId;
	private final SortBy sort;
	private final Integer offset;
	private final Integer limit;

	private static void validateField(String name, Object value) {
		if (value == null) {
			throw new GlobeException(name + " is required");
		}
	}

	private AggregatedFeedbacksRequestContext(Builder builder) {
		docId = builder.docId;
		sort = builder.sort;
		offset = builder.offset;
		limit = builder.limit;

		validateField("docId", docId);
		validateField("sort", sort);
		validateField("offset", offset);
		validateField("limit", limit);
	}

	public Long getDocId() {
		return docId;
	}
	public SortBy getSort() {
		return sort;
	}
	public Integer getOffset() {
		return offset;
	}
	public Integer getLimit() {
		return limit;
	}

	@Override
	public String toString() {
		return "AggregatedFeedbacksRequestContext [docId=" + docId + ", limit=" + limit +
				", offset=" + offset + ", sort=" + sort + "]";
	}

	public static class Builder {
		private Long docId;
		private SortBy sort;
		private Integer offset;
		private Integer limit;

		public Builder docId(Long docId) {
			this.docId = docId;
			return this;
		}

		public Builder sort(SortBy sort) {
			this.sort = sort;
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

		public AggregatedFeedbacksRequestContext build() {
			return new AggregatedFeedbacksRequestContext(this);
		}
	}
}