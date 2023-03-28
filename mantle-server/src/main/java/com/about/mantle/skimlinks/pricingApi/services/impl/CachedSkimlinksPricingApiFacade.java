package com.about.mantle.skimlinks.pricingApi.services.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.commerce.SkimlinksCommerceModel;
import com.about.mantle.skimlinks.pricingApi.services.SkimlinksPricingApiFacade;

public class CachedSkimlinksPricingApiFacade implements SkimlinksPricingApiFacade {

	private final SkimlinksPricingApiFacade delegate;
    private final CacheTemplate<Map<String, SkimlinksCommerceModel>> cache;

    public CachedSkimlinksPricingApiFacade(SkimlinksPricingApiFacade delegate,
                                                   CacheTemplate<Map<String, SkimlinksCommerceModel>> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public Map<String, SkimlinksCommerceModel> lookupItems(Collection<String> urls) {

    	Map<String, SkimlinksCommerceModel> answer = cache.get(
                new CacheKey(urls),
                () -> delegate.lookupItems(urls));
        return answer;
    }
	
	protected static class CacheKey implements Serializable, RedisCacheKey {

        private static final long serialVersionUID = 1L;

        private Collection<String> urls;

        public CacheKey(Collection<String> urls) {
            this.urls = urls;
        }
        
        @Override
		public String getUniqueKey() {
			StringBuilder builder = new StringBuilder();
			if (urls != null) {
				builder = RedisCacheKey.objectsToUniqueKey(builder, urls);
			}
			return builder.toString();
		}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CacheKey cacheKey = (CacheKey) o;

            return urls != null ? !urls.equals(cacheKey.urls) : cacheKey.urls == null;
        }

        @Override
        public int hashCode() {
            int result = urls != null ? urls.hashCode() : 0;
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CacheKey{");
            sb.append("urls=").append(urls);
            sb.append('}');
            return sb.toString();
        }
    }

}
