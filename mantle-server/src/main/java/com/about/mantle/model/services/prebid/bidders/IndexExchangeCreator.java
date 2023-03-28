package com.about.mantle.model.services.prebid.bidders;

import static com.about.mantle.model.services.prebid.csv.PrebidCsvLine.SLOT_ID;

import java.util.HashMap;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

/**
 * Index Exchange
 * https://docs.prebid.org/dev-docs/bidders/ix
 */
public class IndexExchangeCreator extends AbstractBidderCreator {

	public IndexExchangeCreator() {
		super("Index Exchange", "ix");
	}

	@Override
	public void validate(PrebidCsvLine prebidCsvLine) {
		assert name().equals(prebidCsvLine.getBidder());
		validateNotMissing(prebidCsvLine, SLOT_ID, prebidCsvLine.getSlotId());
	}

	@Override
	protected Bidder prevalidatedCreate(PrebidCsvLine prebidCsvLine) {

		HashMap<String, Object> bidderItemMap = new HashMap<>();
		
		bidderItemMap.put("siteId", prebidCsvLine.getSlotId());
		bidderItemMap.put("type", prebidCsvLine.getMediaType());

		if(prebidCsvLine.getSize() != null) {
			bidderItemMap.put("size", prebidCsvLine.getSize());
		}

		return new Bidder(code(), ImmutableMap.copyOf(bidderItemMap));
	}

}