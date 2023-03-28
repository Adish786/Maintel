package com.about.mantle.model.services.cache;

import java.io.Serializable;

import com.about.globe.core.cache.RedisCacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.googleplace.GooglePlaceModel;
import com.about.mantle.model.services.location.LocationService;
import com.about.mantle.model.services.location.MockLocationServiceImpl;

public class CachedLocationService implements LocationService {

	private static final Logger logger = LoggerFactory.getLogger(CachedLocationService.class);
    private final LocationService locationService;
    private final CacheTemplate<GooglePlaceModel> googlePlaceModelCache;
    
    public CachedLocationService(LocationService locationService, CacheTemplate<GooglePlaceModel> googlePlaceModelCache) {
        this.locationService = locationService;
        this.googlePlaceModelCache = googlePlaceModelCache;
    }

    @Override
    public GooglePlaceModel getGooglePlaceModel(String placeId) {
        boolean isMockData = locationService instanceof MockLocationServiceImpl;
        CacheKey key = new CacheKey(placeId, isMockData);

        if (isMockData) {
            logger.warn("Could not find com.about.globe.googlemaps.api.apiKey. Returning mock data instead.");
        }

        GooglePlaceModel answer = googlePlaceModelCache.get(key, () -> locationService.getGooglePlaceModel(placeId));
        
        if (answer == null) {
        	logger.error("Got null response back for placeId: " + placeId);
        }
        return answer;
    }

    private static class CacheKey implements Serializable, RedisCacheKey {

        private static final long serialVersionUID = 1L;
        private final String placeId;
        private final boolean isMockData;

        public CacheKey(String placeId, boolean isMockData) {
            this.placeId = placeId;
            this.isMockData = isMockData;
        }

        @Override
        public String getUniqueKey() {
            return RedisCacheKey.objectsToUniqueKey(null, placeId,isMockData).toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((placeId == null) ? 0 : placeId.hashCode());
            result = prime * result + (isMockData ? 1231 : 1237);
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
            CacheKey other = (CacheKey) obj;
            if (placeId == null) {
                if (other.placeId != null) return false;
            } else if (!placeId.equals(other.placeId)) return false;
            if (isMockData != other.isMockData) return false;
            return true;
        }
    }
}
