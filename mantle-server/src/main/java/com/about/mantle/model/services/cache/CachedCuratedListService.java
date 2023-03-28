package com.about.mantle.model.services.cache;

import java.io.Serializable;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListEx;
import com.about.mantle.model.extended.curatedlist.DocumentCuratedListOfListEx;
import com.about.mantle.model.extended.curatedlist.ImageCuratedListEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.CuratedListService;

public class CachedCuratedListService implements CuratedListService {

	protected final CuratedListService service;
	private final CacheTemplate<SliceableListEx<ImageCuratedListEx>> imageListByNameCache;
	private final CacheTemplate<SliceableListEx<DocumentCuratedListEx>> documentSummaryListByNameCache;
	private final CacheTemplate<SliceableListEx<DocumentCuratedListEx>> documentSummaryListHistoryByNameCache;
	private final CacheTemplate<SliceableListEx<DocumentCuratedListOfListEx>> documentSummaryListofListByNameCache;

	public CachedCuratedListService(CuratedListService service,
			CacheTemplate<SliceableListEx<ImageCuratedListEx>> imageListByNameCache,
			CacheTemplate<SliceableListEx<DocumentCuratedListEx>> documentSummaryListByNameCache,
			CacheTemplate<SliceableListEx<DocumentCuratedListOfListEx>> documentSummaryListofListByNameCache) {

		this(service, imageListByNameCache, documentSummaryListByNameCache, documentSummaryListByNameCache,
				documentSummaryListofListByNameCache);
	}

	public CachedCuratedListService(CuratedListService service,
			CacheTemplate<SliceableListEx<ImageCuratedListEx>> imageListByNameCache,
			CacheTemplate<SliceableListEx<DocumentCuratedListEx>> documentSummaryListByNameCache,
			CacheTemplate<SliceableListEx<DocumentCuratedListEx>> documentSummaryListHistoryByNameCache,
			CacheTemplate<SliceableListEx<DocumentCuratedListOfListEx>> documentSummaryListofListByNameCache) {

		this.service = service;

		this.imageListByNameCache = imageListByNameCache;
		this.documentSummaryListByNameCache = documentSummaryListByNameCache;
		this.documentSummaryListHistoryByNameCache = documentSummaryListHistoryByNameCache;
		this.documentSummaryListofListByNameCache = documentSummaryListofListByNameCache;
	}

	@Override
	public SliceableListEx<ImageCuratedListEx> getImageListByName(String listName, Boolean activeOnly, Integer pageNum,
			Integer itemsPerPage, Long activeDate) {
		CacheKey cacheKey = new CacheKey(listName, activeOnly, pageNum, itemsPerPage, activeDate, null);

		return imageListByNameCache.get(cacheKey, () -> {
			return service.getImageListByName(listName, activeOnly, pageNum, itemsPerPage, activeDate);
		});
	}

	@Override
	public SliceableListEx<DocumentCuratedListOfListEx> getDocumentSummaryListOfListByName(String listName,
			Boolean activeOnly, Integer pageNum, Integer itemsPerPage, Long activeDate) {
		CacheKey cacheKey = new CacheKey(listName, activeOnly, pageNum, itemsPerPage, activeDate, null);

		return documentSummaryListofListByNameCache.get(cacheKey, () -> {
			return service.getDocumentSummaryListOfListByName(listName, activeOnly, pageNum, itemsPerPage, activeDate);
		});
	}

	@Override
	public SliceableListEx<DocumentCuratedListOfListEx> getDocumentSummaryListOfListByName(String listName,
			Boolean activeOnly, Integer pageNum, Integer itemsPerPage, Long activeDate, String projection) {
		CacheKey cacheKey = new CacheKey(listName, activeOnly, pageNum, itemsPerPage, activeDate, projection);
		return documentSummaryListofListByNameCache.get(cacheKey, () -> {
			return service.getDocumentSummaryListOfListByName(listName, activeOnly, pageNum, itemsPerPage, activeDate, projection);
		});
	}

