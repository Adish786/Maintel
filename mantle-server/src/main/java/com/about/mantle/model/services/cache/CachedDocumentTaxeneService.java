package com.about.mantle.model.services.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.globe.core.cache.hash.CollectionHasher;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.cache.clearance.CacheClearanceRequest;
import com.about.mantle.cache.clearance.CacheClearanceThreadLocalUtils;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentTaxeneComposite;
import com.about.mantle.model.extended.docv2.DocumentTaxeneComposite;
import com.about.mantle.model.services.DocumentTaxeneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cached implementation of {@link DocumentTaxeneService}.  Delegates down to a real instance for cache miss.
 */
public class CachedDocumentTaxeneService implements DocumentTaxeneService {
	private static final Logger logger = LoggerFactory.getLogger(CachedDocumentTaxeneService.class);

	private DocumentTaxeneService delegateDocTaxSvc;

	private final CacheTemplate documentTaxeneCompositeListCache;
	private final CacheTemplate dedupDocumentTaxeneCompositeCache;
	private final CacheTemplate<List<CuratedDocumentTaxeneComposite>> curatedDocumentTaxeneCompositeListCache;
	private final ExecutorService executorService;
	private final CacheMetrics cacheMetrics;

	public CachedDocumentTaxeneService(DocumentTaxeneService delegateDocTaxSvc,
			CacheTemplate documentTaxeneCompositeListCache,
		   	CacheTemplate<List<CuratedDocumentTaxeneComposite>> curatedDocumentTaxeneCompositeListCache,
			CacheTemplate dedupDocumentTaxeneCompositeCache,
		   	ExecutorService executorService){
		this.delegateDocTaxSvc = delegateDocTaxSvc;
		this.documentTaxeneCompositeListCache = documentTaxeneCompositeListCache;
		this.curatedDocumentTaxeneCompositeListCache = curatedDocumentTaxeneCompositeListCache;
		this.dedupDocumentTaxeneCompositeCache = dedupDocumentTaxeneCompositeCache;
		this.executorService = executorService;
		this.cacheMetrics = new CacheMetrics();
	}

	/**
	 * @see DocumentTaxeneService#getDocumentTaxeneComposite(BaseDocumentEx, String)
	 */
	@Override
	public <T extends BaseDocumentEx> DocumentTaxeneComposite<T> getDocumentTaxeneComposite(T document,
																							String projection) {
		if (dedupDocumentTaxeneCompositeCache != null) {
			// Serving call as a list call of 1 document, as this should be able to be stored in dedup cache
			List<DocumentTaxeneComposite<T>> response = getDocumentTaxeneCompositeList(Collections.singletonList(document), projection);
			if (!response.isEmpty()) {
				return response.get(0);
			}
		}

		// If not using dedup cache, service is called normally
		return delegateDocTaxSvc.getDocumentTaxeneComposite(document, projection);
	}

	/**
	 * @see DocumentTaxeneService#getDocumentTaxeneCompositeList(List, String)
	 */
	@Override
	public <T extends BaseDocumentEx> List<DocumentTaxeneComposite<T>> getDocumentTaxeneCompositeList(
			List<T> documentList, String projection) {

		// If dedup cache is not specified, list cache should be used directly
		if (dedupDocumentTaxeneCompositeCache == null) {
			CacheKey key = new CacheKey (documentList, projection);
			return (List<DocumentTaxeneComposite<T>>) documentTaxeneCompositeListCache.get(key, ()-> delegateDocTaxSvc.getDocumentTaxeneCompositeList(documentList, projection));
		}

		cacheMetrics.incrementTotalRequests();

		// Suppliers cannot be passed into individual cache get requests, so cache clearance must be handled explicitly
		List<CacheClearanceRequest> cacheClearRequest = CacheClearanceThreadLocalUtils.getClearCacheRequests();

		if(cacheClearRequest != null && cacheClearRequest.size() > 0) {
			return refreshDedupCache(documentList, projection);
		}

		List<DocumentTaxeneComposite<T>> foundDocuments = new ArrayList<>();
		boolean hasUncachedDocument = false;

		// Retrieve existing elements from cache and collect uncached documents for subsequent service call
		for (T doc : documentList) {
			CacheKey key = new CacheKey(doc, projection);
			DocumentTaxeneComposite<T> result = (DocumentTaxeneComposite<T>) dedupDocumentTaxeneCompositeCache.get(key);
			if (result == null) {
				hasUncachedDocument = true;
				/* While we could break here, retrieving remaining documents helps ensure commonly-used docs are
				 * less likely to be evicted due to LRU policy.
				 */
			} else {
				foundDocuments.add(result);
			}
		}

		// If not all documents are cached, return response from list cache and refresh dedup cache
		if (hasUncachedDocument) {
			cacheMetrics.incrementCacheMissesPartial();
			return refreshDedupCache(documentList, projection);
		}

		cacheMetrics.incrementCacheHits();

		return foundDocuments;
	}

