package com.about.mantle.model.services.ugc.cache;

import java.io.Serializable;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.ugc.AggregatedFeedbacks;
import com.about.mantle.model.extended.ugc.AggregatedFeedbacksRequestContext;
import com.about.mantle.model.extended.ugc.AggregatedFeedbacksRequestContext.Builder;
import com.about.mantle.model.extended.ugc.Feedback;
import com.about.mantle.model.extended.ugc.PhotoGalleryItem;
import com.about.mantle.model.extended.ugc.PhotoGalleryRequestContext;
import com.about.mantle.model.extended.ugc.SortBy;
import com.about.mantle.model.services.ugc.UGCFeedbackService;
import com.about.mantle.model.services.ugc.impl.UGCFeedbackServiceImpl;

public class CachedUGCFeedbackService implements UGCFeedbackService {

	UGCFeedbackService ugcFeedbackService;
	CacheTemplate<AggregatedFeedbacks> ugcAggregatedFeedbacksCache;
	CacheTemplate<SliceableListEx<PhotoGalleryItem>> ugcPhotoGalleryCache;

	public CachedUGCFeedbackService(UGCFeedbackServiceImpl ugcFeedbackService,
			CacheTemplate<AggregatedFeedbacks> ugcAggregateFeedbacksCache,
			CacheTemplate<SliceableListEx<PhotoGalleryItem>> ugcPhotoGalleryCache) {
		this.ugcFeedbackService = ugcFeedbackService;
		this.ugcAggregatedFeedbacksCache = ugcAggregateFeedbacksCache;
		this.ugcPhotoGalleryCache = ugcPhotoGalleryCache;
	}

	@Override
	public Feedback postFeedback(Feedback feedback) {
		Feedback response = ugcFeedbackService.postFeedback(feedback);
		bustCacheToReflectNewFeedback(response);
		return response;
	}

	@Override
	public Feedback patchFeedback(Feedback feedback, String patchOperation) {
		Feedback response = ugcFeedbackService.patchFeedback(feedback, patchOperation);
		bustCacheToReflectNewFeedback(response);
		return response;
	}

	@Override
	public Feedback getFeedback(Long docId, String userId) {
		return ugcFeedbackService.getFeedback(docId, userId);
	}

	@Override
	public AggregatedFeedbacks getAggregatedFeedbacks(AggregatedFeedbacksRequestContext reqCtx) {
		CacheKey key = new CacheKey(reqCtx);
		return ugcAggregatedFeedbacksCache.get(key, () -> {
			return ugcFeedbackService.getAggregatedFeedbacks(reqCtx);
		});
	}

	@Override
	public Builder getDefaultAggregatedFeedbacksRequestContextBuilder(Long docId) {
		return ugcFeedbackService.getDefaultAggregatedFeedbacksRequestContextBuilder(docId);
	}

	@Override
	public SliceableListEx<PhotoGalleryItem> getPhotoGallery(PhotoGalleryRequestContext reqCtx) {
		CacheKey key = new CacheKey(reqCtx);
		return ugcPhotoGalleryCache.get(key, () -> {
			return ugcFeedbackService.getPhotoGallery(reqCtx);
		});
	}

	/**
	 * Best-effort attempt at busting the cache when receiving new feedback.
	 * @param feedback
	 */
	private void bustCacheToReflectNewFeedback(Feedback feedback) {
		try {
			AggregatedFeedbacksRequestContext reqCtx =
					ugcFeedbackService.getDefaultAggregatedFeedbacksRequestContextBuilder(feedback.getDocId()).build();
			CacheKey key = new CacheKey(reqCtx);
			if (ugcAggregatedFeedbacksCache.get(key) != null) {
				// intentionally not using the supplier form to mitigate this as an attack vector, i.e.
				// busting the cache is part of the user's rating request as opposed to getting spammed
				// and being at risk of having these accumulate and waste the server's resources
				AggregatedFeedbacks aggregatedFeedbacks = ugcFeedbackService.getAggregatedFeedbacks(reqCtx);
				ugcAggregatedFeedbacksCache.update(key, aggregatedFeedbacks);
			}
		} catch (Exception e) {
			// no-op: this is all best-effort; no reason to explode over errors here
		}
	}

	private static class CacheKey implements Serializable, RedisCacheKey {

		private static final long serialVersionUID = 1L;

		private final Long docId;
		private final SortBy sort;
		private final Integer offset;
		private final Integer limit;

		public CacheKey(AggregatedFeedbacksRequestContext requestContext) {
			this.docId = requestContext.getDocId();
			this.sort = requestContext.getSort();
			this.offset = requestContext.getOffset();
			this.limit = requestContext.getLimit();
		}

		public CacheKey(PhotoGalleryRequestContext requestContext) {
			this.docId = requestContext.getDocId();
			this.sort = null;
			this.offset = requestContext.getOffset();
			this.limit = requestContext.getLimit();
		}

		@Override
		public String getUniqueKey() {
			return RedisCacheKey.objectsToUniqueKey(null, docId, sort, offset, limit).toString();
		}

		@Override
		public String toString() {
			return "CachedUGCFeedbackServiceCacheKey{" +
					"docId=" + docId +
					", sort=" + sort +
					", offset=" + offset +
					", limit=" + limit +
					'}';
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((docId == null) ? 0 : docId.hashCode());
			result = prime * result + ((limit == null) ? 0 : limit.hashCode());
			result = prime * result + ((offset == null) ? 0 : offset.hashCode());
			result = prime * result + ((sort == null) ? 0 : sort.hashCode());
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
			if (docId == null) {
				if (other.docId != null)
					return false;
			} else if (!docId.equals(other.docId))
				return false;
			if (limit == null) {
				if (other.limit != null)
					return false;
			} else if (!limit.equals(other.limit))
				return false;
			if (offset == null) {
				if (other.offset != null)
					return false;
			} else if (!offset.equals(other.offset))
				return false;
			if (sort != other.sort)
				return false;
			return true;
		}
	}
}
