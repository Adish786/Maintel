package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.StaticCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.services.PrebidConfigurationService;
import com.about.mantle.model.services.prebid.PrebidConfiguration;

public class CachedPrebidConfigurationService implements PrebidConfigurationService {

	/**
	 * This data is not actually keyed on anything but we have to use something so we use a static cache key.
	 * Why cache at all?
	 * 1. Performance: we don't want to incur the cost of parsing the config for every request.
	 * 2. Safety: if loading of the config fails for any reason it will continue to use whatever is already in redis.
	 */
	private static final StaticCacheKey key = new StaticCacheKey("PrebidConfigurationKey");

	private final PrebidConfigurationService service;
	private final CacheTemplate<PrebidConfiguration> cacheTemplate;

	public CachedPrebidConfigurationService(PrebidConfigurationService service, CacheTemplate<PrebidConfiguration> cacheTemplate) {
		this.service = service;
		this.cacheTemplate = cacheTemplate;
	}

	@Override
	public PrebidConfiguration getConfiguration() {
		return this.cacheTemplate.get(key, () -> this.service.getConfiguration());
	}

}