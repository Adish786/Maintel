package com.about.mantle.model.services.impl;

import javax.ws.rs.client.WebTarget;

import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.DeionSearchFullResultEx;
import com.about.mantle.model.extended.responses.DeionSearchExFullResponse;
import com.about.mantle.model.services.DeionSearchFullDocumentService;
import com.about.mantle.model.services.DeionSearchService;

public class DeionSearchFullDocumentServiceImpl extends DeionSearchServiceBase implements DeionSearchFullDocumentService {

	private static final String SELENE_PATH_FULL_DOCUMENT = "/deion/search/fulldocument";

	public DeionSearchFullDocumentServiceImpl(HttpServiceClientConfig httpClientConfig) {
		super(httpClientConfig);
	}

	public DeionSearchFullResultEx searchFullResults(DeionSearchService.DeionSearchRequestContext requestContext) {
		WebTarget webTarget = createWebTarget(requestContext, SELENE_PATH_FULL_DOCUMENT);
		return readResponse(webTarget, DeionSearchExFullResponse.class).getData();
	}

}
