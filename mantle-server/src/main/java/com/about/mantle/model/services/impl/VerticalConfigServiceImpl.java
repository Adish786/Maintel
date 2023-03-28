package com.about.mantle.model.services.impl;

import java.util.Map;

import javax.ws.rs.client.WebTarget;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.responses.VerticalConfigResponse;
import com.about.mantle.model.services.VerticalConfigService;

public class VerticalConfigServiceImpl extends AbstractHttpServiceClient implements VerticalConfigService {

	private static final String SELENE_VERTICALCONFIG_PATH = "/verticalconfig";
	
	public VerticalConfigServiceImpl(HttpServiceClientConfig config) {
		super(config);
		this.baseTarget = baseTarget.path(SELENE_VERTICALCONFIG_PATH);
	}
	@Override
	public Map<String, ?> getVerticalConfig(String vertical) {
		VerticalConfigResponse response = getVerticalConfigByVertical(vertical, VerticalConfigResponse.class);
		
		return response.getData();
	}
	
	private <T extends BaseResponse<?>> T getVerticalConfigByVertical(String vertical, Class<T> bindToTarget) {
		WebTarget webTarget = baseTarget.path(vertical);

		T response = readResponse(webTarget, bindToTarget);
		return response;
	}

}
