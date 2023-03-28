package com.about.mantle.model.services.impl;

import javax.ws.rs.client.WebTarget;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.SuggestionSearchResultItemEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.responses.SuggestionExResponse;
import com.about.mantle.model.services.SuggestionService;

public class SuggestionServiceImpl extends AbstractHttpServiceClient implements SuggestionService {

	public static final String SELENE_PATH = "/suggestion/search";
	private final int MAX_QUERY_SIZE = 100;
	
	public SuggestionServiceImpl(HttpServiceClientConfig httpServiceClientConfig) {
		super(httpServiceClientConfig);
	}
	
	@Override
	public SliceableListEx<SuggestionSearchResultItemEx> search(SuggestionSearchRequestContext requestContext) {
		return search(requestContext, SuggestionExResponse.class).getData();
	}

	public <T extends BaseResponse<?>> T search(SuggestionSearchRequestContext requestContext, Class<T> bindToTarget) {
		if (requestContext == null) {
			throw new IllegalArgumentException("search suggestion: invalid operation");
		}

		WebTarget webTarget = baseTarget.path(SELENE_PATH);
		String query = requestContext.getQuery();

		if(query.length() > MAX_QUERY_SIZE) {
			query= query.substring(0,MAX_QUERY_SIZE - 1);
		}

		if (StringUtils.isEmpty(query)) {
			throw new IllegalArgumentException("search suggestion: query is required");
		}
		webTarget = webTarget.queryParam("query", "{q}").resolveTemplate("q", query);

		if (CollectionUtils.isNotEmpty(requestContext.getFilterQueries())) {
			for (String s : requestContext.getFilterQueries()) {
				if (StringUtils.isNotEmpty(s))
					webTarget = webTarget.queryParam("filterQuery", "{fq}").resolveTemplate("fq", s);
			}
		}

		if (requestContext.getSort() != null) {
			webTarget = webTarget.queryParam("sort", requestContext.getSort());
		}
		
		if (requestContext.getOffset() != null) {
			webTarget = webTarget.queryParam("offset", requestContext.getOffset());
		}

		if (requestContext.getLimit() != null) {
			webTarget = webTarget.queryParam("limit", requestContext.getLimit());
		}

		return readResponse(webTarget, bindToTarget);
	}
	
}
