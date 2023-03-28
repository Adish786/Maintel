package com.about.mantle.testing.proctor;

import com.about.globe.core.http.RequestContext;
import com.indeed.proctor.common.Identifiers;
import com.indeed.proctor.common.model.TestType;

public class DefaultIdentifiersExtractor implements IdentifiersExtractor {

	@Override
	public Identifiers extract(RequestContext requestContext) {
		return Identifiers.of(TestType.USER, requestContext.getUserId(), TestType.PAGE, requestContext.getSessionId());
	}

}
