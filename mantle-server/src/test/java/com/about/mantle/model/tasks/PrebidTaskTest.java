package com.about.mantle.model.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.ua.DeviceCategory;
import com.about.globe.core.http.ua.UserAgent;
import com.about.globe.core.testing.GlobeBucket;
import com.about.mantle.model.services.PrebidConfigurationService;
import com.about.mantle.model.services.prebid.BidderEntry;
import com.about.mantle.model.services.prebid.PrebidConfiguration;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.junit.Test;

public class PrebidTaskTest {

	private PrebidConfigurationService getMockService(PrebidConfiguration config) {
		PrebidConfigurationService mockService = mock(PrebidConfigurationService.class);
		when(mockService.getConfiguration()).thenReturn(config);
		return mockService;
	}

	private RequestContext getMockRequestContext(DeviceCategory deviceCategory) {
		RequestContext mockRequestContext = mock(RequestContext.class);
		UserAgent mockUserAgent = getMockUserAgent(deviceCategory);
		Map<String, GlobeBucket> mockTests = getMockTests();
		when(mockRequestContext.getUserAgent()).thenReturn(mockUserAgent);
		when(mockRequestContext.getTests()).thenReturn(mockTests);
		return mockRequestContext;
	}

	private UserAgent getMockUserAgent(DeviceCategory deviceCategory) {
		UserAgent mockUserAgent = mock(UserAgent.class);
		when(mockUserAgent.getDeviceCategory()).thenReturn(deviceCategory);
		return mockUserAgent;
	}

	private Map<String, GlobeBucket> getMockTests() {
		return ImmutableMap.of(
			"test1", new GlobeBucket("active", "active", 99, null, "99"),
			"test2", new GlobeBucket("control", "control", 99, null, "99")
		);
	}

	@Test
	public void testNullConfig() {
		PrebidTask task = new PrebidTask(getMockService(null));
		try {
			task.prebidConfig(getMockRequestContext(DeviceCategory.PERSONAL_COMPUTER));
			fail("expected exception");
		} catch (Exception e) {
		}
	}

	@Test
	public void testEmptyConfig() {
		PrebidTask task = new PrebidTask(getMockService(new PrebidConfiguration.Builder().build()));
		Map<String, List<Map<String, Object>>> config = task.prebidConfig(getMockRequestContext(DeviceCategory.PERSONAL_COMPUTER));
		assertTrue(config.isEmpty());
	}

	@Test
	public void testMixedDeviceConfig() {
		PrebidConfiguration.Builder configBuilder = new PrebidConfiguration.Builder();
		configBuilder.addBidderEntry(new BidderEntry("foo", EnumSet.of(DeviceCategory.SMARTPHONE), Collections.emptyMap(), null));
		configBuilder.addBidderEntry(new BidderEntry("bar", EnumSet.of(DeviceCategory.PERSONAL_COMPUTER), Collections.emptyMap(), null));
		PrebidTask task = new PrebidTask(getMockService(configBuilder.build()));
		Map<String, List<Map<String, Object>>> config = task.prebidConfig(getMockRequestContext(DeviceCategory.PERSONAL_COMPUTER));
		assertEquals(1, config.size());
		assertTrue(config.containsKey("bar"));
	}

	@Test
	public void testMultiSlotConfig() {
		PrebidConfiguration.Builder configBuilder = new PrebidConfiguration.Builder();
		configBuilder.addBidderEntry(new BidderEntry("foo", EnumSet.of(DeviceCategory.PERSONAL_COMPUTER), ImmutableMap.of("see", "dos"), null));
		configBuilder.addBidderEntry(new BidderEntry("foo", EnumSet.of(DeviceCategory.PERSONAL_COMPUTER), ImmutableMap.of("omg", "wtf"), null));
		configBuilder.addBidderEntry(new BidderEntry("bar", EnumSet.of(DeviceCategory.PERSONAL_COMPUTER), ImmutableMap.of("baz", "bin"), null));
		PrebidTask task = new PrebidTask(getMockService(configBuilder.build()));
		Map<String, List<Map<String, Object>>> config = task.prebidConfig(getMockRequestContext(DeviceCategory.PERSONAL_COMPUTER));
		assertEquals(2, config.size());
		assertTrue(config.containsKey("foo"));
		assertTrue(config.containsKey("bar"));
		assertEquals(2, config.get("foo").size());
		assertEquals(1, config.get("bar").size());
		assertEquals(ImmutableMap.of("see", "dos"), config.get("foo").get(0));
		assertEquals(ImmutableMap.of("omg", "wtf"), config.get("foo").get(1));
		assertEquals(ImmutableMap.of("baz", "bin"), config.get("bar").get(0));
	}

	@Test
	public void testABTestsConfig() {
		PrebidConfiguration.Builder configBuilder = new PrebidConfiguration.Builder();
		configBuilder.addBidderEntry(new BidderEntry("foo", EnumSet.of(DeviceCategory.PERSONAL_COMPUTER), ImmutableMap.of("see", "dos"), null));
		configBuilder.addBidderEntry(new BidderEntry("foo", EnumSet.of(DeviceCategory.PERSONAL_COMPUTER), ImmutableMap.of("omg", "wtf"), ImmutableMap.of("test1", ImmutableSet.of("control"))));
		configBuilder.addBidderEntry(new BidderEntry("bar", EnumSet.of(DeviceCategory.PERSONAL_COMPUTER), ImmutableMap.of("baz", "bin"), ImmutableMap.of("test1", ImmutableSet.of("active"))));
		PrebidTask task = new PrebidTask(getMockService(configBuilder.build()));
		Map<String, List<Map<String, Object>>> config = task.prebidConfig(getMockRequestContext(DeviceCategory.PERSONAL_COMPUTER));
		assertEquals(2, config.size());
		assertTrue(config.containsKey("foo"));
		assertTrue(config.containsKey("bar"));
		assertEquals(1, config.get("foo").size());
		assertEquals(1, config.get("bar").size());
		assertEquals(ImmutableMap.of("see", "dos"), config.get("foo").get(0));
		assertEquals(ImmutableMap.of("baz", "bin"), config.get("bar").get(0));
	}

	@Test
	public void testHasBidder() {
		PrebidConfiguration.Builder configBuilder = new PrebidConfiguration.Builder();
		configBuilder.addBidderEntry(new BidderEntry("foo", EnumSet.of(DeviceCategory.PERSONAL_COMPUTER), ImmutableMap.of("bidder", "ix"), null));
		configBuilder.addBidderEntry(new BidderEntry("foo", EnumSet.of(DeviceCategory.PERSONAL_COMPUTER), ImmutableMap.of("bidder", "criteo"), null));
		configBuilder.addBidderEntry(new BidderEntry("bar", EnumSet.of(DeviceCategory.PERSONAL_COMPUTER), ImmutableMap.of("bidder", "rubicon"), null));
		PrebidTask task = new PrebidTask(getMockService(configBuilder.build()));
		Map<String, List<Map<String, Object>>> config = task.prebidConfig(getMockRequestContext(DeviceCategory.PERSONAL_COMPUTER));
		assertTrue(task.prebidConfigHasBidder(config, "ix"));
		assertTrue(task.prebidConfigHasBidder(config, "criteo"));
		assertTrue(task.prebidConfigHasBidder(config, "rubicon"));
		assertFalse(task.prebidConfigHasBidder(config, "grid"));
	}

}