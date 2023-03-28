package com.about.mantle.jmx;

import com.about.mantle.model.services.DocumentTaxeneService;
import com.about.mantle.model.services.cache.CachedDocumentTaxeneService;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(objectName = "com.about.globe:type=Service,name=DocumentTaxeneServiceJmxBean")
public class DocumentTaxeneServiceJmxBean {

	private final CachedDocumentTaxeneService.CacheMetrics metrics;

	public DocumentTaxeneServiceJmxBean(DocumentTaxeneService taxeneService) {
		if (taxeneService instanceof CachedDocumentTaxeneService) {
			this.metrics = ((CachedDocumentTaxeneService)taxeneService).getCacheMetrics();
		} else {
			this.metrics = null;
		}
	}

	@ManagedAttribute(description = "Total Requests")
	public long getTotalRequests() {
		return metrics != null ? metrics.getTotalRequests() : -1;
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
