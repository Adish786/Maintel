package com.about.mantle.model.services.prebid.bidders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

public class AppNexusCreatorTest {

	private static AppNexusCreator appNexus = new AppNexusCreator();
	private PrebidCsvLine prebidCsvLine;

	@Before
	public void resetLine() {
		prebidCsvLine = new PrebidCsvLine();
		prebidCsvLine.setBidder("AppNexus");
		prebidCsvLine.setSlotId("21060508");
	}

	@Test
	public void testMissingSlotId() {
		prebidCsvLine.setSlotId("");
		try {
			appNexus.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreate() {
		Bidder bidder = appNexus.create(prebidCsvLine);
		assertEquals("appnexus", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"placementId", "21060508",
			"type", "display"
		);

		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testCreateInstream() {
		prebidCsvLine.setMediaType("instream");
		Bidder bidder = appNexus.create(prebidCsvLine);
		assertEquals("appnexus", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"placementId", "21060508",
			"type", "instream"
		);

		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testValidate() {
		try {
			appNexus.validate(prebidCsvLine);
		} catch (Exception e) {
			fail("unexpected throw");
		}
	}

}