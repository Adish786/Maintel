package com.about.mantle.model.services.impl;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.attribution.Attribution;
import com.about.mantle.model.extended.attribution.AttributionType;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.responses.AttributionListResponse;
import com.about.mantle.model.extended.responses.AttributionResponse;
import com.about.mantle.model.extended.responses.AttributionTypesResponse;
import com.about.mantle.model.services.AttributionService;

import javax.ws.rs.client.WebTarget;
import java.util.Map;

public class AttributionServiceImpl extends AbstractHttpServiceClient implements AttributionService {

	public static final String SELENE_ATTRIBUTION_PATH = "/attribution";
	public static final String SELENE_ATTRIBUTION_SEARCH_PATH = "search";
	public static final String SELENE_ATTRIBUTION_TYPES_PATH = "types";

	public AttributionServiceImpl(HttpServiceClientConfig httpServiceClientConfig) {
		super(httpServiceClientConfig);
	}

	@Override
	public Attribution getById(String id) {
		AttributionResponse response = getById(id, AttributionResponse.class);
		return response.getData();
	}

	@Override
	public Map<String, AttributionType> getAttributionTypes() {
		WebTarget webTarget = baseTarget.path(SELENE_ATTRIBUTION_PATH).path(SELENE_ATTRIBUTION_TYPES_PATH);
		AttributionTypesResponse response = readResponse(webTarget, AttributionTypesResponse.class);
		return response.getData();
	}

	private <T extends BaseResponse<?>> T getById(String id, Class<T> bindToTarget) {
		WebTarget webTarget = baseTarget.path(SELENE_ATTRIBUTION_PATH).path(id);

		T response = readResponse(webTarget, bindToTarget);
		return response;
	}

	@Override
	public SliceableListEx<Attribution> getAttributionsByAuthorId(String id) {
		WebTarget webTarget = baseTarget.path(SELENE_ATTRIBUTION_PATH).path(SELENE_ATTRIBUTION_SEARCH_PATH).path(id);
		AttributionListResponse response = readResponse(webTarget, AttributionListResponse.class);
		return response.getData();
	}
}
