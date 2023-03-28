package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.docv2.MetaDataEx;
import com.about.mantle.model.services.MetaDataService;

import java.io.Serializable;

public class CachedMetaDataService implements MetaDataService {
    private MetaDataService metaDataService;
    private CacheTemplate<MetaDataEx> metaDataCache;

    public CachedMetaDataService(MetaDataService metaDataService, CacheTemplate<MetaDataEx> metaDataCache) {
        this.metaDataService = metaDataService;
        this.metaDataCache = metaDataCache;
    }

    @Override
    public MetaDataEx getMetaData(MetaDataRequestContext requestContext) {
        return metaDataCache.get(new CacheKey(requestContext), () -> metaDataService.getMetaData(requestContext));
    }

    private static class CacheKey implements Serializable, RedisCacheKey {

        private static final long serialVersionUID = 1L;

        private final String url;
        private final Long docId;
        private final String projection;

        public CacheKey(MetaDataRequestContext requestContext) {
            this.url = requestContext.getUrl();
            this.docId = requestContext.getDocId();
            this.projection = requestContext.getProjection();
        }

        @Override
        public String getUniqueKey() {
            return RedisCacheKey.objectsToUniqueKey(null, url, docId, projection).toString();
        }

        @Override
        public String toString() {
            return "CacheKey{" +
                    "url='" + url + '\'' +
                    ", docId=" + docId +
                    ", projection='" + projection + '\'' +
                    '}';
        }
    }
}
