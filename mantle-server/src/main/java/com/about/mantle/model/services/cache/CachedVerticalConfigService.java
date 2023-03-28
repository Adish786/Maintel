package com.about.mantle.model.services.cache;

import java.util.Map;

import com.about.globe.core.cache.GenericCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.services.VerticalConfigService;

public class CachedVerticalConfigService implements VerticalConfigService {
	
	private final VerticalConfigService verticalConfigService;
	private final CacheTemplate<Map<String, ?>> verticalConfigCache;
	
	public CachedVerticalConfigService(VerticalConfigService verticalConfigService, CacheTemplate<Map<String, ?>> verticalConfigCache) {
		this.verticalConfigService = verticalConfigService;
		this.verticalConfigCache = verticalConfigCache;
	}

	@Override
	public Map<String, ?> getVerticalConfig(String vertical) {
		GenericCacheKey<String> key = new GenericCacheKey<String>(vertical);

		return verticalConfigCache.get(key, () -> {
			return verticalConfigService.getVerticalConfig(vertical);
		});
	}

}
