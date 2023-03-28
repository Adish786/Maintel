package com.about.mantle.model.services.impl;

import javax.ws.rs.client.WebTarget;

import com.about.hippodrome.models.request.HttpMethod;
import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.responses.TaxeneExResponse;
import com.about.mantle.model.services.TaxeneRelationService;
import com.about.mantle.model.services.client.ServiceClientUtils.QueryParamEncoder;

public class TaxeneRelationServiceImpl extends AbstractHttpServiceClient implements TaxeneRelationService {
	public static final String SELENE_TAXENE_PATH = "taxene";
	public static final String SELENE_TAXENE_TRAVERSE_PATH = "traverse";

	public TaxeneRelationServiceImpl(HttpServiceClientConfig httpServiceClientConfig) {
		super(httpServiceClientConfig);
	}

	@Override
	public TaxeneNodeEx traverse(TaxeneTraverseRequestContext reqCtx) {
		TaxeneExResponse response = traverse(reqCtx, TaxeneExResponse.class);
		return response == null ? null : response.getData();
	}

	private <T extends BaseResponse<?>> T traverse(TaxeneTraverseRequestContext reqCtx, Class<T> bindToTarget) {
		if (reqCtx == null) {
			throw new NullPointerException("TaxeneTraverseRequestContext can not be null");
		}

		WebTarget webTarget = baseTarget.path(SELENE_TAXENE_PATH).path(SELENE_TAXENE_TRAVERSE_PATH)
				.path(reqCtx.getDocId().toString());

		webTarget = webTarget.queryParam("direction", reqCtx.getDirection().name());

		webTarget = webTarget.queryParam("traverseStrategy", reqCtx.getTraverseStrategy().name());

		for (String r : reqCtx.getRelationships()) {
			webTarget = webTarget.queryParam("relationship", r);
		}

		if (reqCtx.getNodeType() != null) {
			webTarget = webTarget.queryParam("nodeType", reqCtx.getNodeType().name());
		}

		webTarget = webTarget.queryParam("limit", reqCtx.getLimit());

		webTarget = webTarget.queryParam("includeConfigs", reqCtx.getIncludeConfigs());

		webTarget = webTarget.queryParam("includeDocumentSummaries", reqCtx.getIncludeDocumentSummaries());

		webTarget = webTarget.queryParam("activeOnly", reqCtx.getActiveOnly());

		if (reqCtx.getProjection() != null) {
			webTarget = webTarget.queryParam("projection", QueryParamEncoder.encode(reqCtx.getProjection()));
		}

		if (reqCtx.getMaxDocPopulation() != null) {
			webTarget = webTarget.queryParam("maxDocPopulation", reqCtx.getMaxDocPopulation());
		}

		T response = readResponse(webTarget, bindToTarget, HttpMethod.GET, super.getConfig().getMediaType());

		return response;
	}

}
