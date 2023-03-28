package com.about.mantle.model.services.cache;

import java.io.Serializable;
import java.util.Set;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.globe.core.cache.hash.CollectionHasher;
import com.about.globe.core.cache.hash.EnumCollectionHasher;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.hippodrome.url.PlatformUrlData;
import com.about.hippodrome.url.PlatformUrlDataFactory;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.app.LegacyUrlMap;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.State;
import com.about.mantle.model.services.DocumentService;
import com.google.common.collect.ImmutableSet;

public class CachedDocumentService implements DocumentService {

	private final DocumentService documentService;
	private final CacheTemplate<BaseDocumentEx> documentResponseCache;
	private final PlatformUrlDataFactory urlDataFactory;

	@Deprecated
	public CachedDocumentService(DocumentService documentService, CacheTemplate<BaseDocumentEx> documentResponseCache) {
		this(documentService, documentResponseCache, null);
	}

	public CachedDocumentService(DocumentService documentService, CacheTemplate<BaseDocumentEx> documentResponseCache, PlatformUrlDataFactory urlDataFactory) {
		this.documentService = documentService;
		this.documentResponseCache = documentResponseCache;
		this.urlDataFactory = urlDataFactory;
	}

	@Override
	public BaseDocumentEx getDocument(DocumentReadRequestContext documentRequestContext) {
		CacheKey key = new CacheKey(documentRequestContext, urlDataFactory);

		return documentResponseCache.get(key, () -> {
			return documentService.getDocument(documentRequestContext);
		});
	}

	/**
	 * This method is not cached as it is only for exposing a utility function from
	 * DocumentServiceImpl.
	 * @param document Document to be modified
	 * @return
	 */
	@Override
	public BaseDocumentEx processDocument(BaseDocumentEx document) {
		return documentService.processDocument(document);
	}

	
	private static class CacheKey implements Serializable, RedisCacheKey {

		private static final long serialVersionUID = 1L;
		private final Set<String> urls;
		private final Set<Long> docIds;
		private final Set<State> states;
		private final Boolean allVersions;
		private final Integer limit;
		private final Long activeDate;

		public CacheKey(DocumentReadRequestContext context, PlatformUrlDataFactory urlDataFactory) {
			Set<String> urls = null;
			Set<Long> docIds = null;
			// Attempting to convert url parameter in context to docId parameter in key to prevent
			// caching the document twice.
			if (context.getUrl() != null && urlDataFactory != null) {
				PlatformUrlData urlData = urlDataFactory.create(context.getUrl());
				if(urlData instanceof VerticalUrlData) {
					VerticalUrlData verticalUrlData = (VerticalUrlData)urlData;
					if (verticalUrlData.getDocId() != null) {
						docIds = ImmutableSet.of(verticalUrlData.getDocId());
					}
				}
			}

			// If docId was not set from url, use docId or url from context
			if (docIds == null) {
				docIds = context.getDocId() != null ? ImmutableSet.of(context.getDocId()) : null;
				urls = context.getUrl() != null ? ImmutableSet.of(context.getUrl()) : null;
			}
			this.docIds = docIds;
			this.urls = urls;
			// null and active states are equivalent. Treating as equivalent in the key instead of the context
			// to keep this change together with the docId/url equivalence
			this.states = context.getState() != null ? ImmutableSet.of(context.getState()) : ImmutableSet.of(State.ACTIVE);

			this.limit = null;
			this.allVersions = false;

			this.activeDate = context.getActiveDate();
		}

		@Override
		public String getUniqueKey(){
			return this.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((activeDate == null) ? 0 : activeDate.hashCode());
			result = prime * result + ((allVersions == null) ? 0 : allVersions.hashCode());
			result = prime * result + CollectionHasher.INSTANCE.hash(docIds);
			result = prime * result + ((limit == null) ? 0 : limit.hashCode());
			result = prime * result + EnumCollectionHasher.INSTANCE.hash(states);
			result = prime * result + CollectionHasher.INSTANCE.hash(urls);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			CacheKey other = (CacheKey) obj;
			if (activeDate == null) {
				if (other.activeDate != null) return false;
			} else if (!activeDate.equals(other.activeDate)) return false;
			if (allVersions != other.allVersions) return false;
			if (docIds == null) {
				if (other.docIds != null) return false;
			} else if (!docIds.equals(other.docIds)) return false;
			if (limit == null) {
				if (other.limit != null) return false;
			} else if (!limit.equals(other.limit)) return false;
			if (states == null) {
				if (other.states != null) return false;
			} else if (!states.equals(other.states)) return false;
			if (urls == null) {
				if (other.urls != null) return false;
			} else if (!urls.equals(other.urls)) return false;
			return true;
		}

		@Override
		public String toString() {
			final StringBuffer sb = new StringBuffer("CacheKey{");
			sb.append("urls=").append(urls);
			sb.append(", docIds=").append(docIds);
			sb.append(", states=").append(RedisCacheKey.orderedSetToString(this.states,State::name));
			sb.append(", allVersions=").append(allVersions);
			sb.append(", limit=").append(limit);
			sb.append(", activeDate=").append(activeDate);
			sb.append('}');
			return sb.toString();
		}
	}
}
