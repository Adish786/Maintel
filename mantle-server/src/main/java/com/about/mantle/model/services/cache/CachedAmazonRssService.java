package com.about.mantle.model.services.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.commerce.AmazonRssService;

public class CachedAmazonRssService implements AmazonRssService {

	private final AmazonRssService amazonRssService;
	private final CacheTemplate<List<BaseDocumentEx>> amazonRssDocumentListCache;

	public CachedAmazonRssService(AmazonRssService service,
			CacheTemplate<List<BaseDocumentEx>> amazonRssDocumentListCache) {
		this.amazonRssService = service;
		this.amazonRssDocumentListCache = amazonRssDocumentListCache;
	}

	@Override
	public List<BaseDocumentEx> getDocumentsForRssFeed(String domain, int showDisplayDateInNHours) {
		CacheKey key = new CacheKey(domain, showDisplayDateInNHours);
		return amazonRssDocumentListCache.get(key, () -> {
			return amazonRssService.getDocumentsForRssFeed(domain, showDisplayDateInNHours);
		});
	}

	private static class CacheKey implements Serializable, RedisCacheKey {

		private static final long serialVersionUID = 1L;
		private final String domain;
		private final int showDisplayDateInNHours;

		public CacheKey(String domain, int showDisplayDateInNHours) {
			this.domain = domain;
			this.showDisplayDateInNHours = showDisplayDateInNHours;
		}

		@Override
		public String getUniqueKey() {
			return this.toString();
		}

		@Override
		public int hashCode() {
			return Objects.hash(domain, showDisplayDateInNHours);
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
			return Objects.equals(domain, other.domain)
					&& Objects.equals(showDisplayDateInNHours, other.showDisplayDateInNHours);
		}

		@Override
		public String toString() {
			return "CacheKey [domain=" + domain + ", showDisplayDateInNHours=" + showDisplayDateInNHours + "]";
		}

	}

}
