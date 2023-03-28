package com.about.mantle.model.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.about.mantle.model.extended.DeionSearchResultEx;
import com.google.common.collect.ImmutableList;

public interface DeionSearchService {

	/**
	 * Technical limit is 51k but experience has shown that setting this too high results in
	 * frequent socket timeouts. Note that this limit is suggested and cannot be enforced by the
	 * service because pre-existing clients may have expectations that the limit they specify
	 * is honored and perform arithmethic calculations based on those expectations.
	 */
	public static int SUGGESTED_MAX_LIMIT = 20000;

	DeionSearchResultEx search(DeionSearchRequestContext requestContext);

	public static class DeionSearchRequestContext {
		private static final int DEFAULT_OFFSET = 0;
		private static final int DEFAULT_LIMIT = 10;

		// NOTE : this limit is so high in order to support the last 50000 updated search.
		// If we eventually move that search into a top level service we should reduce this limit to 100
		private static final int MAX_LIMIT = 51000;

		private static final int DEFAULT_FACET_LIMIT = 500;
		private static final int MAX_FACET_LIMIT = 2000;

		private static final List<String> DFT_RESULT_FIELDS = ImmutableList.<String> of("url", "docId", "title",
				"description", "state", "activeDate");

		private final boolean includeDocumentSummaries;
		private final String projection;
		private final DeionSearchKey deionSearchKey;

		private DeionSearchRequestContext(Builder builder) {
			deionSearchKey = new DeionSearchKey(builder);

			includeDocumentSummaries = (builder.includeDocumentSummaries == null) ? false
					: builder.includeDocumentSummaries;
			projection = builder.projection;
		}

		public DeionSearchKey getDeionSearchKey() {
			return deionSearchKey;
		}

		public Boolean getIncludeDocumentSummaries() {
			return includeDocumentSummaries;
		}

		public String getProjection() {
			return projection;
		}
		
		public static String getDefaultTemplateTypesFilterOutQuery() {
			return "-templateType:(REDIRECT OR LEGACY OR CATEGORY OR LANDINGPAGE OR USERPATH OR IMAGEGALLERY OR VIDEO OR JWPLAYERVIDEO OR BRIGHTCOVEVIDEO OR PROGRAMMEDSUMMARY OR AMAZONOSP)";
		}

		@Override
		public int hashCode() {
			// @formatter:off
			return new HashCodeBuilder()
					.append(deionSearchKey)
					.append(includeDocumentSummaries)
					.append(projection)
					.build();
			// @formatter:on
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof DeionSearchRequestContext)) {
				return false;
			}

