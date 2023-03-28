package com.about.mantle.cache.hash;

import com.about.globe.core.cache.hash.CollectionHasher;
import com.about.globe.core.cache.hash.EnumHasher;
import com.about.globe.core.cache.hash.Hasher;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext.DeionSearchKey;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DeionSearchRequestContextHasher implements Hasher {
	public static final DeionSearchRequestContextHasher INSTANCE = new DeionSearchRequestContextHasher();

	public DeionSearchRequestContextHasher() {
	}

	@Override
	public int hash(Object object) {
		if (object == null) return 0;
		DeionSearchRequestContext ctx = (DeionSearchRequestContext) object;
		// @formatter:off
		HashCodeBuilder hashBuilder = new HashCodeBuilder()
				.append(ctx.getIncludeDocumentSummaries())
				.append(ctx.getProjection());
		if (ctx.getDeionSearchKey() != null) {
			DeionSearchKey key = ctx.getDeionSearchKey();
			hashBuilder.append(key.getBoost())
				.append(key.getDomain())
				.append(CollectionHasher.INSTANCE.hash(key.getFacetFields()))
				.append(CollectionHasher.INSTANCE.hash(key.getFields()))
				.append(CollectionHasher.INSTANCE.hash(key.getFilterQueries()))
				.append(key.getLimit())
				.append(key.getOffset())
				.append(key.getQuery())
				.append(EnumHasher.INSTANCE.hash(key.getSolrIndexType()))
				.append(key.getSort())
				.append(CollectionHasher.INSTANCE.hash(key.getTraversalExcludedDocId()))
				.append(key.getTraversalRelationships())
				.append(key.getTraversalStartDocId());
		} else {
			hashBuilder.append(0);
		}
		return hashBuilder.build();
		// @formatter:on
	}
}
