package com.about.mantle.jmx;

import com.about.mantle.model.services.ArticleService;
import com.about.mantle.model.services.cache.CachedArticleService;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(objectName = "com.about.globe:type=Service,name=ArticleServiceJmxBean")
public class ArticleServiceJmxBean {

	private final CachedArticleService.CacheMetrics metrics;

	public ArticleServiceJmxBean(ArticleService articleService) {
		if (articleService instanceof CachedArticleService) {
			this.metrics = ((CachedArticleService)articleService).getCacheMetrics();
		} else {
			this.metrics = null;
		}
	}

	@ManagedAttribute(description = "Total Requests")
	public long getTotalRequests() {
		return metrics != null ? metrics.getTotalRequests() : -1;
	}

	@ManagedAttribute(description = "Cache Misses - No Key")
	public long getCacheMissesNoKey() {
		return metrics != null ? metrics.getCacheMissesNoKey() : -1;
	}

	@ManagedAttribute(description = "Cache Misses - Partially Cached")
	public long getCacheMissesPartial() {
		return metrics != null ? metrics.getCacheMissesPartial() : -1;
	}

	@ManagedAttribute(description = "Cache Hits")
	public long getCacheHits() {
		return metrics != null ? metrics.getCacheHits() : -1;
	}

}
