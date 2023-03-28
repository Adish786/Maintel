package com.about.mantle.model.services.prebid.bidders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

public class TrustXCreatorTest {

	private static TrustXCreator trustX = new TrustXCreator();
	private PrebidCsvLine prebidCsvLine;

	@Before
	public void resetLine() {
		prebidCsvLine = new PrebidCsvLine();
		prebidCsvLine.setBidder("TrustX");
		prebidCsvLine.setSlotId("21060508");
	}

	@Test
	public void testMissingSlotId() {
		prebidCsvLine.setSlotId("");
		try {
			trustX.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreate() {
		Bidder bidder = trustX.create(prebidCsvLine);
		assertEquals("trustx", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"uid", "21060508",
			"useNewFormat", true,
			"type", "display"
		);
		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testCreateInstream() {
		prebidCsvLine.setMediaType("instream");
		Bidder bidder = trustX.create(prebidCsvLine);
		assertEquals("trustx", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"uid", "21060508",
			"useNewFormat", true,
			"type", "instream"
		);
		assertEquals(expectedParams, bidder.getParams());
	}



	@Test
	public void testValidate() {
		try {
			trustX.validate(prebidCsvLine);
		} catch (Exception e) {
			fail("unexpected throw");
		}
	}

}