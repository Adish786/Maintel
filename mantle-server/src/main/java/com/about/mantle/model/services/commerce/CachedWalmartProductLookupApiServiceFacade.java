package com.about.mantle.model.services.commerce;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.globe.core.cache.hash.CollectionHasher;
import com.about.globe.core.cache.hash.Hasher;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.commerce.WalmartCommerceModel;
import com.dotdash.walmart.productlookup.ProductLookupApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class CachedWalmartProductLookupApiServiceFacade implements WalmartProductLookupApiServiceFacade {

    private static Logger logger = LoggerFactory.getLogger(CachedWalmartProductLookupApiServiceFacade.class);

    private final WalmartProductLookupApiServiceFacade delegate;
    private final CacheTemplate<Map<Integer, WalmartCommerceModel>> cache;

    public CachedWalmartProductLookupApiServiceFacade(
            WalmartProductLookupApiServiceFacade delegate, CacheTemplate<Map<Integer, WalmartCommerceModel>> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public Map<Integer, WalmartCommerceModel> lookupItems(Collection<Integer> ids, String imageSize) {
        Map<Integer, WalmartCommerceModel> answer = cache.get(
                new CacheKey(ids, imageSize), () -> delegate.lookupItems(ids, imageSize));
        return answer;
    }

    protected static class CacheKey implements Serializable, RedisCacheKey  {

        private static final long serialVersionUID = 1L;

        private Collection<Integer> ids;
        private String imageSize;

        public CacheKey(Collection<Integer> ids, String imageSize) {
            this.ids = ids;
            this.imageSize = imageSize;
        }

		@Override
		public String getUniqueKey() {
			StringBuilder builder = new StringBuilder();
			builder.append(imageSize).append('|');
			if (ids != null) {
				builder = RedisCacheKey.objectsToUniqueKey(builder, ids);
			}
			return builder.toString();
		}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CacheKey cacheKey = (CacheKey) o;

            if (ids != null ? !ids.equals(cacheKey.ids) : cacheKey.ids != null) return false;
            return imageSize != null ? imageSize.equals(cacheKey.imageSize) : cacheKey.imageSize == null;
        }

        @Override
        public int hashCode() {
            int result = CollectionHasher.INSTANCE.hash(ids);
            result = 31 * result + (imageSize != null ? imageSize.hashCode() : 0);
            return result;
        }
    }
}
