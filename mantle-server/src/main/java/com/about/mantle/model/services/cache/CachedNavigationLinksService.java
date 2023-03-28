package com.about.mantle.model.services.cache;

import java.io.Serializable;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.PageRequest;
import com.about.mantle.model.extended.docv2.CategoryLinkEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.NavigationLinkService;

public class CachedNavigationLinksService implements NavigationLinkService {

	private final NavigationLinkService navigationLinkService;
	private final CacheTemplate<SliceableListEx<CategoryLinkEx>> navigationLinksCache;

	public CachedNavigationLinksService(NavigationLinkService navigationLinkService,
			CacheTemplate<SliceableListEx<CategoryLinkEx>> navigationLinksCache) {
		this.navigationLinkService = navigationLinkService;
		this.navigationLinksCache = navigationLinksCache;
	}

	@Override
	public SliceableListEx<CategoryLinkEx> getNavigationLinks(String url, PageRequest pageRequest) {
		CacheKey key = new CacheKey(url, pageRequest);

		return navigationLinksCache.get(key, () -> navigationLinkService.getNavigationLinks(url, pageRequest));
	}

	private static class CacheKey implements Serializable, RedisCacheKey {

		private static final long serialVersionUID = 1L;

		private final String url;
		private final PageRequest pageRequest;

		public CacheKey(String url, PageRequest pageRequest) {
			this.url = url;
			this.pageRequest = pageRequest;
		}

		@Override
		public String getUniqueKey() {
			StringBuilder builder = new StringBuilder();
			builder.append(String.valueOf(url)).append('|');
			if (pageRequest != null){
				builder = RedisCacheKey.objectsToUniqueKey(builder,pageRequest.getOffset(),pageRequest.getLimit());
			}
			return builder.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			result = prime * result + ((pageRequest == null) ? 0 : pageRequest.hashCode());
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
			if (pageRequest == null) {
				if (other.pageRequest != null)
					return false;
			} else if (!pageRequest.equals(other.pageRequest))
				return false;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			return true;
		}
	}
}
