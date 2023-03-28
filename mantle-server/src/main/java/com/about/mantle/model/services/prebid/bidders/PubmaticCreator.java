package com.about.mantle.model.services.prebid.bidders;

import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.ACCOUNT_ID;
import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.SLOT_ID;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

/**
 * PubmaticCreator
 * https://docs.prebid.org/dev-docs/bidders/pubmatic.html
 */
public class PubmaticCreator extends AbstractBidderCreator {

	public PubmaticCreator() {
		super("Pubmatic", "pubmatic");
	}

	@Override
	public void validate(PrebidCsvLine prebidCsvLine) {
		assert name().equals(prebidCsvLine.getBidder());
		validateNotMissing(prebidCsvLine, ACCOUNT_ID, prebidCsvLine.getAccountId());
		validateNotMissing(prebidCsvLine, SLOT_ID, prebidCsvLine.getSlotId());
	}

	@Override
	protected Bidder prevalidatedCreate(PrebidCsvLine prebidCsvLine) {
		return new Bidder(code(), ImmutableMap.of(
			"publisherId", prebidCsvLine.getAccountId(),
			"adSlot", prebidCsvLine.getSlotId(),
			"type", prebidCsvLine.getMediaType()
		));
	}

}