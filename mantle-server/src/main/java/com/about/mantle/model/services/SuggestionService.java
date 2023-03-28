package com.about.mantle.model.services;

import java.util.List;

import com.about.mantle.model.extended.SuggestionSearchResultItemEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;

public interface SuggestionService {

	SliceableListEx<SuggestionSearchResultItemEx> search(SuggestionSearchRequestContext requestContext);
	
	public static class SuggestionSearchRequestContext {
		private final String query;
		private final List<String> filterQueries;
		private final String sort;
		private final Integer offset;
		private final Integer limit;

		private SuggestionSearchRequestContext(Builder builder) {
			this.query = builder.query;
			this.filterQueries = builder.filterQueries;
			this.sort = builder.sort;
			this.offset = builder.offset;
			this.limit = builder.limit;
		}

		public String getQuery() {
			return query;
		}

		public List<String> getFilterQueries() {
			return filterQueries;
		}

		public String getSort() {
			return sort;
		}

		public Integer getLimit() {
			return limit;
		}

		public Integer getOffset() {
			return offset;
		}

		public static class Builder {
			private String query = null;
			private List<String> filterQueries = null;
			private String sort = null;
			private Integer offset = null;
			private Integer limit = null;

			public SuggestionSearchRequestContext build() {
				return new SuggestionSearchRequestContext(this);
			}

			public Builder setQuery(String query) {
				this.query = query;
				return this;
			}

			public Builder setFilterQueries(List<String> filterQueries) {
				this.filterQueries = filterQueries;
				return this;
			}

			public Builder setSort(String sort) {
				this.sort = sort;
				return this;
			}

			public Builder setLimit(Integer limit) {
				this.limit = limit;
				return this;
			}

			public Builder setOffset(Integer offset) {
				this.offset = offset;
				return this;
			}
		}
	}
}
