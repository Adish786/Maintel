package com.about.mantle.model.services.impl;

import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.AuctionFloorConfig;
import com.about.mantle.model.extended.responses.AuctionFloorConfigResponse;
import com.about.mantle.model.services.AuctionFloorMappingConfigService;

import javax.ws.rs.client.WebTarget;

public class AuctionFloorMappingConfigServiceImpl extends AbstractHttpServiceClient implements AuctionFloorMappingConfigService {
	public static final String SELENE_AUCTION_FLOOR_CONFIG_PATH = "/auction/floor/config";

	public AuctionFloorMappingConfigServiceImpl(HttpServiceClientConfig httpServiceClientConfig) {
		super(httpServiceClientConfig);
	}

	@Override
	public AuctionFloorConfig getAuctionFloorConfig() {
		WebTarget webTarget = baseTarget.path(SELENE_AUCTION_FLOOR_CONFIG_PATH);

		AuctionFloorConfigResponse response = readResponse(webTarget, AuctionFloorConfigResponse.class);

		return response.getData();
	}

}
