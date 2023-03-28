package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.StaticCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.services.AuctionFloorMappingConfigService;
import com.about.mantle.model.extended.AuctionFloorConfig;

public class CachedAuctionFloorMappingConfigService implements AuctionFloorMappingConfigService {

	private static final StaticCacheKey auctionFloorConfigKey = new StaticCacheKey("AuctionFloorConfig");

	private final AuctionFloorMappingConfigService auctionFloorMappingConfigService;
	private final CacheTemplate<AuctionFloorConfig> auctionFloorConfigCache;

	public CachedAuctionFloorMappingConfigService (AuctionFloorMappingConfigService auctionService, CacheTemplate<AuctionFloorConfig> auctionFloorConfigCache) {
		this.auctionFloorMappingConfigService = auctionService;
		this.auctionFloorConfigCache = auctionFloorConfigCache;
	}

	@Override
	public AuctionFloorConfig getAuctionFloorConfig() {
		return auctionFloorConfigCache.get(auctionFloorConfigKey, () -> auctionFloorMappingConfigService.getAuctionFloorConfig());
	}

}
