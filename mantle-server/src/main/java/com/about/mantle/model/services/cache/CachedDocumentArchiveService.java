package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.StaticCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.services.DocumentArchiveService;

import java.util.Set;

public class CachedDocumentArchiveService implements DocumentArchiveService {

	private static final StaticCacheKey key = new StaticCacheKey("ArchivedDocuments");
	private final DocumentArchiveService documentArchiveService;
	private final CacheTemplate<Set<String>> documentArchiveResponseCache;


	public CachedDocumentArchiveService(DocumentArchiveService documentArchiveService, CacheTemplate<Set<String>> documentArchiveResponseCache) {
		this.documentArchiveService = documentArchiveService;
		this.documentArchiveResponseCache = documentArchiveResponseCache;
		// Preload list of archived documents on startup like we do legacy URLs
		getArchivedDocuments();
	}

	@Override
	public Set<String> getArchivedDocuments() {
		return documentArchiveResponseCache.get(key, () -> documentArchiveService.getArchivedDocuments());
	}
}
