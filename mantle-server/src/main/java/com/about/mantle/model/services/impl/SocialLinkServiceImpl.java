package com.about.mantle.model.services.impl;

import javax.ws.rs.client.WebTarget;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.services.SocialLinkService;

public class SocialLinkServiceImpl extends AbstractHttpServiceClient implements SocialLinkService {

	public static final String SELENE_PATH = "/sociallink";
	
	public SocialLinkServiceImpl(HttpServiceClientConfig httpServiceClientConfig) {
		super(httpServiceClientConfig);
	}

	@Override
	public SocialLinkResponse getSocialLinks(String url, SocialNetwork network, DeviceCategory device) {

		return getSocialLinks(url, network, device, SocialLinkResponse.class);
	}
	
	
	private <T extends BaseResponse<?>> T getSocialLinks(String url, SocialNetwork network, DeviceCategory device,
			Class<T> bindToTarget) {

		WebTarget webTarget = baseTarget.path(SELENE_PATH);

		webTarget = webTarget.queryParam("url", url);
		if (network != null) webTarget = webTarget.queryParam("network", network);
		if (device != null) webTarget = webTarget.queryParam("device", device);

		T response = readResponse(webTarget, bindToTarget);
		return response;
	}
}
