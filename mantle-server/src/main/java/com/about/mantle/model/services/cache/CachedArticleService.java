package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.cache.clearance.CacheClearanceCacheKeyPair;
import com.about.mantle.cache.clearance.CacheClearanceRequest;
import com.about.mantle.cache.clearance.CacheClearanceTemplateModifier;
import com.about.mantle.cache.clearance.CacheClearanceThreadLocalUtils;
import com.about.mantle.cache.hash.ArticleFilterRequestHasher;
import com.about.mantle.model.PageRequest;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

public class CachedArticleService implements ArticleService {
	private static final Logger logger = LoggerFactory.getLogger(CachedArticleService.class);
	private final ArticleService articleService;
	private final CacheTemplate<SliceableListEx<BaseDocumentEx>> listRelatedCache;
	private final CacheTemplate<BaseDocumentEx> dedupRelatedCache;
	private final CacheTemplate<List<Long>> relatedDocIdCache;
	private final ExecutorService executorService;
	private final CacheMetrics cacheMetrics;

	public CachedArticleService(ArticleService articleService,
			CacheTemplate<SliceableListEx<BaseDocumentEx>> listRelatedCache,
			CacheTemplate<BaseDocumentEx> dedupRelatedCache,
			CacheTemplate<List<Long>> relatedDocIdCache,
			ExecutorService executorService) {
		this.articleService = articleService;
		this.listRelatedCache = listRelatedCache;
		this.dedupRelatedCache = dedupRelatedCache;
		this.relatedDocIdCache = relatedDocIdCache;
		this.executorService = executorService;
		this.cacheMetrics = new CacheMetrics();
	}

	@Override
	public SliceableListEx<BaseDocumentEx> getRelated(RelatedArticleRequestContext ctx, PageRequest pageRequest,
			ArticleFilterRequest articleFilterRequest) {

		// If list cache is not specified, no caching should be performed.
		if (listRelatedCache == null) return articleService.getRelated(ctx, pageRequest, articleFilterRequest);

		// If dedup cache is not specified, list cache should be used directly
		if (dedupRelatedCache == null) {
			CacheKey key = new CacheKey(pageRequest, articleFilterRequest, ctx);

			return listRelatedCache.get(key, () -> {
				return articleService.getRelated(ctx, pageRequest, articleFilterRequest);
			});
		}

		cacheMetrics.incrementTotalRequests();

		CacheKey key = new CacheKey(pageRequest, articleFilterRequest, ctx);

		// Suppliers cannot be passed into individual cache get requests, so cache clearance must be handled explicitly
		List<CacheClearanceRequest> cacheClearRequest = CacheClearanceThreadLocalUtils.getClearCacheRequests();

		if(cacheClearRequest != null && cacheClearRequest.size() > 0) {
			return refreshDedupCache(key, ctx, pageRequest, articleFilterRequest);
		}

		List<Long> relatedDocIds = relatedDocIdCache.get(key);
		// If list of related doc IDs is not cached, return response from list cache and refresh dedup caches
		if (relatedDocIds == null) {
		    cacheMetrics.incrementCacheMissesNoKey();
			return refreshDedupCache(key, ctx, pageRequest, articleFilterRequest);
		}

		List<BaseDocumentEx> foundDocuments = new ArrayList<>();
		boolean hasUncachedDocument = false;
		// Retrieve existing elements from cache and collect uncached documents for subsequent service call
		for (Long docId : relatedDocIds) {
			BaseDocumentEx doc = (BaseDocumentEx) dedupRelatedCache.get(docId);
			if (doc == null) {
				hasUncachedDocument = true;
				/* While we could break here, retrieving remaining documents helps ensure commonly-used docs are
				 * less likely to be evicted due to LRU policy.
				 */
			} else {
				foundDocuments.add(doc);
			}
		}

		// If not all related documents are cached, return response from list cache and refresh dedup caches
		if (hasUncachedDocument) {
		    cacheMetrics.incrementCacheMissesPartial();
			return refreshDedupCache(key, ctx, pageRequest, articleFilterRequest);
		}

		cacheMetrics.incrementCacheHits();

		return SliceableListEx.of(foundDocuments);
	}