	private <T extends BaseDocumentEx> List<DocumentTaxeneComposite<T>> refreshDedupCache(List<T> documentList, String projection) {
		CacheKey key = new CacheKey (documentList, projection);
		List<DocumentTaxeneComposite<T>> result = (List<DocumentTaxeneComposite<T>>) documentTaxeneCompositeListCache.get(key, ()-> delegateDocTaxSvc.getDocumentTaxeneCompositeList(documentList, projection));

		try {
			// Updating cache asynchronously
			executorService.submit(() -> {
				for (DocumentTaxeneComposite<T> newCacheEntry : result) {
					CacheKey singleDocKey = new CacheKey(newCacheEntry.getDocument(), projection);
					dedupDocumentTaxeneCompositeCache.update(singleDocKey, newCacheEntry);
				}
			});
		} catch (Exception e) {
			logger.error("Failed to add cache update to executor service", e);
		}

		return result;
	}

	/**
	 * @see DocumentTaxeneService#getCuratedDocumentTaxeneCompositeList(List, String)
	 */
	@Override
	public  List<CuratedDocumentTaxeneComposite> getCuratedDocumentTaxeneCompositeList(
			List<CuratedDocumentEx> documentList, String projection) {
		CacheKey key = new CacheKey (documentList, projection);
		return curatedDocumentTaxeneCompositeListCache.get(key, ()-> delegateDocTaxSvc.getCuratedDocumentTaxeneCompositeList(documentList, projection));
	}

	public CacheMetrics getCacheMetrics() {
		return cacheMetrics;
	}

	private static class CacheKey implements Serializable, RedisCacheKey {
		private static final long serialVersionUID = 1L;
		private final Set<Long> setOfDocIds;
		private final Set<String> urls;
		private final String projection;


		public <T extends BaseDocumentEx> CacheKey(List<T> listOfDocument, String projection){
			this.setOfDocIds = Optional.ofNullable(listOfDocument).orElse(Collections.emptyList())
					.stream()
					.map(document -> document.getDocumentId())
					.collect(Collectors.toSet());
			this.urls = Optional.ofNullable(listOfDocument).orElse(Collections.emptyList())
					.stream()
					.map(document -> document.getUrl())
					.collect(Collectors.toSet());
			this.projection = projection;
		}

		public <T extends BaseDocumentEx> CacheKey(T document, String projection){
			this.setOfDocIds = Collections.singleton(document.getDocumentId());
			this.urls = Collections.singleton(document.getUrl());
			this.projection = projection;
		}

		@Override
		public String getUniqueKey() {
			return RedisCacheKey.objectsToUniqueKey(null,setOfDocIds,urls,projection).toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((projection == null) ? 0 : projection.hashCode());
			result = prime * result + CollectionHasher.INSTANCE.hash(urls);
			result = prime * result + CollectionHasher.INSTANCE.hash(setOfDocIds);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if(getClass() != obj.getClass()) return false;
			CacheKey object = (CacheKey)obj;
			if(projection!=null && !projection.equals(object.projection)) return false;
			if(!setOfDocIds.equals(object.setOfDocIds)) return false;
			if(!urls.equals(object.urls)) return false;
			return true;
		}
	}

	public static class CacheMetrics {
		private AtomicLong totalRequests = new AtomicLong();
		private AtomicLong cacheMissesPartial = new AtomicLong();
		private AtomicLong cacheHits = new AtomicLong();

		public long incrementTotalRequests() {
			return totalRequests.addAndGet(1);
		}

		public long getTotalRequests() {
			return totalRequests.get();
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
