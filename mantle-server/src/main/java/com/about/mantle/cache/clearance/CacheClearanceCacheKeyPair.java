package com.about.mantle.cache.clearance;

import com.about.hippodrome.ehcache.template.CacheTemplate;

import java.util.Objects;

public class CacheClearanceCacheKeyPair {
    private Object key;
    private CacheTemplate cacheTemplate;

    public CacheClearanceCacheKeyPair(Object key, CacheTemplate cacheTemplate){
        this.key = key;
        this.cacheTemplate = cacheTemplate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheClearanceCacheKeyPair)) return false;
        CacheClearanceCacheKeyPair that = (CacheClearanceCacheKeyPair) o;
        return key.equals(that.key) && cacheTemplate.equals(that.cacheTemplate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, cacheTemplate);
    }

    public Object getKey() {
        return key;
    }

    public CacheTemplate getCacheTemplate() {
        return cacheTemplate;
    }
}
