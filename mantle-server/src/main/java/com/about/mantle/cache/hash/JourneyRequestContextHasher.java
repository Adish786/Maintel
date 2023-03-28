package com.about.mantle.cache.hash;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.about.globe.core.cache.hash.Hasher;
import com.about.mantle.model.services.JourneyService.JourneyRequestContext;

public class JourneyRequestContextHasher implements Hasher{
	public static final JourneyRequestContextHasher INSTANCE = new JourneyRequestContextHasher();
	public JourneyRequestContextHasher(){}
	
	@Override
	public int hash(Object object) {
		if (object == null) return 0;
		JourneyRequestContext ctx = (JourneyRequestContext)object;
		// @formatter:off
		return new HashCodeBuilder()
				.append(ctx.getDocId())
				.append(ctx.getIncludeDocumentSummaries())
				.append(ctx.getProjection())
				.build();
		// @formatter:on
	}
}
