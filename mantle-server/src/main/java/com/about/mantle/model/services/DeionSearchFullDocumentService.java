package com.about.mantle.model.services;

import com.about.mantle.model.extended.DeionSearchFullResultEx;

public interface DeionSearchFullDocumentService {

	DeionSearchFullResultEx searchFullResults(DeionSearchService.DeionSearchRequestContext requestContext);
	
}
