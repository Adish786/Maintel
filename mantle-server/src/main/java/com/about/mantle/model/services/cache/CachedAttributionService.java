package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.GenericCacheKey;
import com.about.globe.core.cache.StaticCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.attribution.Attribution;
import com.about.mantle.model.extended.attribution.AttributionType;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.AttributionService;

import java.util.Map;

public class CachedAttributionService implements AttributionService {

	private final AttributionService attributionService;
	private final CacheTemplate<Attribution> attributionByIdCache;
	private final CacheTemplate<Map<String, AttributionType>> attributionTypesCache;
	private final CacheTemplate<SliceableListEx<Attribution>> attributionListByAuthorIdCache;
	private static final StaticCacheKey attributionTypesKey = new StaticCacheKey("AttributionTypeKey");

	public CachedAttributionService(AttributionService attributionService, CacheTemplate<Attribution> attributionByIdCache,
									CacheTemplate<Map<String, AttributionType>> attributionTypesCache,
									CacheTemplate<SliceableListEx<Attribution>> attributionListByAuthorIdCache) {
		this.attributionService = attributionService;
		this.attributionByIdCache = attributionByIdCache;
		this.attributionTypesCache = attributionTypesCache;
		this.attributionListByAuthorIdCache = attributionListByAuthorIdCache;
	}

	@Override
	public Attribution getById(String id) {
		GenericCacheKey<String> key = new GenericCacheKey<>(id);

		return attributionByIdCache.get(key, () -> attributionService.getById(id));
	}

	@Override
	public Map<String, AttributionType> getAttributionTypes() {
		return attributionTypesCache.get(attributionTypesKey, () -> attributionService.getAttributionTypes());
	}

	@Override
	public SliceableListEx<Attribution> getAttributionsByAuthorId(String id) {
		GenericCacheKey<String> key = new GenericCacheKey<>(id);

		return attributionListByAuthorIdCache.get(key, () -> attributionService.getAttributionsByAuthorId(id));
	}

}
