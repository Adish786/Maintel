package com.about.mantle.amazon.productAdvertisingApi.services.impl;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.globe.core.cache.hash.CollectionHasher;
import com.about.globe.core.cache.hash.EnumHasher;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.amazon.productAdvertisingApi.model.ResponseGroup;
import com.about.mantle.amazon.productAdvertisingApi.services.AmazonProductAdvertisingApiFacade;
import com.about.mantle.model.commerce.AmazonCommerceModel;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;

public class CachedAmazonProductAdvertisingApiFacade implements AmazonProductAdvertisingApiFacade {

    private final AmazonProductAdvertisingApiFacade delegate;
    private final CacheTemplate<Collection<AmazonCommerceModel>> cache;

    public CachedAmazonProductAdvertisingApiFacade(AmazonProductAdvertisingApiFacade delegate,
                                                   CacheTemplate<Collection<AmazonCommerceModel>> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public Collection<AmazonCommerceModel> lookupItems(Collection<String> asins, EnumSet<ResponseGroup> responseGroups, Locale locale, String imageSize) {

        Collection<AmazonCommerceModel> answer = cache.get(
                new CacheKey(asins, responseGroups, locale, imageSize),
                () -> delegate.lookupItems(asins, responseGroups, locale, imageSize));
        return answer;
    }

    protected static class CacheKey implements Serializable, RedisCacheKey {

        private static final long serialVersionUID = 1L;
        private static final CollectionHasher enumCollectionHasher = new CollectionHasher(new EnumHasher());

        public Collection<String> asins;
        public EnumSet<ResponseGroup> responseGroups;
        public Locale locale;
        public String imageSize;

        public CacheKey(Collection<String> asins, EnumSet<ResponseGroup> responseGroups, Locale locale,
                        String imageSize) {
            this.asins = asins;
            this.responseGroups = responseGroups;
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
			if (responseGroups != null) {
				builder = RedisCacheKey.objectsToUniqueKey(builder, responseGroups);
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
            if (responseGroups != null ? !responseGroups.equals(cacheKey.responseGroups) : cacheKey.responseGroups != null)
                return false;
            if (locale != null ? !locale.equals(cacheKey.locale) : cacheKey.locale != null) return false;
            return imageSize != null ? imageSize.equals(cacheKey.imageSize) : cacheKey.imageSize == null;
        }

        @Override
        public int hashCode() {
            int result = asins != null ? asins.hashCode() : 0;
            result = 31 * result + enumCollectionHasher.hash(responseGroups);
            result = 31 * result + (locale != null ? locale.toString().hashCode() : 0);
            result = 31 * result + (imageSize != null ? imageSize.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("CacheKey{");
            sb.append("asins=").append(asins);
            sb.append('}');
            return sb.toString();
        }
    }
}
