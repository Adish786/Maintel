package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.globe.core.exception.GlobeException;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.disqus.DisqusThreadDetails;
import com.about.mantle.model.disqus.DisqusPost;
import com.about.mantle.model.services.DisqusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

public class CachedDisqusService implements DisqusService {

    private final DisqusService disqusService;
    private final CacheTemplate<DisqusThreadDetails> cacheTemplateGetThreadDetails;
    private final CacheTemplate<List<DisqusPost>> cacheTemplateGetPopularComments;
    private final CacheTemplate<List<DisqusPost>> cacheTemplateGetOriginalComments;

    private static final Logger logger = LoggerFactory.getLogger(CachedDisqusService.class);

    public CachedDisqusService(DisqusService disqusService, CacheTemplate<DisqusThreadDetails> cacheTemplateGetThreadDetails,
                               CacheTemplate<List<DisqusPost>> cacheTemplateGetPopularComments, CacheTemplate<List<DisqusPost>> cacheTemplateGetOriginalComments) {

        this.disqusService = disqusService;
        this.cacheTemplateGetThreadDetails = cacheTemplateGetThreadDetails;
        this.cacheTemplateGetPopularComments = cacheTemplateGetPopularComments;
        this.cacheTemplateGetOriginalComments = cacheTemplateGetOriginalComments;
    }

    @Override
    public DisqusThreadDetails getThreadDetails(Long docId) {

        ThreadCacheKey key = new ThreadCacheKey(docId);
        DisqusThreadDetails threadDetails = cacheTemplateGetThreadDetails.get(key, () -> {
            DisqusThreadDetails suppliedContent = null;
            try {
                suppliedContent = disqusService.getThreadDetails(docId);
            } catch (Exception e) {
                logger.error("Failed to supply thread details for [" + docId + "]", e);
                throw new GlobeException("Failed to supply thread details for [" + docId + "]", e);
            }
            if (suppliedContent == null) {
                logger.warn("Supplied thread details was null for key [{}]", key.getUniqueKey());
            }
            return suppliedContent;
        });

        if (threadDetails == null) {
            logger.warn("Cached thread details was null for key [{}]", key.getUniqueKey());
        }
        return threadDetails;
    }

    @Override
    public List<DisqusPost> getPopularPosts(String threadId, int limit, String interval) {

        PopularCommentsCacheKey key = new PopularCommentsCacheKey(threadId, limit, interval);

        List<DisqusPost> popularComments = cacheTemplateGetPopularComments.get(key, () -> {
            List<DisqusPost> suppliedContent = null;
            try {
                suppliedContent = disqusService.getPopularPosts(threadId, limit, interval);
            } catch (Exception e) {
                logger.error("Failed to supply thread details for [" + threadId + "]", e);
                throw new GlobeException("Failed to supply thread details for [" + threadId + "]", e);
            }
            if (suppliedContent == null) {
                logger.warn("Supplied thread details was null for key [{}]", key.getUniqueKey());
            }
            return suppliedContent;
        });

        if (popularComments == null) {
            logger.warn("Cached thread details was null for key [{}]", key.getUniqueKey());
        }
        return popularComments;
    }
    
    @Override
	public List<DisqusPost> getOriginalPosts(String threadId, int limit, String order) {
		
    	OriginalCommentsCacheKey key = new OriginalCommentsCacheKey(threadId, limit, order);
    	
    	return cacheTemplateGetOriginalComments.get(key, () -> {
    		List<DisqusPost> suppliedOriginalPosts = disqusService.getOriginalPosts(threadId, limit, order);
    		
    		if(suppliedOriginalPosts == null) {
    			logger.warn("Supplied original posts was null for key {}", key.getUniqueKey());
    		}
    		
    		return suppliedOriginalPosts;
    	});
	}

    private static class ThreadCacheKey implements Serializable, RedisCacheKey {
        private static final long serialVersionUID = 1L;
        private Long docId;

        ThreadCacheKey(Long docId){
            this.docId = docId;
        }

        @Override
        public String getUniqueKey() {
            return RedisCacheKey.objectsToUniqueKey(null, docId).toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((docId == null) ? 0 : docId.hashCode());
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
            ThreadCacheKey other = (ThreadCacheKey) obj;
            if (docId == null) {
                if (other.docId != null)
                    return false;
            } else if (!docId.equals(other.docId))
                return false;

            return true;
        }
    }

    private static class PopularCommentsCacheKey implements Serializable, RedisCacheKey {
        private static final long serialVersionUID = 1L;
        private String threadId;
        private Integer limit;
        private String interval;

        PopularCommentsCacheKey(String threadId, Integer limit, String interval){
            this.threadId = threadId;
            this.limit = limit;
            this.interval = interval;
        }

        @Override
        public String getUniqueKey() {
            return RedisCacheKey.objectsToUniqueKey(null, threadId, limit, interval).toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((threadId == null) ? 0 : threadId.hashCode());
            result = prime * result + ((limit == null) ? 0 : limit.hashCode());
            result = prime * result + ((interval == null) ? 0 : interval.hashCode());
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
            PopularCommentsCacheKey other = (PopularCommentsCacheKey) obj;
            if (threadId == null) {
                if (other.threadId != null)
                    return false;
            } else if (!threadId.equals(other.threadId))
                return false;
            if (limit == null) {
                if (other.limit != null)
                    return false;
            } else if (!limit.equals(other.limit))
                return false;
            if (interval == null) {
                if (other.interval != null)
                    return false;
            } else if (!interval.equals(other.interval))
                return false;

            return true;
        }
    }
    
    private static class OriginalCommentsCacheKey implements Serializable, RedisCacheKey {
        private static final long serialVersionUID = 1L;
        private String threadId;
        private Integer limit;
        private String order;

        OriginalCommentsCacheKey(String threadId, Integer limit, String order){
            this.threadId = threadId;
            this.limit = limit;
            this.order = order;
        }

        @Override
        public String getUniqueKey() {
            return RedisCacheKey.objectsToUniqueKey(null, threadId, limit, order).toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((threadId == null) ? 0 : threadId.hashCode());
            result = prime * result + ((limit == null) ? 0 : limit.hashCode());
            result = prime * result + ((order == null) ? 0 : order.hashCode());
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
            OriginalCommentsCacheKey other = (OriginalCommentsCacheKey) obj;
            if (threadId == null) {
                if (other.threadId != null)
                    return false;
            } else if (!threadId.equals(other.threadId))
                return false;
            if (limit == null) {
                if (other.limit != null)
                    return false;
            } else if (!limit.equals(other.limit))
                return false;
            if (order == null) {
                if (other.order != null)
                    return false;
            } else if (!order.equals(other.order))
                return false;

            return true;
        }
    }
}
