package com.about.mantle.cache.hash;

import com.about.globe.core.cache.hash.CollectionHasher;
import com.about.globe.core.cache.hash.EnumCollectionHasher;
import com.about.globe.core.cache.hash.Hasher;
import com.about.mantle.model.services.ArticleService.ArticleFilterRequest;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ArticleFilterRequestHasher implements Hasher {
	public static final ArticleFilterRequestHasher INSTANCE = new ArticleFilterRequestHasher();

	public ArticleFilterRequestHasher() {
	}

	@Override
	public int hash(Object object) {
		if (object == null) return 0;
		ArticleFilterRequest articleFilterRequest = (ArticleFilterRequest) object;
		// @formatter:off
		return new HashCodeBuilder()
				.append(EnumCollectionHasher.INSTANCE.hash(articleFilterRequest.getTemplateTypes()))
				.append(CollectionHasher.INSTANCE.hash(articleFilterRequest.getExcludeKeys()))
				.append(CollectionHasher.INSTANCE.hash(articleFilterRequest.getExcludeDocIds()))
				.build();
		// @formatter:on
	}
}
