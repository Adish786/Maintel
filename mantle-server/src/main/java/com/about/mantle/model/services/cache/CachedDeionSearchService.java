package com.about.mantle.model.services.cache;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.cache.hash.DeionSearchRequestContextHasher;
import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.services.DeionSearchService;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext.DeionSearchKey;

public class CachedDeionSearchService implements DeionSearchService {

	private final DeionSearchService deionSearchService;
	private final CacheTemplate<DeionSearchResultEx> searchCache;

	public CachedDeionSearchService(DeionSearchService deionSearchService,
			CacheTemplate<DeionSearchResultEx> searchCache) {
		this.deionSearchService = deionSearchService;
		this.searchCache = searchCache;
	}

	@Override
	public DeionSearchResultEx search(DeionSearchRequestContext requestContext) {
		CacheKey key = new CacheKey(requestContext);

		return searchCache.get(key, () -> deionSearchService.search(requestContext));
	}

	private static class CacheKey implements Serializable, RedisCacheKey {

		private static final long serialVersionUID = 1L;

		private final DeionSearchRequestContext requestContext;

		public CacheKey(DeionSearchRequestContext requestContext) {
			this.requestContext = requestContext;
		}

		@Override
		public String getUniqueKey() {
			if (requestContext != null){
				StringBuilder builder = RedisCacheKey.objectsToUniqueKey(null, requestContext.getIncludeDocumentSummaries()
					,requestContext.getProjection());
				builder.append('|');
				if (requestContext.getDeionSearchKey() != null){
					DeionSearchKey deionSearchKey = requestContext.getDeionSearchKey();
					RedisCacheKey.objectsToUniqueKey(builder,deionSearchKey.getQuery(),deionSearchKey.getBoost()
							,deionSearchKey.getDomain(),deionSearchKey.getFacetFields(),deionSearchKey.getFields()
							,deionSearchKey.getFilterQueries(),deionSearchKey.getSort(),deionSearchKey.getOffset()
							,deionSearchKey.getLimit(),deionSearchKey.getTraversalExcludedDocId()
							,deionSearchKey.getTraversalStartDocId(),deionSearchKey.getTraversalRelationships()
							,deionSearchKey.getSolrIndexType(),deionSearchKey.isNoCache());

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
			if (!(obj instanceof CachedDeionSearchService.CacheKey))
				return false;

			CachedDeionSearchService.CacheKey other = (CachedDeionSearchService.CacheKey) obj;
			// @formatter:off
			return new EqualsBuilder()
					.append(requestContext,other.requestContext)
					.build();
			// @formatter:on
		}
	}
}