	private SliceableListEx<BaseDocumentEx> refreshDedupCache(CacheKey key, RelatedArticleRequestContext ctx,
			  PageRequest pageRequest, ArticleFilterRequest articleFilterRequest) {
		SliceableListEx<BaseDocumentEx> result = listRelatedCache.get(key, () -> {
			return articleService.getRelated(ctx, pageRequest, articleFilterRequest);
		});

		try {
			// Updating cache asynchronously
			executorService.submit(() -> {
				List<Long> relatedIds = new ArrayList<Long>();
				for (BaseDocumentEx doc : result) {
					relatedIds.add(doc.getDocumentId());
					dedupRelatedCache.update(doc.getDocumentId(), doc);
				}
				relatedDocIdCache.update(key, relatedIds);
			});
		} catch (Exception e) {
			logger.error("Failed to add cache update to executor service", e);
		}

		return result;
	}

	@Override
	public SliceableListEx<BaseDocumentEx> getRelated(String url, PageRequest pageRequest,
			ArticleFilterRequest articleFilterRequest) {

		RelatedArticleRequestContext ctx = new RelatedArticleRequestContext.Builder().setUrl(url).build();
		return getRelated(ctx, pageRequest, articleFilterRequest);
	}

	public CacheMetrics getCacheMetrics() {
		return cacheMetrics;
	}

	private static class CacheKey implements Serializable, RedisCacheKey {

		private static final long serialVersionUID = 1L;

		private final PageRequest pageRequest;
		private final ArticleFilterRequest articleFilterRequest;
		private final RelatedArticleRequestContext ctx;

		public CacheKey(PageRequest pageRequest, ArticleFilterRequest articleFilterRequest, RelatedArticleRequestContext ctx) {
			this.pageRequest = pageRequest;
			this.articleFilterRequest = articleFilterRequest;
			this.ctx = ctx;
		}

		@Override
		public String getUniqueKey() {
			StringBuilder result = new StringBuilder();
			if (pageRequest != null){
				RedisCacheKey.objectsToUniqueKey(result,pageRequest.getLimit(),pageRequest.getOffset());
			}
			result.append('|');
			if (articleFilterRequest != null){
				RedisCacheKey.objectsToUniqueKey(result,articleFilterRequest.getExcludeKeys()
						,articleFilterRequest.getExcludeDocIds()
						,RedisCacheKey.orderedSetToString(articleFilterRequest.getTemplateTypes(),TemplateTypeEx::name)
				);
			}
			result.append('|');
			if (ctx != null) {
				RedisCacheKey.objectsToUniqueKey(result, ctx.getUrl(), ctx.getDocId(), ctx.getAlgorithm());
			}
			return result.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + new ArticleFilterRequestHasher().hash(articleFilterRequest);
			result = prime * result + ((pageRequest == null) ? 0 : pageRequest.hashCode());
			result = prime * result + ((ctx == null) ? 0 : ctx.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			CacheKey other = (CacheKey) obj;
			if (articleFilterRequest == null) {
				if (other.articleFilterRequest != null) return false;
			} else if (!articleFilterRequest.equals(other.articleFilterRequest)) return false;
			if (pageRequest == null) {
				if (other.pageRequest != null) return false;
			} else if (!pageRequest.equals(other.pageRequest)) return false;
			if (ctx == null) {
				if (other.ctx != null) return false;
			} else if (!ctx.equals(other.ctx)) return false;
			return true;
		}
	}

	public static class CacheMetrics {
		private AtomicLong totalRequests = new AtomicLong();
		private AtomicLong cacheMissesNoKey = new AtomicLong();
		private AtomicLong cacheMissesPartial = new AtomicLong();
		private AtomicLong cacheHits = new AtomicLong();

		public long incrementTotalRequests() {
			return totalRequests.addAndGet(1);
		}

		public long getTotalRequests() {
			return totalRequests.get();
		}

		public long incrementCacheMissesNoKey() {
			return cacheMissesNoKey.addAndGet(1);
		}

		public long getCacheMissesNoKey() {
			return cacheMissesNoKey.get();
		}

		public long incrementCacheMissesPartial() {
			return cacheMissesPartial.addAndGet(1);
		}

		public long getCacheMissesPartial() {
			return cacheMissesPartial.get();
		}

		public long incrementCacheHits() {
			return cacheHits.addAndGet(1);
		}

		public long getCacheHits() {
			return cacheHits.get();
		}
	}
}
