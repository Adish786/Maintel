package com.about.mantle.amazon.productAdvertisingApi.v5.services.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.globe.core.cache.hash.CollectionHasher;
import com.about.globe.core.cache.hash.EnumHasher;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.amazon.productAdvertisingApi.v5.services.AmazonProductAdvertisingApiFacade;
import com.about.mantle.model.commerce.AmazonCommerceModelV5;
import com.amazon.paapi5.v1.GetItemsResource;

public class CachedAmazonProductAdvertisingApiFacade implements AmazonProductAdvertisingApiFacade {

    private final AmazonProductAdvertisingApiFacade delegate;
    private final CacheTemplate<Collection<AmazonCommerceModelV5>> cache;

    public CachedAmazonProductAdvertisingApiFacade(AmazonProductAdvertisingApiFacade delegate,
                                                   CacheTemplate<Collection<AmazonCommerceModelV5>> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public Collection<AmazonCommerceModelV5> getItems(Collection<String> asins, EnumSet<GetItemsResource> itemResources, Locale locale, String imageSize) {

        Collection<AmazonCommerceModelV5> answer = cache.get(
                new CacheKey(asins, itemResources, locale, imageSize),
                () -> delegate.getItems(asins, itemResources, locale, imageSize));
        return answer;
    }

    protected static class CacheKey implements Serializable, RedisCacheKey {

        private static final long serialVersionUID = 1L;
        private static final CollectionHasher enumCollectionHasher = new CollectionHasher(new EnumHasher());

        public Collection<String> asins;
        public EnumSet<GetItemsResource> itemResources;
        public Locale locale;
        public String imageSize;

        public CacheKey(Collection<String> asins, EnumSet<GetItemsResource> itemResources, Locale locale,
                        String imageSize) {
            this.asins = asins;
            this.itemResources = itemResources;
            this.locale = locale;
            this.imageSize = imageSize;
        }

        @Override
		public String getUniqueKey() {
        	StringBuilder builder = new StringBuilder();
			builder.append(imageSize).append('|');
			if (asins != null) {
				builder = RedisCacheKey.objectsToUniqueKey(builder, asins);
			}
			if (itemResources != null) {
				builder = RedisCacheKey.objectsToUniqueKey(builder, itemResources);
			}
			if (locale != null) {
				builder = RedisCacheKey.objectsToUniqueKey(builder, locale.getLanguage(), locale.getCountry());
			}
			return builder.toString();
		}
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CacheKey cacheKey = (CacheKey) o;

            if (asins != null ? !asins.equals(cacheKey.asins) : cacheKey.asins != null) return false;
            if (itemResources != null ? !itemResources.equals(cacheKey.itemResources) : cacheKey.itemResources != null)
                return false;
            if (locale != null ? !locale.equals(cacheKey.locale) : cacheKey.locale != null) return false;
            return imageSize != null ? imageSize.equals(cacheKey.imageSize) : cacheKey.imageSize == null;
        }

        @Override
        public int hashCode() {
            int result = asins != null ? asins.hashCode() : 0;
            result = 31 * result + enumCollectionHasher.hash(itemResources);
            result = 31 * result + (locale != null ? locale.toString().hashCode() : 0);
            result = 31 * result + (imageSize != null ? imageSize.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CacheKey{");
            sb.append("asins=").append(asins);
            sb.append('}');
            return sb.toString();
        }
    }
}
