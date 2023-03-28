package com.about.mantle.model.services.prebid.bidders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

public class PubmaticCreatorTest {

	private static PubmaticCreator Pubmatic = new PubmaticCreator();
	private PrebidCsvLine prebidCsvLine;

	@Before
	public void resetLine() {
		prebidCsvLine = new PrebidCsvLine();
		prebidCsvLine.setBidder("Pubmatic");
		prebidCsvLine.setAccountId("158139");
		prebidCsvLine.setSlotId("3479358");
	}

	@Test
	public void testMissingAccountId() {
		prebidCsvLine.setAccountId("");
		try {
			Pubmatic.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testMissingSlotId() {
		prebidCsvLine.setSlotId("");
		try {
			Pubmatic.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreate() {
		Bidder bidder = Pubmatic.create(prebidCsvLine);
		assertEquals("pubmatic", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"publisherId", "158139",
			"adSlot", "3479358",
			"type", "display"
		);
		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testCreateInstream() {
		prebidCsvLine.setMediaType("instream");
		Bidder bidder = Pubmatic.create(prebidCsvLine);
		assertEquals("pubmatic", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"publisherId", "158139",
			"adSlot", "3479358",
			"type", "instream"
		);
		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testValidate() {
		try {
			Pubmatic.validate(prebidCsvLine);
		} catch (Exception e) {
			fail("unexpected throw");
		}
	}

}