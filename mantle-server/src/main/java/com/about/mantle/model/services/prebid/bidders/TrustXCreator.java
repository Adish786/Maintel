package com.about.mantle.model.services.prebid.bidders;

import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.SLOT_ID;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

/**
 * Trust X
 * https://docs.prebid.org/dev-docs/bidders/trustx.html
 */
public class TrustXCreator extends AbstractBidderCreator {

	public TrustXCreator() {
		super("TrustX", "trustx");
	}

	@Override
	public void validate(PrebidCsvLine prebidCsvLine) {
		assert name().equals(prebidCsvLine.getBidder());
		validateNotMissing(prebidCsvLine, SLOT_ID, prebidCsvLine.getSlotId());
	}

	@Override
	protected Bidder prevalidatedCreate(PrebidCsvLine prebidCsvLine) {
		return new Bidder(code(), ImmutableMap.of(
			"uid", prebidCsvLine.getSlotId(),
			"useNewFormat", true,
			"type", prebidCsvLine.getMediaType()
		));
	}

}