package com.about.mantle.model.services.prebid.bidders;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

/**
 * Criteo
 * https://docs.prebid.org/dev-docs/bidders/criteo.html
 * Custom bidding values from Criteo for Programmatic Guaranteed
 */
public class CriteoPGCreator extends AbstractBidderCreator {

	public CriteoPGCreator() {
		super("CriteoPG", "criteopg");
	
	}

	@Override
	public void validate(PrebidCsvLine prebidCsvLine) {
		assert name().equals(prebidCsvLine.getBidder());
	}

	@Override
	protected Bidder prevalidatedCreate(PrebidCsvLine prebidCsvLine) {
		return new Bidder(code(), ImmutableMap.of());
	}

}