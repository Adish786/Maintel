package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.services.CuratedDomainService;

import java.io.Serializable;
import java.util.Set;

public class CachedCuratedDomainService implements CuratedDomainService {

	private CuratedDomainService service;
	private CacheTemplate<Set<String>> curatedDomainCache;

	public CachedCuratedDomainService(CuratedDomainService service, CacheTemplate<Set<String>> curatedDomainCache) {
		this.service = service;
		this.curatedDomainCache = curatedDomainCache;
	}

	@Override
	public Set<String> getDomainsBySource(String type, String subType) {
		CacheKey key = new CacheKey(type, subType);

		return curatedDomainCache.get(key, () -> service.getDomainsBySource(type, subType));
	}

	private static class CacheKey implements Serializable, RedisCacheKey {

		private static final long serialVersionUID = 1L;

		private final String type;
		private final String subType;

		public CacheKey(String type, String subType) {
			this.type = type;
			this.subType = subType;
		}

		@Override
		public String getUniqueKey() {

			String key = RedisCacheKey.objectsToUniqueKey(null, type, subType).toString();
			return key;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result + ((subType == null) ? 0 : subType.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			CacheKey other = (CacheKey) obj;
			if (type == null) {
				if (other.type != null) return false;
			} else if (!type.equals(other.type)) return false;
			if (subType == null) {
				if (other.subType != null) return false;
			} else if (!subType.equals(other.subType)) return false;
			return true;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("CacheKey{");
			sb.append("type=").append(type);
			sb.append(", subType=").append(subType);
			sb.append('}');
			return sb.toString();
		}
	}
}
