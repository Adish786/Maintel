package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.globe.core.cache.hash.CollectionHasher;
import com.about.globe.core.cache.hash.EnumHasher;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.responses.SearchExPageResponse;
import com.about.mantle.model.services.SearchService;

import java.io.Serializable;
import java.util.Set;

public class CachedSearchService implements SearchService {

	private final SearchService searchService;
	private final CacheTemplate<SearchExPageResponse> searchCache;

	public CachedSearchService(SearchService searchService, CacheTemplate<SearchExPageResponse> searchCache) {
		this.searchService = searchService;
		this.searchCache = searchCache;
	}

	@Override
	public SearchExPageResponse getSearchResults(String term,
			Integer offset, Integer limit, AlgorithmType algorithmType, String projection, Set<String> tags) {
		CacheKey key = new CacheKey(term, false, offset, limit, algorithmType, projection, tags);

		return searchCache.get(key, () -> searchService.getSearchResults(term, offset, limit, algorithmType, projection, tags));
	}

	@Override
	public SearchExPageResponse getSearchResults(String term, Boolean allowFuzzy,
												 Integer offset, Integer limit, AlgorithmType algorithmType, String projection, Set<String> tags) {
		CacheKey key = new CacheKey(term, allowFuzzy, offset, limit, algorithmType, projection, tags);

		return searchCache.get(key, () -> searchService.getSearchResults(term, allowFuzzy, offset, limit, algorithmType, projection, tags));
	}

	private static class CacheKey implements Serializable, RedisCacheKey {

		private static final long serialVersionUID = 1L;

		private final String term;
		private final Boolean allowFuzzy;
		private final Integer offset;
		private final Integer limit;
		private final AlgorithmType algorithmType;
		private final String projection;
		private final Set<String> tags;

		public CacheKey(String term, Boolean allowFuzzy,
				Integer offset, Integer limit, AlgorithmType algorithmType, String projection, Set<String> tags) {
			this.term = term;
			this.allowFuzzy = allowFuzzy;
			this.offset = offset;
			this.limit = limit;
			this.algorithmType = algorithmType;
			this.projection = projection;
			this.tags = tags;
		}

		@Override
		public String getUniqueKey() {
			return RedisCacheKey.objectsToUniqueKey(null,term,allowFuzzy,offset,limit,projection,tags
				,algorithmType).toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((term == null) ? 0 : term.hashCode());
			result = prime * result + ((allowFuzzy == null) ? 0 : allowFuzzy.hashCode());
			result = prime * result + ((offset == null) ? 0 : offset.hashCode());
			result = prime * result + ((limit == null) ? 0 : limit.hashCode());
			result = prime * result + EnumHasher.INSTANCE.hash(algorithmType);
			result = prime * result + ((projection == null) ? 0 : projection.hashCode());
            result = prime * result + CollectionHasher.INSTANCE.hash(tags);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			CacheKey other = (CacheKey) obj;
			if (term == null) {
				if (other.term != null) return false;
			} else if (!term.equals(other.term)) return false;
			if (allowFuzzy == null) {
				if (other.allowFuzzy != null) return false;
			} else if (!allowFuzzy.equals(other.allowFuzzy)) return false;
			if (offset == null) {
				if (other.offset != null) return false;
			} else if (!offset.equals(other.offset)) return false;
			if (limit == null) {
				if (other.limit != null) return false;
			} else if (!limit.equals(other.limit)) return false;
			if (algorithmType == null) {
				if (other.algorithmType != null) return false;
			} else if (!algorithmType.equals(other.algorithmType)) return false;
			if (projection == null) {
				if (other.projection != null) return false;
			} else if (!projection.equals(other.projection)) return false;
			if (tags == null) {
				if (other.tags != null) return false;
			} else if (!tags.equals(other.tags)) return false;
			return true;
		}
	}
}
