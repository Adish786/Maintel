package com.about.mantle.model.services.impl;

import java.util.Set;

import javax.ws.rs.client.WebTarget;

import org.apache.commons.collections4.CollectionUtils;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.responses.SearchExPageResponse;
import com.about.mantle.model.services.SearchService;

public class SearchServiceImpl extends AbstractHttpServiceClient implements SearchService {

	public static final String SELENE_PATH = "/search";
	
	public SearchServiceImpl(HttpServiceClientConfig httpServiceClientConfig) {
		super(httpServiceClientConfig);
	}

	@Override
	public SearchExPageResponse getSearchResults(String term, Integer offset, Integer limit, AlgorithmType algorithmType, 
			String projection, Set<String> tags) {

		return getSearchResults(term, false, offset, limit, algorithmType, projection, tags);
	}

	@Override
	public SearchExPageResponse getSearchResults(String term, Boolean allowFuzzy, Integer offset, Integer limit, AlgorithmType algorithmType,
												 String projection, Set<String> tags) {

		SearchRequestContext.Builder searchRequestContextBuilder = new SearchRequestContext.Builder().setTerm(term).setAllowFuzzy(allowFuzzy)
				.setOffset(offset).setLimit(limit).setAlgorithmType(algorithmType).setProjection(projection).setTags(tags);

		return getSearch(searchRequestContextBuilder.build(), SearchExPageResponse.class);
	}

	private <T extends BaseResponse<?>> T getSearch(SearchRequestContext reqCtx, Class<T> bindToTarget) {

		WebTarget webTarget = baseTarget.path(SELENE_PATH);

		if (reqCtx == null || reqCtx.getTerm() == null) {
			throw new IllegalArgumentException(
					"HttpSearchServiceClient.getSearch(): term must be set in the request");
		}

		webTarget = webTarget.queryParam("term", reqCtx.getTerm());
		if (reqCtx.getAllowFuzzy() != null) {
			webTarget = webTarget.queryParam("allowFuzzy", reqCtx.getAllowFuzzy());
		}
		if (reqCtx.getOffset() != null) {
			webTarget = webTarget.queryParam("offset", reqCtx.getOffset());
		}
		if (reqCtx.getLimit() != null) {
			webTarget = webTarget.queryParam("limit", reqCtx.getLimit());
		}
		if (reqCtx.getAlgorithmType() != null) {
			webTarget = webTarget.queryParam("algoType", reqCtx.getAlgorithmType());
		}

		if (reqCtx.getProjection() != null) {
			webTarget = webTarget.queryParam("projection", "{p}").resolveTemplate("p",
					reqCtx.getProjection());
		}
		
		if (CollectionUtils.isNotEmpty(reqCtx.getTags())) {
			for (String tag : reqCtx.getTags()) {
				webTarget = webTarget.queryParam("tag", tag);
			}
		}

		T response = readResponse(webTarget, bindToTarget);
		return response;
	}
}
