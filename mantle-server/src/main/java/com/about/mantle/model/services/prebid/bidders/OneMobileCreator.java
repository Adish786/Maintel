package com.about.mantle.model.services.prebid.bidders;

import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.DOMAIN_ID;
import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.SLOT_ID;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

/**
 * ONE by AOL Mobile
 * https://docs.prebid.org/dev-docs/bidders/onemobile
 */
public class OneMobileCreator extends AbstractBidderCreator {

	public OneMobileCreator() {
		super("OneMobile", "onemobile");
	}

	@Override
	public void validate(PrebidCsvLine prebidCsvLine) {
		assert name().equals(prebidCsvLine.getBidder());
		validateNotMissing(prebidCsvLine, DOMAIN_ID, prebidCsvLine.getDomainId());
		validateNotMissing(prebidCsvLine, SLOT_ID, prebidCsvLine.getSlotId());
	}

	@Override
	protected Bidder prevalidatedCreate(PrebidCsvLine prebidCsvLine) {
		return new Bidder(code(), ImmutableMap.of(
			"dcn", prebidCsvLine.getDomainId(),
			"pos", prebidCsvLine.getSlotId()
		));
	}

}