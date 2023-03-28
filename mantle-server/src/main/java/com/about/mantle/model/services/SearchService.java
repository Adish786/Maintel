package com.about.mantle.model.services;

import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.about.mantle.model.extended.responses.SearchExPageResponse;

public interface SearchService {
	SearchExPageResponse getSearchResults(String term, Integer offset, Integer limit, AlgorithmType algorithmType,
			String projection, Set<String> tags);

	SearchExPageResponse getSearchResults(String term, Boolean allowFuzzy, Integer offset, Integer limit, AlgorithmType algorithmType,
										  String projection, Set<String> tags);
	
	public enum AlgorithmType {
		BUSINESS,
		CAREERS,
		CRAFTS,
		DEFAULT,
		ESPANOL,
		FAMILY,
		FASHION,
        FINANCE,
		FITNESS,
		FOOD,
		HEALTH,
		LIFESTYLE,
		MIND,
		MONEY,
		NONE,
		PETS,
		REFERENCE,
		TECH,
		TRAVEL,
		WHITE_LABEL,
		BEAUTY,
		DESIGN,
		RELIGION,
		WEDDINGS,
		LIQUOR,
		GREEN,
		SIMPLYRECIPES,
		SERIOUSEATS,
        CARBON,
	    PEOPLE,
        ENTERTAINMENTWEEKLY,
        PEOPLEENESPANOL,
        ALLRECIPES,
        MYRECIPES,
        EATINGWELL,
        FOODANDWINE,
        BHG,
        MARTHASTEWART,
        REALSIMPLE,
        SOUTHERNLIVING,
        INSTYLE,
        HELLOGIGGLES,
        MYWEDDING,
        MORE,
        SHAPE,
        HEALTHCOM,
        PARENTS,
        PARENTING,
        SERPADRES,
        TRAVELANDLEISURE,
        SIEMPREMUJER,
        AGRICULTURE,
        WOODMAGAZINE,
        ALLPEOPLEQUILT,
        COOKINGLIGHT,
        MIDWESTLIVING,
        DAILYPAWS
	}

	public static class SearchRequestContext {
		private static final int DEFAULT_OFFSET = 0;
		private static final int DEFAULT_LIMIT = 10;
		private static final int MAX_LIMIT = 100;

		private final String term;
		private final Boolean allowFuzzy;
		private final Integer offset;
		private final Integer limit;
		private final AlgorithmType algorithmType;
		private final Set<String> tags;
		private final String projection;

		private SearchRequestContext(Builder builder) {
			term = builder.term;
			allowFuzzy = builder.allowFuzzy;
			offset = builder.offset;
			limit = builder.limit;
			algorithmType = builder.algorithmType;
			tags = builder.tags;
			projection = builder.projection;
		}

		public String getTerm() {
			return term;
		}

		public Boolean getAllowFuzzy() {
			return allowFuzzy;
		}

		public Integer getOffset() {
			return offset;
		}

		public Integer getLimit() {
			return limit;
		}

		public AlgorithmType getAlgorithmType() {
			return algorithmType;
		}

		public Set<String> getTags() {
			return tags;
		}

		public String getProjection() {
			return projection;
		}

		@Override
		public int hashCode() {
			// @formatter:off
			return new HashCodeBuilder()
					.append(term)
					.append(allowFuzzy)
					.append(offset)
					.append(limit)
					.append(algorithmType)
					.append(tags)
					.append(projection)
					.build();
			// @formatter:on
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof SearchRequestContext)) return false;

			SearchRequestContext other = (SearchRequestContext) obj;

			// @formatter:off
			return new EqualsBuilder()
					.append(term, other.term)
					.append(allowFuzzy, other.allowFuzzy)
					.append(offset, other.offset)
					.append(limit,  other.limit)
					.append(algorithmType, other.algorithmType)
					.append(tags, other.tags)
					.append(projection, other.projection)
					.build();
			// @formatter:on
		}

		@Override
		public String toString() {
			// @formatter:off
			return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
					.append("term", term)
					.append("allowFuzzy", allowFuzzy)
					.append("offset", offset)
					.append("limit", limit)
					.append("algorithmType", algorithmType)
					.append("tags", tags)
					.append("projection", projection)
					.build();
			// @formatter:on
		}

		public static class Builder {
			private String term;
			private Boolean allowFuzzy;
			private Integer offset = DEFAULT_OFFSET;
			private Integer limit = DEFAULT_LIMIT;
			private AlgorithmType algorithmType;
			private Set<String> tags;
			private String projection = null;

			public Builder setTerm(String term) {
				if (StringUtils.isNotEmpty(term)) this.term = SearchUtils.cleanupTerms(term);
				return this;
			}

			public Builder setAllowFuzzy(Boolean allowFuzzy) {
				if (allowFuzzy != null) this.allowFuzzy = allowFuzzy;
				return this;
			}

			public Builder setOffset(Integer offset) {
				if (offset != null) this.offset = offset;
				return this;
			}

			public Builder setLimit(Integer limit) {
				if (limit != null) {
					if (limit > MAX_LIMIT) {
						this.limit = MAX_LIMIT;
					} else {
						this.limit = limit;
					}
				}
				return this;
			}

			public Builder setAlgorithmType(AlgorithmType algorithmType) {
				this.algorithmType = algorithmType;
				return this;
			}

			public Builder setTags(Set<String> tags) {
				this.tags = tags;
				return this;
			}

			public Builder setProjection(String projection) {
				this.projection = projection;
				return this;
			}

			public SearchRequestContext build() {
				return new SearchRequestContext(this);
			}
		}
	}

	public static class SearchUtils {

		private static final Pattern REPLACE_WITH_SPACE = Pattern.compile("(,|;|<|>| \\+ | \\- )");
		private static final Pattern COLLAPSE_SPACE = Pattern.compile("\\s\\s+");
		private static final Pattern TRIM_SPACE_AND_QUOTE = Pattern.compile("(^(\"|'|\\s)+|(\"|'|\\s)+$)");

		public static String cleanupTerms(String terms) {

			// replace some character with space
			String cterms = REPLACE_WITH_SPACE.matcher(terms).replaceAll(" ");

			//collapse spaces
			cterms = COLLAPSE_SPACE.matcher(cterms).replaceAll(" ");

			// trim the leading and trailing white spaces and quotes
			cterms = TRIM_SPACE_AND_QUOTE.matcher(cterms).replaceAll("");

			cterms = cterms.replace("&apos", "%27");

			return cterms;
		}
	}
}
