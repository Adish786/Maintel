package com.about.mantle.model.services.prebid.bidders;

import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.ACCOUNT_ID;
import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.DOMAIN_ID;
import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.SLOT_ID;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

/**
 * Rubicon Project
 * https://docs.prebid.org/dev-docs/bidders/rubicon.html
 * Special implementation for Programmatic Guaranteed
 */
public class RubiconPGCreator extends AbstractBidderCreator {
    
	public RubiconPGCreator() {
		super("RubiconPG", "pgRubicon");
	}

	@Override
	public void validate(PrebidCsvLine prebidCsvLine) {
		assert name().equals(prebidCsvLine.getBidder());
		validateNotMissing(prebidCsvLine, ACCOUNT_ID, prebidCsvLine.getAccountId());
		validateNotMissing(prebidCsvLine, DOMAIN_ID, prebidCsvLine.getDomainId());
		validateNotMissing(prebidCsvLine, SLOT_ID, prebidCsvLine.getSlotId());
	}

	@Override
	protected Bidder prevalidatedCreate(PrebidCsvLine prebidCsvLine) {
		return new Bidder(code(), ImmutableMap.of(
			"accountId", Integer.parseInt(prebidCsvLine.getAccountId()),
			"siteId", Integer.parseInt(prebidCsvLine.getDomainId()),
            "zoneId", Integer.parseInt(prebidCsvLine.getSlotId()),
            "dealsonly", true
		));
	}
}
