package com.about.mantle.model.services.prebid.bidders;

import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.SLOT_ID;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

/**
 * AppNexus
 * https://docs.prebid.org/dev-docs/bidders/appnexus.html
 */
public class AppNexusCreator extends AbstractBidderCreator {

	public AppNexusCreator() {
		super("AppNexus", "appnexus");
	}

	@Override
	public void validate(PrebidCsvLine prebidCsvLine) {
		assert name().equals(prebidCsvLine.getBidder());
		validateNotMissing(prebidCsvLine, SLOT_ID, prebidCsvLine.getSlotId());
	}

	@Override
	protected Bidder prevalidatedCreate(PrebidCsvLine prebidCsvLine) {
		return new Bidder(code(), ImmutableMap.of(
			"placementId", prebidCsvLine.getSlotId(),
			"type", prebidCsvLine.getMediaType()
		));
	}

}