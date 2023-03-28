package com.about.mantle.model.services.cache;

import java.io.Serializable;

import com.about.mantle.model.services.DeionSearchService;
import org.apache.commons.lang3.builder.EqualsBuilder;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.cache.hash.DeionSearchRequestContextHasher;
import com.about.mantle.model.extended.DeionSearchFullResultEx;
import com.about.mantle.model.services.DeionSearchFullDocumentService;

public class CachedDeionSearchFullDocumentService implements DeionSearchFullDocumentService {

	private final DeionSearchFullDocumentService deionSearchFullDocumentService;
	private final CacheTemplate<DeionSearchFullResultEx> searchCache;

	public CachedDeionSearchFullDocumentService(DeionSearchFullDocumentService deionSearchFullDocumentService,
			CacheTemplate<DeionSearchFullResultEx> searchCache) {
		this.deionSearchFullDocumentService = deionSearchFullDocumentService;
		this.searchCache = searchCache;
	}

	@Override
	public DeionSearchFullResultEx searchFullResults(DeionSearchService.DeionSearchRequestContext requestContext) {
		CacheKey key = new CacheKey(requestContext);
		return searchCache.get(key, () -> deionSearchFullDocumentService.searchFullResults(requestContext));
	}

	public static class CacheKey implements Serializable, RedisCacheKey {

		private static final long serialVersionUID = 1L;

		private final DeionSearchService.DeionSearchRequestContext requestContext;

		public CacheKey(DeionSearchService.DeionSearchRequestContext requestContext) {
			this.requestContext = requestContext;
		}

		@Override
		public String getUniqueKey() {
			if (requestContext != null) {
				StringBuilder builder = RedisCacheKey.objectsToUniqueKey(null,
						requestContext.getIncludeDocumentSummaries(), requestContext.getProjection());
				builder.append('|');
				if (requestContext.getDeionSearchKey() != null) {
					DeionSearchService.DeionSearchRequestContext.DeionSearchKey deionSearchKey = requestContext.getDeionSearchKey();
					RedisCacheKey.objectsToUniqueKey(builder, deionSearchKey.getBoost(), deionSearchKey.getDomain(),
							deionSearchKey.getFacetFields(), deionSearchKey.getFields(),
							deionSearchKey.getFilterQueries(), deionSearchKey.getSort(),
							deionSearchKey.getTraversalExcludedDocId(), deionSearchKey.getTraversalStartDocId(),
							deionSearchKey.getTraversalRelationships(), deionSearchKey.getSolrIndexType());

				}
				return builder.toString();
			}
			return "null";
		}

		@Override
		public int hashCode() {
			return DeionSearchRequestContextHasher.INSTANCE.hash(requestContext);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof CachedDeionSearchFullDocumentService.CacheKey))
				return false;

			CachedDeionSearchFullDocumentService.CacheKey other = (CachedDeionSearchFullDocumentService.CacheKey) obj;
			// @formatter:off
			return new EqualsBuilder().append(requestContext, other.requestContext).build();
			// @formatter:on
		}
	}

}
