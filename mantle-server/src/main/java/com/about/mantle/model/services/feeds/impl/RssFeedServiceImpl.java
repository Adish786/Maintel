package com.about.mantle.model.services.feeds.impl;

import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.services.feeds.api.RssFeedService;
import com.about.mantle.model.services.feeds.response.RssFeedSearchResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.client.WebTarget;
import java.util.List;
import java.util.Map;

public class RssFeedServiceImpl extends AbstractHttpServiceClient implements RssFeedService {

	public static final String SELENE_PATH = "/rssfeed/build";

	public RssFeedServiceImpl(HttpServiceClientConfig httpClientConfig) {
		super(httpClientConfig);
	}

	public RssFeedSearchResponse search(String query, List<String> filterQueries, Boolean mapExchanges, Integer limit,
			Integer offset, Boolean includeFullDocument, String sort, Map<String, Object> additionalQueryParams) {
		
		WebTarget webTarget = baseTarget.path(SELENE_PATH);

		if (StringUtils.isNotBlank(query)) {
			webTarget = webTarget.queryParam("query", "{q}").resolveTemplate("q", query);
		}

		if (CollectionUtils.isNotEmpty(filterQueries)) {
			for (String aFilterQuery : filterQueries) {
				if (StringUtils.isNotEmpty(aFilterQuery))
					webTarget = webTarget.queryParam("filterQuery", aFilterQuery);
			}
		}

		if (mapExchanges != null) {
			webTarget = webTarget.queryParam("mapExchanges", mapExchanges);
		}

		// default to SEARCH index in SOLR as CMS index can have unpublished docs e.g.
		// DRAFT, PREVIEW etc.
		webTarget = webTarget.queryParam("solrIndexType", "SEARCH");

		if (limit != null) {
			webTarget = webTarget.queryParam("limit", limit);
		}

		if (offset != null) {
			webTarget = webTarget.queryParam("offset", offset);
		}

		if (includeFullDocument != null) {
			webTarget = webTarget.queryParam("includeFullDocument", includeFullDocument);
		}

		if (StringUtils.isNotBlank(sort)) {
			webTarget = webTarget.queryParam("sort", sort);
		}

		for (String key : additionalQueryParams.keySet()) {
			Object val = additionalQueryParams.get(key);
			if (val instanceof String) {
				webTarget = webTarget.queryParam(key, val);
			} else if (val instanceof List) {
				webTarget = webTarget.queryParam(key, ((List) val).toArray());
			}
		}
		return readResponse(webTarget, RssFeedSearchResponse.class);
	}
}
