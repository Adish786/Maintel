package com.about.mantle.model.services;

import java.util.Map;

import com.about.mantle.model.services.rtb.RtbRequestContext;
import com.about.mantle.model.services.rtb.RtbResult;

/**
 * Service responsible for server-side RTB.
 */
public interface RtbService {

	/**
	 * Get bids from partners.
	 * @param ctx
	 * @return map of partner name to result object
	 */
	Map<String, RtbResult> getBids(RtbRequestContext ctx);

}
