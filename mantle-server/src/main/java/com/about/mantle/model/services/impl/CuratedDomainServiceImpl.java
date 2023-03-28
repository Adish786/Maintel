package com.about.mantle.model.services.impl;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.responses.CuratedDomainListResponse;
import com.about.mantle.model.services.CuratedDomainService;
import jersey.repackaged.com.google.common.collect.Sets;

import javax.ws.rs.client.WebTarget;
import java.util.Set;

public class CuratedDomainServiceImpl extends AbstractHttpServiceClient implements CuratedDomainService {

	private static final String SELENE_CURATED_DOMAIN_PATH = "/curateddomain";
	private static final String SELENE_BY_SOURCE_PATH = "/bysource";

	public CuratedDomainServiceImpl(HttpServiceClientConfig httpServiceClientConfig) {
		super(httpServiceClientConfig);
		this.baseTarget = baseTarget.path(SELENE_CURATED_DOMAIN_PATH).path(SELENE_BY_SOURCE_PATH);
	}
	
	@Override
	public Set<String> getDomainsBySource(String type, String subType) {
		CuratedDomainListResponse response = getDomainsBySource(type, subType, CuratedDomainListResponse.class);
		
		Set<String> results = response.getData() == null ? Sets.newHashSetWithExpectedSize(1) : Sets.newHashSet(response.getData().getList());
		return results;
	}
	
	private <T extends BaseResponse<?>> T getDomainsBySource(String type, String subType, Class<T> bindToTarget) {
		WebTarget webTarget = baseTarget.queryParam("type", type).queryParam("subType", subType);

		T response = readResponse(webTarget, bindToTarget);
		return response;
	}
}
