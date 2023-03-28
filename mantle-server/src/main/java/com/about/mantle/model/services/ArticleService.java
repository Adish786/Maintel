package com.about.mantle.model.services;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.about.mantle.model.PageRequest;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;

/**
 * Provides collections of articles based on some criteria, e.g. those related to a particular article.
 */
public interface ArticleService {

	SliceableListEx<BaseDocumentEx> getRelated(RelatedArticleRequestContext ctx, PageRequest pageRequest,
			ArticleFilterRequest articleFilterRequest);

	SliceableListEx<BaseDocumentEx> getRelated(String url, PageRequest pageRequest,
			ArticleFilterRequest articleFilterRequest);

	public static class RelatedArticleRequestContext {

		private final String url;
		private final Long docId;
		private final String algorithm;

		private RelatedArticleRequestContext(Builder builder) {
			this.url = builder.url;
			this.docId = builder.docId;
			this.algorithm = builder.algorithm;
		}

		public String getUrl() {
			return url;
		}

		public Long getDocId() {
			return docId;
		}

		public String getAlgorithm() {
			return algorithm;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
			result = prime * result + ((docId == null) ? 0 : docId.hashCode());
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RelatedArticleRequestContext other = (RelatedArticleRequestContext) obj;
			if (algorithm == null) {
				if (other.algorithm != null)
					return false;
			} else if (!algorithm.equals(other.algorithm))
				return false;
			if (docId == null) {
				if (other.docId != null)
					return false;
			} else if (!docId.equals(other.docId))
				return false;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			return true;
		}

		public static class Builder {
			private String url;
			private Long docId;
			private String algorithm;

			public Builder setUrl(String url) {
				this.url = url;
				return this;
			}

			public Builder setDocId(Long docId) {
				this.docId = docId;
				return this;
			}

			public Builder setAlgorithm(String algorithm) {
				this.algorithm = algorithm;
				return this;
			}

			public RelatedArticleRequestContext build() {
				if (docId != null && StringUtils.isNotEmpty(url))
					throw new IllegalArgumentException("Both url and docId are set. They are mutually exclusive.");

				return new RelatedArticleRequestContext(this);
			}
		}
	}

	public static class ArticleFilterRequest {

		private Set<TemplateTypeEx> templateTypes;
		private Set<String> excludeKeys;
		private Set<Long> excludeDocIds;

		private ArticleFilterRequest(Builder builder) {
			templateTypes = builder.templateTypes;
			excludeKeys = builder.excludeKeys;
			excludeDocIds = builder.excludeDocIds;
		}

		public Set<TemplateTypeEx> getTemplateTypes() {
			return templateTypes;
		}

		public Set<String> getExcludeKeys() {
			return excludeKeys;
		}

		public Set<Long> getExcludeDocIds() {
			return excludeDocIds;
		}

		public static class Builder {

			private Set<TemplateTypeEx> templateTypes;
			private Set<String> excludeKeys;
			private Set<Long> excludeDocIds;

			public Builder setTemplateTypes(Set<TemplateTypeEx> templateTypes) {
				templateTypes.removeAll(Collections.singleton(null));
				this.templateTypes = templateTypes;
				return this;
			}

			public Builder setExcludeKeys(Set<String> excludeKeys) {
				excludeKeys.removeAll(Collections.singleton(null));
				this.excludeKeys = excludeKeys;
				return this;
			}

			public Builder setExcludeDocIds(Set<Long> excludeDocIds) {
				excludeDocIds.removeAll(Collections.singleton(null));
				this.excludeDocIds = excludeDocIds;
				return this;
			}

			public ArticleFilterRequest build() {
				return new ArticleFilterRequest(this);
			}

		}

		@Override
		public int hashCode() {
			// @formatter:off
			return new HashCodeBuilder()
					.append(templateTypes)
					.append(excludeKeys)
					.append(excludeDocIds)
					.build();
			// @formatter:on
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof ArticleFilterRequest)) return false;

			ArticleFilterRequest other = (ArticleFilterRequest) obj;
			// @formatter:off
			return new EqualsBuilder()
					.append(templateTypes, other.templateTypes)
					.append(excludeKeys, other.excludeKeys)
					.append(excludeDocIds, other.excludeDocIds)
					.build();
			// @formatter:on
		}

	}

}
