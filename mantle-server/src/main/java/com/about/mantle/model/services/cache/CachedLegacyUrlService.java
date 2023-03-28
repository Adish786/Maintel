package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.LegacyUrlResultEx;
import com.about.mantle.model.services.LegacyUrlService;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CachedLegacyUrlService implements LegacyUrlService {
    private CacheTemplate<LegacyUrlResultEx> legacyUrlResultCache;
    private LegacyUrlService legacyUrlService;
    private final String vertical;

    public CachedLegacyUrlService(LegacyUrlService legacyUrlService, CacheTemplate<LegacyUrlResultEx> legacyUrlResultCache, String vertical){
        this.legacyUrlService = legacyUrlService;
        this.legacyUrlResultCache = legacyUrlResultCache;
        this.vertical = vertical;
    }

    @Override
    public LegacyUrlResultEx getUrlList(long cursor, int limit) {
        CacheKey key = new CacheKey(cursor, limit, vertical);

        //First check the cache, if it's there we return the result
        LegacyUrlResultEx answer = legacyUrlResultCache.get(key);

        //If there's no Result Cached, call the service
        if (answer == null){
            answer = legacyUrlService.getUrlList(cursor, limit);

            //Cursor is a time stamp, so if there is no timestamp for the future, that means this cursor could get more
            //results later when they get published, so we don't want to cache a result where next cursor is null as
            //that will be the cursor to get new updates with
            if(answer.getNextCursor() != null){
                legacyUrlResultCache.update(key, answer);
            }
        }

        return answer;
    }

    private static class CacheKey implements Serializable, RedisCacheKey {
        private static final long serialVersionUID = 1L;

        private long cursor;
        private long limit;
        private String vertical;

        public CacheKey(long cursor, long limit, String vertical){
            this.vertical = vertical;
            this.cursor = cursor;
            this.limit = limit;
        }

        @Override
        public String getUniqueKey() {
            return this.toString();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(vertical)
                    .append(cursor)
                    .append(limit)
                    .build();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            CacheKey other = (CacheKey) obj;
            if(vertical == null && vertical != other.vertical) return false;
            if(vertical != null && !vertical.equals(other.vertical)) return false;
            if (cursor != other.cursor) return false;
            if (limit != other.limit) return false;
            return true;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CacheKey{");
            sb.append("cursor=").append(cursor);
            sb.append(", limit=").append(limit);
            sb.append(", vertical=").append(vertical);
            sb.append('}');
            return sb.toString();
        }
    }
}
