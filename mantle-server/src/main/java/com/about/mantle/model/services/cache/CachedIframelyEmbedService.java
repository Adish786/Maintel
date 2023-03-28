package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.globe.core.cache.hash.MapHasher;
import com.about.globe.core.exception.GlobeException;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.services.EmbedService;
import com.about.mantle.model.services.embeds.EmbedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

public class CachedIframelyEmbedService implements EmbedService{
    private static final Logger logger = LoggerFactory.getLogger(CachedIframelyEmbedService.class);

    private final EmbedService service;
    private final CacheTemplate<EmbedContent> cache;

    public CachedIframelyEmbedService(EmbedService service, CacheTemplate<EmbedContent> cache) {
        this.service = service;
        this.cache = cache;
    }

    @Override
    public EmbedContent getContent(String url, Map<String, Map<String, String>> options) {
        CachedIframelyEmbedService.CacheKey key = new CachedIframelyEmbedService.CacheKey(url, options);
        EmbedContent cachedContent = cache.get(key, () -> {
            EmbedContent suppliedContent = null;
            try {
                suppliedContent = service.getContent(url, options);
            } catch (Exception e) {
                // just in case there is some non-GlobeException that's being thrown in there (e.g. jaxrs)
                logger.error("Failed to supply embed content for url [" + url + "]", e);
                throw new GlobeException("Failed to supply embed content for url [" + url + "]", e);
            }
            if (suppliedContent == null) {
                logger.warn("Supplied embed content was null for key [{}]", key.getUniqueKey());
            }
            return suppliedContent;
        });
        if (cachedContent == null) {
            logger.warn("Cached embed content was null for key [{}]", key.getUniqueKey());
        }
        return cachedContent;
    }

    private static class CacheKey implements Serializable, RedisCacheKey {

        private static final long serialVersionUID = 1L;
        private String url;
        private Map<String, Map<String, String>> options;

        CacheKey(String url, Map<String, Map<String, String>> options) {
            this.url = url;
            this.options = options;
        }

        @Override
        public String getUniqueKey() {
            return RedisCacheKey.objectsToUniqueKey(null, url, options).toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((url == null) ? 0 : url.hashCode());
            result = prime * result + MapHasher.INSTANCE.hash(options);
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
            CachedIframelyEmbedService.CacheKey other = (CachedIframelyEmbedService.CacheKey) obj;
            if (url == null) {
                if (other.url != null)
                    return false;
            } else if (!url.equals(other.url))
                return false;
            if (options == null) {
                if (other.options != null)
                    return false;
            } else if (!options.equals(other.options))
                return false;
            return true;
        }

    }
}
