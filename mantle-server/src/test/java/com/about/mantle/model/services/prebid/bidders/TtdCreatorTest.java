package com.about.mantle.model.services.prebid.bidders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

public class TtdCreatorTest {

	private static TtdCreator TTD = new TtdCreator();
	private PrebidCsvLine prebidCsvLine;

	@Before
	public void resetLine() {
		prebidCsvLine = new PrebidCsvLine();
		prebidCsvLine.setBidder("TTD");
		prebidCsvLine.setAccountId("1");
		prebidCsvLine.setDomainId("dotdash");
	}

	@Test
	public void testMissingAccountId() {
		prebidCsvLine.setAccountId("");
		try {
			TTD.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testMissingDomainId() {
		prebidCsvLine.setDomainId("");
		try {
			TTD.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreate() {
		Bidder bidder = TTD.create(prebidCsvLine);
		assertEquals("ttd", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"supplySourceId", "dotdash",			
			"publisherId", "1",
			"type", "display"
		);
		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testCreateInstream() {
		prebidCsvLine.setMediaType("instream");
		Bidder bidder = TTD.create(prebidCsvLine);
		assertEquals("ttd", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"supplySourceId", "dotdash",
			"publisherId", "1",
			"type", "instream"
		);
		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testValidate() {
		try {
			TTD.validate(prebidCsvLine);
		} catch (Exception e) {
			fail("unexpected throw");
		}
	}

}