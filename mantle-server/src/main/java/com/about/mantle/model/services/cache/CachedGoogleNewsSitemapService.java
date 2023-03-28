package com.about.mantle.model.services.cache;

import java.util.List;

import com.about.globe.core.cache.GenericCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.news.GoogleNewsSitemapService;

public class CachedGoogleNewsSitemapService implements GoogleNewsSitemapService {

	private final GoogleNewsSitemapService service;
	private final CacheTemplate<List<BaseDocumentEx>> googleNewsSitemapCache;

	public CachedGoogleNewsSitemapService(GoogleNewsSitemapService service, CacheTemplate<List<BaseDocumentEx>> googleNewsSitemapCache) {
		this.service = service;
		this.googleNewsSitemapCache = googleNewsSitemapCache;
	}

	@Override
	public List<BaseDocumentEx> getGoogleNewsDocuments(String domain) {
		GenericCacheKey<String> key = new GenericCacheKey<>(domain);
		return googleNewsSitemapCache.get(key, () -> service.getGoogleNewsDocuments(domain));
	}

}
