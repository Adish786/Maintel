package com.about.mantle.model.services.prebid.bidders;

import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.ACCOUNT_ID;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

/**
 * Criteo
 * https://docs.prebid.org/dev-docs/bidders/criteo.html
 */
public class CriteoCreator extends AbstractBidderCreator {

	public CriteoCreator() {
		super("Criteo", "criteo");
	}

	@Override
	public void validate(PrebidCsvLine prebidCsvLine) {
		assert name().equals(prebidCsvLine.getBidder());
		validateIsNumber(prebidCsvLine, ACCOUNT_ID, prebidCsvLine.getAccountId());
	}

	@Override
	protected Bidder prevalidatedCreate(PrebidCsvLine prebidCsvLine) {
		return new Bidder(code(), ImmutableMap.of(
			"networkId", Integer.valueOf(prebidCsvLine.getAccountId())
		));
	}

}