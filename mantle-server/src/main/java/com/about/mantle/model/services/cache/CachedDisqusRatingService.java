package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.GenericCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.ratings.DisqusAggregateRating;
import com.about.mantle.model.services.DisqusRatingService;

public class CachedDisqusRatingService implements DisqusRatingService {
	
	private final DisqusRatingService disqusRatingService;
	private final CacheTemplate<DisqusAggregateRating> disqusAggregateRatingCache;
	
	public CachedDisqusRatingService(DisqusRatingService disqusRatingService, CacheTemplate<DisqusAggregateRating> disqusAggregateRatingCache) {
		this.disqusRatingService = disqusRatingService;
		this.disqusAggregateRatingCache = disqusAggregateRatingCache;
	}

	@Override
	public DisqusAggregateRating getAggregateRating(Long documentId) {
		GenericCacheKey<Long> key = new GenericCacheKey<Long>(documentId);
		return disqusAggregateRatingCache.get(key, () -> disqusRatingService.getAggregateRating(documentId));
	}
	
}
