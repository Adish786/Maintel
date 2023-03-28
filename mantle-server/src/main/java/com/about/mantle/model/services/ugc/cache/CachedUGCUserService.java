package com.about.mantle.model.services.ugc.cache;

import com.about.globe.core.cache.GenericCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.services.ugc.UGCUserService;
import com.about.mantle.model.services.ugc.dto.UGCUserDto;
import com.about.mantle.model.services.ugc.impl.UGCUserServiceImpl;

public class CachedUGCUserService implements UGCUserService {

    private final UGCUserService ugcUserService;
    private final CacheTemplate<UGCUserDto> ugcUserCache;

    public CachedUGCUserService(UGCUserServiceImpl ugcUserService,
                                CacheTemplate<UGCUserDto> ugcUserCache) {
        this.ugcUserService = ugcUserService;
        this.ugcUserCache = ugcUserCache;
    }

    @Override
    public UGCUserDto getUgcUserDtoById(String id) {
        GenericCacheKey<String> key = new GenericCacheKey<>(id);

        return ugcUserCache.get(key, () -> ugcUserService.getUgcUserDtoById(id));
    }


}
