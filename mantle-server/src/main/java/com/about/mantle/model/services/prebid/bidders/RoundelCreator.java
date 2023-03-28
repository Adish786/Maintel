package com.about.mantle.model.services.prebid.bidders;

import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.ACCOUNT_ID;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

/**
 * Roundel
 * https://docs.prebid.org/dev-docs/bidders/roundel.html
 */
public class RoundelCreator extends AbstractBidderCreator {

	public RoundelCreator() {
		super("Roundel", "roundel");
	}

	@Override
	public void validate(PrebidCsvLine prebidCsvLine) {
		assert name().equals(prebidCsvLine.getBidder());
		validateNotMissing(prebidCsvLine, ACCOUNT_ID, prebidCsvLine.getAccountId());
	}

	@Override
	protected Bidder prevalidatedCreate(PrebidCsvLine prebidCsvLine) {
		return new Bidder(code(), ImmutableMap.of(
			"siteId", Integer.parseInt(prebidCsvLine.getAccountId()),
			"type", prebidCsvLine.getMediaType()
		));
	}

}