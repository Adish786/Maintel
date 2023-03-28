package com.about.mantle.model.services.prebid.bidders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

public class RubiconProjectCreatorTest {

	private static RubiconProjectCreator rubiconProject = new RubiconProjectCreator();
	private PrebidCsvLine prebidCsvLine;

	@Before
	public void resetLine() {
		prebidCsvLine = new PrebidCsvLine();
		prebidCsvLine.setBidder("Rubicon Project");
		prebidCsvLine.setAccountId("7499");
		prebidCsvLine.setDomainId("42");
		prebidCsvLine.setSlotId("1337");
	}

	@Test
	public void testMissingAccountId() {
		prebidCsvLine.setAccountId("");
		try {
			rubiconProject.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testMissingDomainId() {
		prebidCsvLine.setDomainId("");
		try {
			rubiconProject.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testMissingSlotId() {
		prebidCsvLine.setSlotId("");
		try {
			rubiconProject.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreate() {
		Bidder bidder = rubiconProject.create(prebidCsvLine);
		assertEquals("rubicon", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"accountId", "7499",
			"siteId", "42",
			"zoneId", "1337",
			"type", "display"
		);
		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testCreateInstream() {
		prebidCsvLine.setMediaType("instream");
		Bidder bidder = rubiconProject.create(prebidCsvLine);
		assertEquals("rubicon", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"accountId", "7499",
			"siteId", "42",
			"zoneId", "1337",
			"type", "instream"
		);
		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testValidate() {
		try {
			rubiconProject.validate(prebidCsvLine);
		} catch (Exception e) {
			fail("unexpected throw");
		}
	}

}