	@Override
	public SliceableListEx<DocumentCuratedListEx> getDocumentSummaryListByName(String listName, Boolean activeOnly,
																			   Integer pageNum, Integer itemsPerPage, Long activeDate,
																			   String projection) {

		CacheKey cacheKey = new CacheKey(listName, activeOnly, pageNum, itemsPerPage, activeDate, projection);

		return documentSummaryListByNameCache.get(cacheKey, () -> {
			return service.getDocumentSummaryListByName(listName, activeOnly, pageNum, itemsPerPage, activeDate, projection);
		});
	}

	@Override
	public SliceableListEx<DocumentCuratedListEx> getDocumentSummaryListByName(String listName, Boolean activeOnly,
			Integer pageNum, Integer itemsPerPage, Long activeDate) {
		CacheKey cacheKey = new CacheKey(listName, activeOnly, pageNum, itemsPerPage, activeDate, null);

		return documentSummaryListByNameCache.get(cacheKey, () -> {
			return service.getDocumentSummaryListByName(listName, activeOnly, pageNum, itemsPerPage, activeDate);
		});
	}

	@Override
	public SliceableListEx<DocumentCuratedListEx> getDocumentSummaryListHistoryByName(String listName,
			Integer historyDepth, Integer pageNum, Integer itemsPerPage) {
		CacheKey cacheKey = new CacheKey(listName, true, pageNum, itemsPerPage, null, null);

		return documentSummaryListHistoryByNameCache.get(cacheKey, () -> {
			return service.getDocumentSummaryListHistoryByName(listName, historyDepth, pageNum, itemsPerPage);
		});
	}

	protected static class CacheKey implements Serializable, RedisCacheKey {

		public static final CacheKey GLOBAL = new CacheKey(null, null, null, null, null, null);

		private static final long serialVersionUID = 1L;

		private final String name;
		private final Boolean activeOnly;
		private final Integer pageNum;
		private final Integer itemsPerPage;
		private final Long activeDate;
		private final String projection;

		public CacheKey(String name, Boolean activeOnly, Integer pageNum,
						Integer itemsPerPage, Long activeDate) {
			this(name, activeOnly, pageNum, itemsPerPage, activeDate, null);
		}

		public CacheKey(String name, Boolean activeOnly, Integer pageNum,
						Integer itemsPerPage, Long activeDate, String projection) {
			this.name = name;
			this.activeOnly = activeOnly;
			this.pageNum = pageNum;
			this.itemsPerPage = itemsPerPage;
			this.activeDate = activeDate;
			this.projection = projection;
		}

		@Override
		public String getUniqueKey() {
			return RedisCacheKey.objectsToUniqueKey(null,name,activeOnly,pageNum,itemsPerPage,activeDate,projection).toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((activeDate == null) ? 0 : activeDate.hashCode());
			result = prime * result + ((activeOnly == null) ? 0 : activeOnly.hashCode());
			result = prime * result + ((itemsPerPage == null) ? 0 : itemsPerPage.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((pageNum == null) ? 0 : pageNum.hashCode());
			result = prime * result + ((projection == null) ? 0 : projection.hashCode());
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
			if (activeOnly == null) {
				if (other.activeOnly != null) return false;
			} else if (!activeOnly.equals(other.activeOnly)) return false;
			if (itemsPerPage == null) {
				if (other.itemsPerPage != null) return false;
			} else if (!itemsPerPage.equals(other.itemsPerPage)) return false;
			if (name == null) {
				if (other.name != null) return false;
			} else if (!name.equals(other.name)) return false;
			if (pageNum == null) {
				if (other.pageNum != null) return false;
			} else if (!pageNum.equals(other.pageNum)) return false;
			if (projection == null) {
				if(other.projection != null) return false;
			} else if (!projection.equals(other.projection)) return false;
			return true;
		}

		@Override
		public String toString() {
			final StringBuffer sb = new StringBuffer("CacheKey{");
			sb.append("name='").append(name).append('\'');
			sb.append(", activeOnly=").append(activeOnly);
			sb.append(", pageNum=").append(pageNum);
			sb.append(", itemsPerPage=").append(itemsPerPage);
			sb.append(", activeDate=").append(activeDate);
			sb.append(", projection=").append(projection);
			sb.append('}');
			return sb.toString();
		}
	}

}
