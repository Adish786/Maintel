package com.about.mantle.model.services.prebid;

import static com.about.globe.core.http.ua.DeviceCategory.PERSONAL_COMPUTER;
import static com.about.globe.core.http.ua.DeviceCategory.SMARTPHONE;
import static com.about.globe.core.http.ua.DeviceCategory.TABLET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.junit.Test;

public class BidderEntryTest {

	private Map<String, Object> getConfig1() {
		Map<String, Object> answer = new HashMap<>();
		answer.put("string", "value1");
		answer.put("int", 2);
		answer.put("bool", true);
		answer.put("map", ImmutableMap.of("innerKey", ImmutableMap.of("innerInnerKey", "innerInnerValue")));
		answer.put("list", ImmutableList.of(1, 2, 3));
		answer.put("set", ImmutableSet.of(1, 2, 3));
		return answer;
	}

	private Map<String, Object> getConfig2() {
		Map<String, Object> answer = new TreeMap<>();
		answer.put("list", ImmutableList.of(1, 2, 3));
		answer.put("string", "value1");
		answer.put("map", ImmutableMap.of("innerKey", ImmutableMap.of("innerInnerKey", "innerInnerValue")));
		answer.put("bool", true);
		answer.put("set", ImmutableSet.of(1, 3, 2));
		answer.put("int", 2);
		return answer;
	}

	private Map<String, Object> getConfig3() {
		Map<String, Object> answer = new HashMap<>();
		answer.put("list", ImmutableList.of(1, 2, 3));
		answer.put("string", "value1");
		answer.put("map", ImmutableMap.of("innerKey", ImmutableMap.of("innerInnerKey", "innerInnerValueChanged"))); // difference between map1/2
		answer.put("bool", true);
		answer.put("set", ImmutableSet.of(1, 3, 2));
		answer.put("int", 2);
		return answer;
	}

	private Map<String, Set<String>> getABTests1() {
		Map<String, Set<String>> answer = new HashMap<>();
		answer.put("rtb", ImmutableSet.of("inactive", "control"));
		answer.put("prebid", ImmutableSet.of("active"));
		return answer;
	}

	private Map<String, Set<String>> getABTests2() {
		Map<String, Set<String>> answer = new HashMap<>();
		answer.put("prebid", ImmutableSet.of("active"));
		answer.put("rtb", ImmutableSet.of("control", "inactive"));
		return answer;
	}

	private Map<String, Set<String>> getABTests3() {
		Map<String, Set<String>> answer = new HashMap<>();
		answer.put("prebid", ImmutableSet.of("active"));
		answer.put("test", ImmutableSet.of("foo", "bar", "baz"));
		return answer;
	}

	@Test
	public void testHashCodeSlot() {
		BidderEntry bidderEntry1 = new BidderEntry("slot", EnumSet.of(PERSONAL_COMPUTER, SMARTPHONE), getConfig1(), getABTests1());
		BidderEntry bidderEntry2 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig1(), getABTests1());
		BidderEntry bidderEntry3 = new BidderEntry("slot2", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig1(), getABTests1());
		assertEquals(bidderEntry1.hashCode(), bidderEntry2.hashCode());
		assertNotEquals(bidderEntry2.hashCode(), bidderEntry3.hashCode());
	}

	@Test
	public void testHashCodeDevices() {
		BidderEntry bidderEntry1 = new BidderEntry("slot", EnumSet.of(PERSONAL_COMPUTER, SMARTPHONE), getConfig1(), getABTests1());
		BidderEntry bidderEntry2 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig1(), getABTests1());
		BidderEntry bidderEntry3 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER, TABLET), getConfig1(), getABTests1());
		assertEquals(bidderEntry1.hashCode(), bidderEntry2.hashCode());
		assertNotEquals(bidderEntry2.hashCode(), bidderEntry3.hashCode());
	}

	@Test
	public void testHashCodeConfig() {
		BidderEntry bidderEntry1 = new BidderEntry("slot", EnumSet.of(PERSONAL_COMPUTER, SMARTPHONE), getConfig1(), getABTests1());
		BidderEntry bidderEntry2 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig2(), getABTests1());
		BidderEntry bidderEntry3 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig3(), getABTests1());
		assertEquals(bidderEntry1.hashCode(), bidderEntry2.hashCode());
		assertNotEquals(bidderEntry2.hashCode(), bidderEntry3.hashCode());
	}

	@Test
	public void testHashCodeABTests() {
		BidderEntry bidderEntry1 = new BidderEntry("slot", EnumSet.of(PERSONAL_COMPUTER, SMARTPHONE), getConfig1(), getABTests1());
		BidderEntry bidderEntry2 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig1(), getABTests2());
		BidderEntry bidderEntry3 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig1(), getABTests3());
		assertEquals(bidderEntry1.hashCode(), bidderEntry2.hashCode());
		assertNotEquals(bidderEntry2.hashCode(), bidderEntry3.hashCode());
	}

	@Test
	public void testEqualsSlot() {
		BidderEntry bidderEntry1 = new BidderEntry("slot", EnumSet.of(PERSONAL_COMPUTER, SMARTPHONE), getConfig1(), getABTests1());
		BidderEntry bidderEntry2 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig1(), getABTests1());
		BidderEntry bidderEntry3 = new BidderEntry("slot2", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig1(), getABTests1());
		assertTrue(bidderEntry1.equals(bidderEntry2));
		assertFalse(bidderEntry2.equals(bidderEntry3));
	}

	@Test
	public void testEqualsDevices() {
		BidderEntry bidderEntry1 = new BidderEntry("slot", EnumSet.of(PERSONAL_COMPUTER, SMARTPHONE), getConfig1(), getABTests1());
		BidderEntry bidderEntry2 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig1(), getABTests1());
		BidderEntry bidderEntry3 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER, TABLET), getConfig1(), getABTests1());
		assertTrue(bidderEntry1.equals(bidderEntry2));
		assertFalse(bidderEntry2.equals(bidderEntry3));
	}

	@Test
	public void testEqualsConfig() {
		BidderEntry bidderEntry1 = new BidderEntry("slot", EnumSet.of(PERSONAL_COMPUTER, SMARTPHONE), getConfig1(), getABTests1());
		BidderEntry bidderEntry2 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig2(), getABTests1());
		BidderEntry bidderEntry3 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig3(), getABTests1());
		assertTrue(bidderEntry1.equals(bidderEntry2));
		assertFalse(bidderEntry2.equals(bidderEntry3));
	}

	@Test
	public void testEqualsABTests() {
		BidderEntry bidderEntry1 = new BidderEntry("slot", EnumSet.of(PERSONAL_COMPUTER, SMARTPHONE), getConfig1(), getABTests1());
		BidderEntry bidderEntry2 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig1(), getABTests2());
		BidderEntry bidderEntry3 = new BidderEntry("slot", EnumSet.of(SMARTPHONE, PERSONAL_COMPUTER), getConfig1(), getABTests3());
		assertTrue(bidderEntry1.equals(bidderEntry2));
		assertFalse(bidderEntry2.equals(bidderEntry3));
	}

}