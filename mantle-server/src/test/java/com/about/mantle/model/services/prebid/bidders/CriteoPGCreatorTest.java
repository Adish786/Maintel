package com.about.mantle.model.services.prebid.bidders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

public class CriteoPGCreatorTest {

	private static CriteoPGCreator criteopg = new CriteoPGCreator();
	private PrebidCsvLine prebidCsvLine;

	@Before
	public void resetLine() {
		prebidCsvLine = new PrebidCsvLine();
		prebidCsvLine.setBidder("CriteoPG");
	}

	@Test
	public void testCreate() {
		Bidder bidder = criteopg.create(prebidCsvLine);
		assertEquals("criteopg", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of();
		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testValidate() {
		try {
			criteopg.validate(prebidCsvLine);
		} catch (Exception e) {
			fail("unexpected throw");
		}
	}

}