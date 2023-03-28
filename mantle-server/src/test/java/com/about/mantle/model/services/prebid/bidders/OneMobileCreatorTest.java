package com.about.mantle.model.services.prebid.bidders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

public class OneMobileCreatorTest {

	private static OneMobileCreator oneMobile = new OneMobileCreator();
	private PrebidCsvLine prebidCsvLine;

	@Before
	public void resetLine() {
		prebidCsvLine = new PrebidCsvLine();
		prebidCsvLine.setBidder("OneMobile");
		prebidCsvLine.setDomainId("8a96901a017a7ae786e2e89dc3ec0037");
		prebidCsvLine.setSlotId("hlt_square_fixed_tier4_300x250");
	}

	@Test
	public void testMissingDomainId() {
		prebidCsvLine.setDomainId("");
		try {
			oneMobile.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testMissingSlotId() {
		prebidCsvLine.setSlotId("");
		try {
			oneMobile.create(prebidCsvLine);
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testCreate() {
		Bidder bidder = oneMobile.create(prebidCsvLine);
		assertEquals("onemobile", bidder.getCode());
		Map<String, Object> expectedParams = ImmutableMap.of(
			"dcn", "8a96901a017a7ae786e2e89dc3ec0037",
			"pos", "hlt_square_fixed_tier4_300x250"
		);
		assertEquals(expectedParams, bidder.getParams());
	}

	@Test
	public void testValidate() {
		try {
			oneMobile.validate(prebidCsvLine);
		} catch (Exception e) {
			fail("unexpected throw");
		}
	}

}