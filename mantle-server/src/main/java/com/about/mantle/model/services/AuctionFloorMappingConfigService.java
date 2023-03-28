package com.about.mantle.model.services;

import com.about.mantle.model.extended.AuctionFloorConfig;

/**
 * Used to provide floor values for ad slots.
 */
public interface AuctionFloorMappingConfigService {

	// returns null if exempt from auction floor pricing
	AuctionFloorConfig getAuctionFloorConfig();

}
