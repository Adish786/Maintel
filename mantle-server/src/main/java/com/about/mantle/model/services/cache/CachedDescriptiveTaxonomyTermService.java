package com.about.mantle.model.services.cache;

import com.about.globe.core.cache.StaticCacheKey;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.descriptive_taxonomy_terms.DescriptiveTaxonomyTermParsedData;
import com.about.mantle.model.services.DescriptiveTaxonomyTermService;

public class CachedDescriptiveTaxonomyTermService implements DescriptiveTaxonomyTermService {

	private DescriptiveTaxonomyTermService service;
	private CacheTemplate<DescriptiveTaxonomyTermParsedData> generateDescriptionTaxonomyTermParsedDataCache;
	private static final StaticCacheKey descriptiveTaxonomyTermsKey = new StaticCacheKey("descriptiveTaxonomyTermsKey");


	public CachedDescriptiveTaxonomyTermService(DescriptiveTaxonomyTermService service,
												CacheTemplate<DescriptiveTaxonomyTermParsedData> generateDescriptionTaxonomyTermParsedDataCache) {
		this.service = service;
		this.generateDescriptionTaxonomyTermParsedDataCache = generateDescriptionTaxonomyTermParsedDataCache;
	}

	@Override
	public DescriptiveTaxonomyTermParsedData generateDescriptionTaxonomyTermParsedData() {
		return generateDescriptionTaxonomyTermParsedDataCache.get(descriptiveTaxonomyTermsKey, () -> service.generateDescriptionTaxonomyTermParsedData());
	}
}
