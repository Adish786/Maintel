package com.about.mantle.model.services.prebid.bidders;

import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.ACCOUNT_ID;
import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.DOMAIN_ID;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

/**
 * TTD
 * https://docs.prebid.org/dev-docs/bidders/ttd.html
 */
public class TtdCreator extends AbstractBidderCreator {

	public TtdCreator() {
		super("TTD", "ttd");
	}

	@Override
	public void validate(PrebidCsvLine prebidCsvLine) {
		assert name().equals(prebidCsvLine.getBidder());
		validateNotMissing(prebidCsvLine, ACCOUNT_ID, prebidCsvLine.getAccountId());
		validateNotMissing(prebidCsvLine, DOMAIN_ID, prebidCsvLine.getDomainId());
	}

	@Override
	protected Bidder prevalidatedCreate(PrebidCsvLine prebidCsvLine) {
		return new Bidder(code(), ImmutableMap.of(
			"supplySourceId", prebidCsvLine.getDomainId(),
			"publisherId", prebidCsvLine.getAccountId(),
			"type", prebidCsvLine.getMediaType()
		));
	}

}