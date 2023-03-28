package com.about.mantle.model.services.prebid.bidders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

public class CriteoCreatorTest {

	private static CriteoCreator criteo = new CriteoCreator();
	private PrebidCsvLine prebidCsvLine;

	@Before
	public void resetLine() {
		prebidCsvLine = new PrebidCsvLine();
		prebidCsvLine.setBidder("Criteo");
		prebidCsvLine.setAccountId("1337");
	}

	@Test
	public void testMissingAccountId() {
		prebidCsvLine.setAccountId("");
		try {
			criteo.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testInvalidAccountId() {
		prebidCsvLine.setAccountId("foo");
		try {
			criteo.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreate() {
		Bidder bidder = criteo.create(prebidCsvLine);
		assertEquals("criteo", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"networkId", 1337
		);
		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testValidate() {
		try {
			criteo.validate(prebidCsvLine);
		} catch (Exception e) {
			fail("unexpected throw");
		}
	}

}