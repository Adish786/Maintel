package com.about.mantle.model.services.impl;

import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.extended.responses.DeionSearchExResponse;
import com.about.mantle.model.services.DeionSearchService;

public class DeionSearchServiceImpl extends DeionSearchServiceBase implements DeionSearchService {

	private static final Logger logger = LoggerFactory.getLogger(DeionSearchServiceImpl.class);

	private static final String SELENE_PATH = "/deion/search";

	public DeionSearchServiceImpl(HttpServiceClientConfig httpClientConfig) {
		super(httpClientConfig);
	}

	@Override
	public DeionSearchResultEx search(DeionSearchRequestContext requestContext) {
		DeionSearchResultEx answer = null;
		WebTarget webTarget = createWebTarget(requestContext, SELENE_PATH);
		try {
			DeionSearchExResponse response = readResponse(webTarget, DeionSearchExResponse.class);
			if (response.getData() == null) {
				logger.error("Deion search response missing data: {}", response.getStatus() == null ? "unknown" : response.getStatus().toString());
			} else {
				answer = response.getData();
			}
		} catch (Exception e) {
			logger.error("Deion search request failed", e);
		}
		// We can't allow a null value to propagate here because it has the potential to get cached.
		// The risk of caching a null value here is too great because some clients of deion search,
		// e.g. sitemaps, legacy urls, etc. are mission critical and may cause the app to consistently
		// fail startup until redis is flushed to remove the bad value.
		if (answer == null) {
			throw new GlobeException("Deion search is null");
		}
		return answer;
	}
}
