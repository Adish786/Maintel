package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.GenericCacheKey;
import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.services.DocSchemaService;

import java.io.Serializable;

public class CachedDocSchemaService implements DocSchemaService {

    protected final DocSchemaService service;
    private final CacheTemplate<String> docSchemaByDocIdCache;

    public CachedDocSchemaService(DocSchemaService service,
                                    CacheTemplate<String> docSchemaByDocIdCache) {

        this.service = service;
        this.docSchemaByDocIdCache = docSchemaByDocIdCache;
    }

    @Override
    public String getDocSchemaByDocId(Long docId) {
        GenericCacheKey<Long> cacheKey = new GenericCacheKey<>(docId);

        return docSchemaByDocIdCache.get(cacheKey, () -> {
            return service.getDocSchemaByDocId(docId);
        });
    }
}