package com.about.mantle.model.services.cache;

import java.io.Serializable;

import com.about.globe.core.cache.RedisCacheKey;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.about.globe.core.cache.hash.CollectionHasher;
import com.about.globe.core.cache.hash.EnumHasher;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.services.TaxeneRelationService;

public class CachedTaxeneRelationService implements TaxeneRelationService {

	private final TaxeneRelationService taxeneService;
	private final CacheTemplate<TaxeneNodeEx> taxeneCache;

	public CachedTaxeneRelationService(TaxeneRelationService taxeneService, CacheTemplate<TaxeneNodeEx> taxeneCache) {
		this.taxeneService = taxeneService;
		this.taxeneCache = taxeneCache;
	}

	@Override
	public TaxeneNodeEx traverse(TaxeneTraverseRequestContext reqCtx) {
		CacheKey cacheKey = new CacheKey(reqCtx);
		return taxeneCache.get(cacheKey, () -> 
			taxeneService.traverse(reqCtx));
	}

	private static class CacheKey implements Serializable, RedisCacheKey {
		private static final long serialVersionUID = 1L;

		private TaxeneTraverseRequestContext reqCtx;

		public CacheKey(TaxeneTraverseRequestContext reqCtx) {
			this.reqCtx = reqCtx;
		}

		@Override
		public String getUniqueKey() {
			if (reqCtx != null)
				return reqCtx.toString();
			return "null";
		}

		@Override
		public int hashCode() {
			// @formatter:off
            return new HashCodeBuilder()
						.append(reqCtx.getDocId())
						.append(EnumHasher.INSTANCE.hash(reqCtx.getDirection()))
						.append(EnumHasher.INSTANCE.hash(reqCtx.getTraverseStrategy()))
						.append(CollectionHasher.INSTANCE.hash(reqCtx.getRelationships()))
						.append(EnumHasher.INSTANCE.hash(reqCtx.getNodeType()))
						.append(reqCtx.getLimit())
						.append(reqCtx.getIncludeConfigs())
						.append(reqCtx.getIncludeDocumentSummaries())
						.append(reqCtx.getProjection())
						.append(reqCtx.getMaxDocPopulation())
						.append(reqCtx.getActiveOnly())
						.append(reqCtx.getIsPreview())
						.build();
			// @formatter:on
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof CachedTaxeneRelationService.CacheKey)) return false;

			CachedTaxeneRelationService.CacheKey other = (CachedTaxeneRelationService.CacheKey) obj;
			// @formatter:off
			return new EqualsBuilder()
					.append(reqCtx.getDocId(), other.reqCtx.getDocId())
					.append(reqCtx.getDirection(), other.reqCtx.getDirection())
					.append(reqCtx.getTraverseStrategy(), other.reqCtx.getTraverseStrategy())
					.append(reqCtx.getRelationships(), other.reqCtx.getRelationships())
					.append(reqCtx.getNodeType(), other.reqCtx.getNodeType())
					.append(reqCtx.getLimit(), other.reqCtx.getLimit())
					.append(reqCtx.getIncludeConfigs(), other.reqCtx.getIncludeConfigs())
					.append(reqCtx.getIncludeDocumentSummaries(), other.reqCtx.getIncludeDocumentSummaries())
					.append(reqCtx.getProjection(), other.reqCtx.getProjection())
					.append(reqCtx.getMaxDocPopulation(), other.reqCtx.getMaxDocPopulation())
					.append(reqCtx.getActiveOnly(), other.reqCtx.getActiveOnly())
					.append(reqCtx.getIsPreview(), other.reqCtx.getIsPreview())
					.build();
			// @formatter:on
		}
	}

}
