package com.about.mantle.model.services.feeds.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.services.feeds.api.RssFeedService;
import com.about.mantle.model.services.feeds.response.RssFeedSearchResponse;

public class CachedRssFeedService implements RssFeedService {

	private final RssFeedService delegate;
	private final CacheTemplate<RssFeedSearchResponse> cache;

	public CachedRssFeedService(RssFeedService delegate, CacheTemplate<RssFeedSearchResponse> cache) {
		this.delegate = delegate;
		this.cache = cache;
	}

	public RssFeedSearchResponse search(String query, List<String> filterQueries, Boolean mapExchanges, Integer limit,
			Integer offset, Boolean includeFullDocument, String sort, Map<String, Object> additionalQueryParams) {
		CacheKey key = new CacheKey(query, filterQueries, mapExchanges, limit, offset, includeFullDocument, sort,
				additionalQueryParams);
		return cache.get(key, () -> delegate.search(query, filterQueries, mapExchanges, limit, offset,
				includeFullDocument, sort, additionalQueryParams));
	}

	private static class CacheKey implements Serializable, RedisCacheKey {

		private static final long serialVersionUID = 1L;

		private final String query;
		private final List<String> filterQueries;
		private final Boolean mapExchanges;
		private final Integer limit;
		private final Integer offset;
		private final Boolean includeFullDocument;
		private final String sort;
		private final Map<String, Object> additionalQueryParams;

		public CacheKey(String query, List<String> filterQueries, Boolean mapExchanges, Integer limit, Integer offset,
				Boolean includeFullDocument, String sort, Map<String, Object> additionalQueryParams) {
			this.query = query;
			this.filterQueries = filterQueries;
			this.mapExchanges = mapExchanges;
			this.limit = limit;
			this.offset = offset;
			this.includeFullDocument = includeFullDocument;
			this.sort = sort;
			this.additionalQueryParams = additionalQueryParams;
		}

		@Override
		public String getUniqueKey() {

			String key = RedisCacheKey.objectsToUniqueKey(null, query, filterQueries, mapExchanges, limit, offset,
					includeFullDocument, sort, additionalQueryParams).toString();
			return key;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((additionalQueryParams == null) ? 0 : additionalQueryParams.hashCode());
			result = prime * result + ((filterQueries == null) ? 0 : filterQueries.hashCode());
			result = prime * result + ((includeFullDocument == null) ? 0 : includeFullDocument.hashCode());
			result = prime * result + ((limit == null) ? 0 : limit.hashCode());
			result = prime * result + ((mapExchanges == null) ? 0 : mapExchanges.hashCode());
			result = prime * result + ((offset == null) ? 0 : offset.hashCode());
			result = prime * result + ((query == null) ? 0 : query.hashCode());
			result = prime * result + ((sort == null) ? 0 : sort.hashCode());
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
			CacheKey other = (CacheKey) obj;
			if (additionalQueryParams == null) {
				if (other.additionalQueryParams != null)
					return false;
			} else if (!additionalQueryParams.equals(other.additionalQueryParams))
				return false;
			if (filterQueries == null) {
				if (other.filterQueries != null)
					return false;
			} else if (!filterQueries.equals(other.filterQueries))
				return false;
			if (includeFullDocument == null) {
				if (other.includeFullDocument != null)
					return false;
			} else if (!includeFullDocument.equals(other.includeFullDocument))
				return false;
			if (limit == null) {
				if (other.limit != null)
					return false;
			} else if (!limit.equals(other.limit))
				return false;
			if (mapExchanges == null) {
				if (other.mapExchanges != null)
					return false;
			} else if (!mapExchanges.equals(other.mapExchanges))
				return false;
			if (offset == null) {
				if (other.offset != null)
					return false;
			} else if (!offset.equals(other.offset))
				return false;
			if (query == null) {
				if (other.query != null)
					return false;
			} else if (!query.equals(other.query))
				return false;
			if (sort == null) {
				if (other.sort != null)
					return false;
			} else if (!sort.equals(other.sort))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "CacheKey [query=" + query + ", filterQueries=" + filterQueries + ", mapExchanges=" + mapExchanges
					+ ", limit=" + limit + ", offset=" + offset + ", includeFullDocument=" + includeFullDocument
					+ ", sort=" + sort + ", additionalQueryParams=" + additionalQueryParams + "]";
		}

	}
}