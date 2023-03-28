package com.about.mantle.model.services.prebid.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import com.about.globe.core.http.ua.DeviceCategory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import org.junit.Test;

public class PrebidCsvLineTest {

	private static final String HEADER = "domain,device,slot,size,bidder,account_id,domain_id,slot_id,ab_tests,media_type";

	private static List<PrebidCsvLine> parse(Reader reader) {
		return new CsvToBeanBuilder<PrebidCsvLine>(reader)
			.withType(PrebidCsvLine.class)
			.withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
			.build()
			.parse();
	}

	private static PrebidCsvLine parseLine(String line) {
		List<PrebidCsvLine> lines = parse(new StringReader(HEADER + "\n" + line));
		if (lines.size() != 1) {
			fail("Expected to parse a single line");
		}
		return lines.get(0);
	}

	@Test
	public void testRequiredHeader() {
		try {
			parse(new StringReader("foo,bar"));
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testParsing() {
		PrebidCsvLine line = parseLine("Investopedia,mobile,adhesive,320x50,Index Exchange,183710-54318773746259,408198,35,,");
		assertEquals("Investopedia", line.getDomain());
		assertTrue(line.getDeviceCategories().contains(DeviceCategory.SMARTPHONE));
		assertFalse(line.getDeviceCategories().contains(DeviceCategory.PERSONAL_COMPUTER));
		assertFalse(line.getDeviceCategories().contains(DeviceCategory.TABLET));
		assertEquals("adhesive", line.getSlot());
		assertEquals(ImmutableList.of(320, 50), line.getSize());
		assertEquals("Index Exchange", line.getBidder());
		assertEquals("183710-54318773746259", line.getAccountId());
		assertEquals("408198", line.getDomainId());
		assertEquals("35", line.getSlotId());
		assertEquals("display", line.getMediaType());
		assertNull(line.getABTests());
	}

	@Test
	public void testParsingMultipleDeviceCategories() {
		PrebidCsvLine line = parseLine("Investopedia,\"mobile,tablet\",adhesive,320x50,Index Exchange,183710-54318773746259,408198,35,,");
		assertEquals("Investopedia", line.getDomain());
		assertTrue(line.getDeviceCategories().contains(DeviceCategory.SMARTPHONE));
		assertFalse(line.getDeviceCategories().contains(DeviceCategory.PERSONAL_COMPUTER));
		assertTrue(line.getDeviceCategories().contains(DeviceCategory.TABLET));
		assertEquals("adhesive", line.getSlot());
		assertEquals(ImmutableList.of(320, 50), line.getSize());
		assertEquals("Index Exchange", line.getBidder());
		assertEquals("183710-54318773746259", line.getAccountId());
		assertEquals("408198", line.getDomainId());
		assertEquals("35", line.getSlotId());
		assertEquals("display", line.getMediaType());
		assertNull(line.getABTests());
	}

	@Test
	public void testParsingSingleABTest() {
		PrebidCsvLine line = parseLine("Investopedia,mobile,adhesive,320x50,Index Exchange,183710-54318773746259,408198,35,prebid=active,");
		assertEquals("Investopedia", line.getDomain());
		assertTrue(line.getDeviceCategories().contains(DeviceCategory.SMARTPHONE));
		assertFalse(line.getDeviceCategories().contains(DeviceCategory.PERSONAL_COMPUTER));
		assertFalse(line.getDeviceCategories().contains(DeviceCategory.TABLET));
		assertEquals("adhesive", line.getSlot());
		assertEquals(ImmutableList.of(320, 50), line.getSize());
		assertEquals("Index Exchange", line.getBidder());
		assertEquals("183710-54318773746259", line.getAccountId());
		assertEquals("408198", line.getDomainId());
		assertEquals("35", line.getSlotId());
		assertEquals("display", line.getMediaType());
		assertEquals(ImmutableMap.of("prebid", ImmutableSet.of("active")), line.getABTests());
	}

	@Test
	public void testParsingMultipleABTests() {
		PrebidCsvLine line = parseLine("Investopedia,mobile,adhesive,320x50,Index Exchange,183710-54318773746259,408198,35,\"prebid=active;test=foo,bar,baz;prebid=control\",");
		assertEquals("Investopedia", line.getDomain());
		assertTrue(line.getDeviceCategories().contains(DeviceCategory.SMARTPHONE));
		assertFalse(line.getDeviceCategories().contains(DeviceCategory.PERSONAL_COMPUTER));
		assertFalse(line.getDeviceCategories().contains(DeviceCategory.TABLET));
		assertEquals("adhesive", line.getSlot());
		assertEquals(ImmutableList.of(320, 50), line.getSize());
		assertEquals("Index Exchange", line.getBidder());
		assertEquals("183710-54318773746259", line.getAccountId());
		assertEquals("408198", line.getDomainId());
		assertEquals("35", line.getSlotId());
		assertEquals("display", line.getMediaType());
		assertEquals(ImmutableMap.of("prebid", ImmutableSet.of("active", "control"), "test", ImmutableSet.of("foo", "bar", "baz")), line.getABTests());
	}

	@Test
	public void testParsingInvalidDeviceCategory() {
		try {
			parseLine("Investopedia,desktop,adhesive,320x50,Index Exchange,183710-54318773746259,408198,35,,");
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testParsingInvalidABTest() {
		try {
			parseLine("Investopedia,mobile,adhesive,320x50,Index Exchange,183710-54318773746259,408198,35,prebid,");
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testParsingRequiredColumnsOnly() {
		PrebidCsvLine line = parseLine("Investopedia,mobile,adhesive,,Index Exchange,,,,,");
		assertEquals("Investopedia", line.getDomain());
		assertTrue(line.getDeviceCategories().contains(DeviceCategory.SMARTPHONE));
		assertFalse(line.getDeviceCategories().contains(DeviceCategory.PERSONAL_COMPUTER));
		assertFalse(line.getDeviceCategories().contains(DeviceCategory.TABLET));
		assertEquals("adhesive", line.getSlot());
		assertEquals("Index Exchange", line.getBidder());
		assertNull(line.getSize());
		assertNull(line.getAccountId());
		assertNull(line.getDomainId());
		assertNull(line.getSlotId());
		assertNull(line.getABTests());
		assertEquals("display", line.getMediaType());
	}

	@Test
	public void testParsingDefinedMediaTypeDisplay() {
		PrebidCsvLine line = parseLine("Investopedia,\"pc,tablet\",leaderboardac,,Media Grid,,,35,,display");
		assertEquals("Investopedia", line.getDomain());
		assertFalse(line.getDeviceCategories().contains(DeviceCategory.SMARTPHONE));
		assertTrue(line.getDeviceCategories().contains(DeviceCategory.PERSONAL_COMPUTER));
		assertTrue(line.getDeviceCategories().contains(DeviceCategory.TABLET));
		assertEquals("leaderboardac", line.getSlot());
		assertEquals("Media Grid", line.getBidder());
		assertEquals("35", line.getSlotId());
		assertNull(line.getABTests());
		assertEquals("display", line.getMediaType());
	}
	
	@Test
	public void testParsingDefinedMediaTypeOutstream() {
		PrebidCsvLine line = parseLine("Investopedia,\"pc,tablet\",leaderboardac,,Media Grid,,,35,,outstream");
		assertEquals("Investopedia", line.getDomain());
		assertFalse(line.getDeviceCategories().contains(DeviceCategory.SMARTPHONE));
		assertTrue(line.getDeviceCategories().contains(DeviceCategory.PERSONAL_COMPUTER));
		assertTrue(line.getDeviceCategories().contains(DeviceCategory.TABLET));
		assertEquals("leaderboardac", line.getSlot());
		assertEquals("Media Grid", line.getBidder());
		assertEquals("35", line.getSlotId());
		assertNull(line.getABTests());
		assertEquals("outstream", line.getMediaType());
	}

	@Test
	public void testParsingMissingRequiredColumn() {
		try {
			parseLine("Investopedia,mobile,adhesive,320x50,,,,,,");
			fail("expected exception");
		} catch (Exception e) {
		}
	}

}