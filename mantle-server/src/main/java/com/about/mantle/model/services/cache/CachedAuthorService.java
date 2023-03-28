package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.GenericCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.AuthorEx;
import com.about.mantle.model.services.AuthorService;

public class CachedAuthorService implements AuthorService {

	private final AuthorService authorService;
	private final CacheTemplate<AuthorEx> authorByIdCache;

	public CachedAuthorService(AuthorService authorService, CacheTemplate<AuthorEx> authorByIdCache) {
		this.authorService = authorService;
		this.authorByIdCache = authorByIdCache;
	}

	@Override
	public AuthorEx getAuthorById(String id) {
		GenericCacheKey<String> key = new GenericCacheKey<>(id);

		return authorByIdCache.get(key, () -> authorService.getAuthorById(id));
	}

}
