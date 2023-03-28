package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.GenericCacheKey;
import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.AuctionFloorConfig;
import com.about.mantle.model.extended.AuctionFloorInfo;
import com.about.mantle.model.extended.AuctionFloorInfoListItem;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.AuctionFloorMappingConfigService;
import com.about.mantle.model.services.AuctionFloorService;

import java.io.Serializable;

public class CachedAuctionFloorService implements AuctionFloorService {

	private final AuctionFloorService auctionFloorService;
	private final CacheTemplate<AuctionFloorInfo> auctionFloorInfoCache;

	private final CacheTemplate<SliceableListEx<AuctionFloorInfoListItem>> auctionFloorListInfoCache;

	public CachedAuctionFloorService(AuctionFloorService auctionService, CacheTemplate<AuctionFloorInfo> auctionFloorInfoCache, CacheTemplate<SliceableListEx<AuctionFloorInfoListItem>> auctionFloorListInfoCache) {
		this.auctionFloorService = auctionService;
		this.auctionFloorInfoCache = auctionFloorInfoCache;
		this.auctionFloorListInfoCache = auctionFloorListInfoCache;
	}

	@Override
	public AuctionFloorInfo getAuctionFloorInfo(AuctionFloorRequestContext ctx) {
		CacheKey key = new CacheKey(ctx);
		return auctionFloorInfoCache.get(key, () -> auctionFloorService.getAuctionFloorInfo(ctx));
	}

	@Override
	public SliceableListEx<AuctionFloorInfoListItem> getAuctionFloorInfoList(AuctionFloorListRequestContext ctx, AuctionFloorConfig config) {
		GenericCacheKey<AuctionFloorListRequestContext> key = new GenericCacheKey<>(ctx);
		return auctionFloorListInfoCache.get(key, () -> auctionFloorService.getAuctionFloorInfoList(ctx, config));
	}

	private static class CacheKey implements Serializable, RedisCacheKey {

		private static final long serialVersionUID = 1L;
		private final AuctionFloorRequestContext ctx;

		public CacheKey(AuctionFloorRequestContext ctx) {
			this.ctx = ctx;
		}

		@Override
		public String getUniqueKey(){
			return this.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((ctx == null) ? 0 : ctx.hashCode());
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
			if (ctx == null) {
				if (other.ctx != null)
					return false;
			} else if (!ctx.equals(other.ctx))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "CacheKey [ctx=" + ctx + "]";
		}

	}

}
