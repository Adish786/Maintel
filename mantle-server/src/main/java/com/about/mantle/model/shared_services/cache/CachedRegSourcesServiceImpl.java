package com.about.mantle.model.shared_services.cache;

import com.about.globe.core.cache.GenericCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.shared_services.regsources.RegSourcesService;
import com.about.mantle.model.shared_services.regsources.response.RegSource;

public class CachedRegSourcesServiceImpl implements RegSourcesService {

    private CacheTemplate<RegSource> regSourceCacheTemplate;
    private RegSourcesService regSourcesService;

    public CachedRegSourcesServiceImpl(CacheTemplate<RegSource> regSourceCacheTemplate, RegSourcesService regSourcesService) {
        this.regSourceCacheTemplate = regSourceCacheTemplate;
        this.regSourcesService = regSourcesService;
    }

    @Override
    public RegSource getRegSourceById(String id) {
        GenericCacheKey<String> cacheKey = new GenericCacheKey(id);
        return regSourceCacheTemplate.get(cacheKey, () -> regSourcesService.getRegSourceById(id));
    }
}
