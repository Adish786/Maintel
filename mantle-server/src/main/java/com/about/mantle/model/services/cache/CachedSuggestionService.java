package com.about.mantle.model.services.cache;

import java.io.Serializable;

import com.about.globe.core.cache.RedisCacheKey;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.SuggestionSearchResultItemEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.SuggestionService;

public class CachedSuggestionService implements SuggestionService {

	private final SuggestionService suggestionService;
	private final CacheTemplate<SliceableListEx<SuggestionSearchResultItemEx>> searchCache;
	
	public CachedSuggestionService(SuggestionService suggestionService,
			CacheTemplate<SliceableListEx<SuggestionSearchResultItemEx>> searchCache) {
		this.suggestionService = suggestionService;
		this.searchCache = searchCache;
	}
	
	@Override
	public SliceableListEx<SuggestionSearchResultItemEx> search(SuggestionSearchRequestContext requestContext) {
		CacheKey key = new CacheKey(requestContext);

		return searchCache.get(key, () -> suggestionService.search(requestContext));
	}

	public static class CacheKey implements Serializable, RedisCacheKey {

		private static final long serialVersionUID = 1L;

		private final SuggestionSearchRequestContext requestContext;

		public CacheKey(SuggestionSearchRequestContext requestContext) {
			this.requestContext = requestContext;
		}

		@Override
		public String getUniqueKey() {
			if (requestContext != null) {
				return RedisCacheKey.objectsToUniqueKey(null,requestContext.getFilterQueries()
						,requestContext.getLimit(),requestContext.getOffset(),requestContext.getQuery()
						,requestContext.getSort()).toString();
			}
			return "null";
		}

		@Override
		public int hashCode() {
			// @formatter:off
			return new HashCodeBuilder()
					//TODO: Remove fields and just use requestContext when SuggestionSearchRequestContext overrides hashcode and equals
					.append(requestContext.getQuery())
					.append(requestContext.getFilterQueries())
					.append(requestContext.getLimit())
					.append(requestContext.getOffset())
					.append(requestContext.getSort())
					.build();
			// @formatter:on
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof CachedSuggestionService.CacheKey)) return false;

			CachedSuggestionService.CacheKey other = (CachedSuggestionService.CacheKey) obj;
			// @formatter:off
			return new EqualsBuilder()
					//TODO: Remove fields and just use requestContext when SuggestionSearchRequestContext overrides hashcode and equals
					.append(requestContext.getQuery(), other.requestContext.getQuery())
					.append(requestContext.getFilterQueries(), requestContext.getFilterQueries())
					.append(requestContext.getLimit(), other.requestContext.getLimit())
					.append(requestContext.getOffset(), other.requestContext.getOffset())
					.append(requestContext.getSort(), other.requestContext.getSort())
					.build();
			// @formatter:on
		}
	}

}
