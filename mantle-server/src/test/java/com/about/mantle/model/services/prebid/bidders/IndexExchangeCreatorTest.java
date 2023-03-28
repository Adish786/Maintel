package com.about.mantle.model.services.prebid.bidders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

public class IndexExchangeCreatorTest {

	private static IndexExchangeCreator indexExchange = new IndexExchangeCreator();
	private PrebidCsvLine prebidCsvLine;

	@Before
	public void resetLine() {
		prebidCsvLine = new PrebidCsvLine();
		prebidCsvLine.setBidder("Index Exchange");
		prebidCsvLine.setSlotId("1337");
		prebidCsvLine.setSize(ImmutableList.of(300, 250));
	}

	@Test
	public void testMissingSize() {
		prebidCsvLine.setSize(null);
		try {
			indexExchange.validate(prebidCsvLine);
		} catch (Exception e) {
			fail("unexpected throw");
		}
	}

	@Test
	public void testMissingSlotId() {
		prebidCsvLine.setSlotId("");
		try {
			indexExchange.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreate() {
		Bidder bidder = indexExchange.create(prebidCsvLine);
		assertEquals("ix", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"siteId", "1337",
			"size", ImmutableList.of(300, 250),
			"type", "display"
		);
		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testCreateInstream() {
		prebidCsvLine.setMediaType("instream");
		prebidCsvLine.setSize(null);

		Bidder bidder = indexExchange.create(prebidCsvLine);
		assertEquals("ix", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"siteId", "1337",
			"type", "instream"
		);
		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testValidate() {
		try {
			indexExchange.validate(prebidCsvLine);
		} catch (Exception e) {
			fail("unexpected throw");
		}
	}

}