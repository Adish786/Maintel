package com.about.mantle.model.services.cache;

import java.util.Map;

import com.about.globe.core.cache.GenericCacheKey;
import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.GeoData;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.services.ConsentBannerService;

/**
 * We map the geoData to a template and then template to its domain data to optimize for space in the cache.
 * e.g. DE-BE-EU -> GDPR | GDPR -> (big domain data payload)
 *      IT-VE-EU -> GDPR |
 */
public class CachedConsentBannerService implements ConsentBannerService {

	private final ConsentBannerService service;
	private final CacheTemplate<Template> templateCache;
	private final CacheTemplate<Map<String, Object>> domainDataCache;

	public CachedConsentBannerService(ConsentBannerService service,
	                                  CacheTemplate<Template> templateCache, CacheTemplate<Map<String, Object>> domainDataCache) {
		this.service = service;
		this.templateCache = templateCache;
		this.domainDataCache = domainDataCache;
	}

	@Override
	public Map<String, Object> getDomainData(GeoData geoData) {
		final Template expectedTemplate = ConsentBannerService.getExpectedTemplate(geoData);
		if (expectedTemplate == null) {
			// We don't want these filling the cache.
			return null;
		}

		final GenericCacheKey<GeoData> geoKey = new GenericCacheKey<>(geoData);
		Template template = templateCache.get(geoKey, () -> {
			Map<String, Object> domainData = service.getDomainData(geoData);
			if (domainData == null) {
				// Note that this is the only reason we need the template cache. If we were guaranteed a non-null return
				// then the template would always be a static computation, namely the "expected" template.
				return null; // No need to log an error here because the service will already log it.
			}
			domainDataCache.update(expectedTemplate, domainData);
			return expectedTemplate;
		});
		if (template == null) {
			return null;
		}

		final GenericCacheKey<Template> templateKey = new GenericCacheKey<>(template);
		return domainDataCache.get(templateKey, () -> {
			Map<String, Object> domainData = service.getDomainData(geoData);
			if (domainData == null) {
				// SERIOUS PROBLEM!
				throw new GlobeException("Failed to populate " + template + " template for " + geoData);
			}
			return domainData;
		});
	}

}