			DeionSearchRequestContext other = (DeionSearchRequestContext) obj;
			// @formatter:off
			return new EqualsBuilder()
					.append(deionSearchKey, other.deionSearchKey)
					.append(includeDocumentSummaries, other.includeDocumentSummaries)
					.append(projection, other.projection)
					.build();
			// @formatter:on
		}

		public enum SolrIndexType {
			SEARCH,
			CMS
		}

		public static class DeionSearchKey {
			// When set to true, the search result will not be cached. Not include in hashcode/equals
			private final boolean noCache;

			private final String query;
			private final String boost;
			private final List<String> filterQueries;
			private final List<String> fields;
			private final List<String> facetFields;
			
			private final List<FacetRangeQuery> facetRangeQueryList;
			
			private final String sort;
			private final String domain;

			private final List<Long> traversalStartDocId;
			private final List<Long> traversalExcludedDocId;
			private final List<String> traversalRelationships;

			// when facet = false, offset and limit apply to search result, otherwise it apply to facet result
			private final Integer offset;
			private final Integer limit;

			// the solr index to which the query will be sent
			private SolrIndexType solrIndexType;

			private DeionSearchKey(Builder builder) {
				query = builder.query;
				boost = builder.boost;

				if (builder.filterQueries == null) {
					filterQueries = null;
				} else {
					filterQueries = builder.filterQueries.stream().filter(s -> s != null && s.length() > 0)
							.collect(Collectors.toList());
				}

				if (CollectionUtils.isEmpty(builder.fields)) {
					fields = DFT_RESULT_FIELDS;
				} else {
					fields = builder.fields.stream().filter(s -> s != null && s.length() > 0).collect(Collectors.toList());
				}

				if (builder.facetFields == null) {
					facetFields = null;
				} else {
					facetFields = builder.facetFields.stream().filter(s -> s != null && s.length() > 0)
							.collect(Collectors.toList());
				}
				
				if (builder.facetRangeQueryList != null) {
					facetRangeQueryList = new ArrayList<>(builder.facetRangeQueryList);
				} else {
					facetRangeQueryList = null;
				}

				noCache = (builder.noCache == null) ? false : builder.noCache;
				sort = builder.sort;
				solrIndexType = builder.solrIndexType;

				traversalStartDocId = builder.traversalStartDocId;
				traversalExcludedDocId = builder.traversalExcludedDocId;
				traversalRelationships = builder.traversalRelationships;

				domain = builder.domain;

				if (builder.offset == null) {
					offset = DEFAULT_OFFSET;
				} else {
					offset = builder.offset;
				}

				if (CollectionUtils.isNotEmpty(facetFields)) {
					if (builder.limit == null) {
						limit = DEFAULT_FACET_LIMIT;
					} else if (builder.limit > MAX_FACET_LIMIT) {
						limit = MAX_FACET_LIMIT;
					} else {
						limit = builder.limit;
					}
				} else {
					if (builder.limit == null) {
						limit = DEFAULT_LIMIT;
					} else if (builder.limit > MAX_LIMIT) {
						limit = MAX_LIMIT;
					} else {
						limit = builder.limit;
					}
				}
			}

			public boolean isNoCache() {
				return noCache;
			}

			public String getQuery() {
				return query;
			}

			public String getBoost() {
				return boost;
			}

			public String getDomain() {
				return domain;
			}

			public List<String> getFilterQueries() {
				return filterQueries;
			}

			public List<String> getFields() {
				return fields;
			}

			public List<String> getFacetFields() {
				return facetFields;
			}

			public Integer getOffset() {
				return offset;
			}

			public Integer getLimit() {
				return limit;
			}

			public String getSort() {
				return sort;
			}

			public SolrIndexType getSolrIndexType() {
				return this.solrIndexType;
			}

			public List<Long> getTraversalStartDocId() {
				return traversalStartDocId;
			}

			public List<Long> getTraversalExcludedDocId() {
				return traversalExcludedDocId;
			}

			public List<String> getTraversalRelationships() {
				return traversalRelationships;
			}
			
			public List<FacetRangeQuery> getFacetRangeQueryList() {
				return facetRangeQueryList;
			}

			@Override
			public int hashCode() {
				// @formatter:off
				return new HashCodeBuilder()
						.append(query)
						.append(boost)
						.append(domain)
						.append(filterQueries)
						.append(fields)
						.append(facetFields)
						.append(facetRangeQueryList)
						.append(offset)
						.append(limit)
						.append(sort)
						.append(solrIndexType)
						.append(traversalStartDocId)
						.append(traversalExcludedDocId)
						.append(traversalRelationships)
						.build();
				// @formatter:on
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) return true;
				if (!(obj instanceof DeionSearchKey)) return false;

				DeionSearchKey other = (DeionSearchKey) obj;
				// @formatter:off
				return new EqualsBuilder()
						.append(query, other.query)
						.append(boost, other.boost)
						.append(domain, other.domain)
						.append(filterQueries, other.filterQueries)
						.append(fields, other.fields)
						.append(facetFields, other.facetFields)
						.append(facetRangeQueryList, other.facetFields)
						.append(offset, other.offset)
						.append(limit, other.limit)
						.append(sort, other.sort)
						.append(traversalStartDocId, other.traversalStartDocId)
						.append(traversalExcludedDocId, other.traversalExcludedDocId)
						.append(traversalRelationships, other.traversalRelationships)
						.append(solrIndexType, other.solrIndexType).build();
				// @formatter:on
			}

			@Override
			public String toString() {
				// @formatter:off
				return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
						.append("noCache", noCache)
						.append("query", query)
						.append("boost", boost)
						.append("domain", domain)
						.append("filterQueries", filterQueries)
						.append("fields", fields)
						.append("facetRangeQueryList",facetRangeQueryList)
						.append("facetFields", facetFields)
						.append("offset", offset)
						.append("limit", limit)
						.append("sort", sort)
						.append("traversalStartDocId", traversalStartDocId)
						.append("traversalExcludedDocId", traversalExcludedDocId)
						.append("traversalRelationships", traversalRelationships)
						.append("solrIndexType", solrIndexType)
						.build();
				// @formatter:on
			}
		}

		public static class Builder {
			private String query;
			private String boost;
			private List<String> filterQueries;
			private List<String> fields;
			private List<String> facetFields;
			
			private List<FacetRangeQuery> facetRangeQueryList;
			private String sort;
			private String domain;
			private Boolean includeDocumentSummaries;
			private String projection;

			private Integer offset;
			private Integer limit;

			private SolrIndexType solrIndexType;
			private Boolean noCache;

			private List<Long> traversalStartDocId;
			private List<Long> traversalExcludedDocId;
			private List<String> traversalRelationships;

			public Builder setQuery(String query) {
				this.query = query.replace("&apos", "%27");
				return this;
			}

			public Builder setBoost(String boost) {
				this.boost = boost;
				return this;
			}

			public Builder setDomain(String domain) {
				this.domain = domain;
				return this;
			}

			public Builder setFilterQueries(List<String> filterQueries) {
				this.filterQueries = filterQueries;
				return this;
			}

			public Builder setFields(List<String> fields) {
				this.fields = fields;
				return this;
			}

			public Builder setFacetFields(List<String> facetFields) {
				this.facetFields = facetFields;
				return this;
			}

			public Builder setOffset(Integer offset) {
				this.offset = offset;
				return this;
			}

			public Builder setLimit(Integer limit) {
				this.limit = limit;
				return this;
			}

			public Builder setNoCache(Boolean noCache) {
				this.noCache = noCache;
				return this;
			}

			public Builder setSort(String sort) {
				this.sort = sort;
				return this;
			}

			public Builder setSolrIndexType(SolrIndexType solrIndexType) {
				this.solrIndexType = solrIndexType;
				return this;
			}

			public Builder setIncludeDocumentSummaries(Boolean includeDocumentSummaries) {
				this.includeDocumentSummaries = includeDocumentSummaries;
				return this;
			}

			public Builder setProjection(String projection) {
				this.projection = projection;
				return this;
			}

			public Builder setTraversalStartDocId(List<Long> docId) {
				this.traversalStartDocId = docId;
				return this;
			}

			public Builder setTraversalExcludedDocId(List<Long> docId) {
				this.traversalExcludedDocId = docId;
				return this;
			}

			public Builder setTraversalRelationships(List<String> relationships) {
				this.traversalRelationships = relationships;
				return this;
			}
			
			public Builder setFacetRange(List<FacetRangeQuery> facetRanges) {
				this.facetRangeQueryList = facetRanges;
				return this;
			}

			public DeionSearchRequestContext build() {
				return new DeionSearchRequestContext(this);
			}
		}
		
		public static class FacetRangeQuery {
			private String field;
			private String gap;
			private String start;
			private String end;

			public String getField() {
				return field;
			}

			public void setField(String field) {
				this.field = field;
			}

			public String getGap() {
				return gap;
			}

			public void setGap(String gap) {
				this.gap = gap;
			}

			public String getStart() {
				return start;
			}

			public void setStart(String start) {
				this.start = start;
			}

			public String getEnd() {
				return end;
			}

			public void setEnd(String end) {
				this.end = end;
			}

			@Override
			public boolean equals(Object o) {
				if (this == o)
					return true;

				if (o == null || getClass() != o.getClass())
					return false;

				FacetRangeQuery that = (FacetRangeQuery) o;

				return new EqualsBuilder()
						.append(field, that.field)
						.append(gap, that.gap)
						.append(start, that.start)
						.append(end, that.end)
						.isEquals();
			}

			@Override
			public int hashCode() {
				return new HashCodeBuilder(17, 37)
						.append(field)
						.append(gap)
						.append(start)
						.append(end)
						.toHashCode();
			}

			@Override
			public String toString() {
				return "FacetRangeQuery{" +
						"field='" + field + '\'' +
						", gap='" + gap + '\'' +
						", start='" + start + '\'' +
						", end='" + end + '\'' +
						'}';
			}
		}
	}
}
