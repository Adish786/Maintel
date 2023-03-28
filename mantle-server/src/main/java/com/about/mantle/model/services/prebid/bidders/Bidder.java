package com.about.mantle.model.services.prebid.bidders;

import java.util.Map;

/**
 * Represents a Prebid bidder.
 * http://prebid.org/dev-docs/bidders.html
 *
 * To be used as an element in the Prebid AdUnit's `bids` array.
 * http://prebid.org/dev-docs/adunit-reference.html#adunitbids
 */
public class Bidder {

	private final String code;
	private final Map<String, Object> params;

	public Bidder(String code, Map<String, Object> params) {
		this.code = code;
		this.params = params;
	}

	public String getCode() {
		return code;
	}

	public Map<String, Object> getParams() {
		return params;
	}

}