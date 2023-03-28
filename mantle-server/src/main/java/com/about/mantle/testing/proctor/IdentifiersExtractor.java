package com.about.mantle.testing.proctor;

import com.about.globe.core.http.RequestContext;
import com.indeed.proctor.common.Identifiers;

public interface IdentifiersExtractor {

	Identifiers extract(RequestContext requestContext);
	
}
