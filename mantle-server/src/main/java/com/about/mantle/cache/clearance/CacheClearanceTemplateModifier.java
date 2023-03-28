package com.about.mantle.cache.clearance;

import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.cache.ModifiedCacheTemplate;
import com.about.hippodrome.ehcache.template.CacheTemplate;

/**
 * Checks if this entry is a candidate for update, if yes then update else
 * return whatever there's in cache.
 */
public class CacheClearanceTemplateModifier<T> extends ModifiedCacheTemplate<T, T> {

	private static final Logger logger = LoggerFactory.getLogger(CacheClearanceTemplateModifier.class);

	private String name;
	private Integer level;
	public static Integer CACHE_CLEARANCE_BASE_LEVEL = 0;
	public static Integer CACHE_CLEARANCE_ALL_LEVEL = Integer.MAX_VALUE;

	public CacheClearanceTemplateModifier(CacheTemplate<T> backingCache, String name) {
		this(backingCache,name,CACHE_CLEARANCE_BASE_LEVEL);
	}

	public CacheClearanceTemplateModifier(CacheTemplate<T> backingCache, String name, Integer level) {
		super(backingCache);
		this.name = name;
		this.level = level;
	}

	@Override
	protected T convertResult(T result) {
		return result;
	}

	@Override
	protected T convertValue(T value) {
		return value;
	}

	@Override
	public T get(Object key, Supplier<T> supplier) {
		List<CacheClearanceRequest> cacheClearRequest = CacheClearanceThreadLocalUtils.getClearCacheRequests();

		if(cacheClearRequest != null && cacheClearRequest.size() > 0) {
			CacheClearanceCacheKeyPair pair = new CacheClearanceCacheKeyPair(key, this);
			Boolean alreadyCleaned = CacheClearanceThreadLocalUtils.alreadyCleaned(pair);

			if(!alreadyCleaned && CacheClearanceThreadLocalUtils.isTargetedCacheClearCandidate(level, name)) {
				logger.debug("Performing targeted cache clearance for cache name {} for key {}", name, key);
				return backingCache.update(key, supplier);
			}
		}

		return backingCache.get(key, supplier);
	}

}
