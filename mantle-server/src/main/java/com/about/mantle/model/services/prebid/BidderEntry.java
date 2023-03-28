package com.about.mantle.model.services.prebid;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.about.globe.core.http.ua.DeviceCategory;
import com.about.mantle.model.services.prebid.bidders.Bidder;
import com.about.mantle.model.services.prebid.csv.PrebidCsvLine;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

public class BidderEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String slot;
	private final EnumSet<DeviceCategory> deviceCategories;
	private final Map<String, Object> prebidConfig;
	private final Map<String, Set<String>> abTests;

	public static BidderEntry from(PrebidCsvLine prebidCsvLine, Bidder bidder) {
		String slot = prebidCsvLine.getSlot();
		EnumSet<DeviceCategory> deviceCategories = EnumSet.copyOf(prebidCsvLine.getDeviceCategories());
		Map<String, Set<String>> abTests = prebidCsvLine.getABTests();
		Map<String, Object> prebidConfig = ImmutableMap.of(
			"bidder", bidder.getCode(),
			"params", bidder.getParams()
		);
		return new BidderEntry(slot, deviceCategories, prebidConfig, abTests);
	}

	@JsonCreator
	public BidderEntry(@JsonProperty("slot") String slot,
	                   @JsonProperty("deviceCategories") EnumSet<DeviceCategory> deviceCategories,
	                   @JsonProperty("prebidConfig") Map<String, Object> prebidConfig,
	                   @JsonProperty("abTests") Map<String, Set<String>> abTests) {
		this.slot = slot;
		this.deviceCategories = deviceCategories;
		this.prebidConfig = prebidConfig;
		this.abTests = abTests;
	}

	/**
	 * The ad slot for which this entry applies.
	 * e.g. adhesive, billboard, leaderboard, etc.
	 */
	public String getSlot() {
		return slot;
	}

	/**
	 * The device categories for which this entry applies.
	 * e.g. pc, tablet, mobile
	 */
	public EnumSet<DeviceCategory> getDeviceCategories() {
		return deviceCategories;
	}

	/**
	 * The AB tests/buckets for which this entry applies.
	 * e.g. prebid=active, etc.
	 * A `null` return value means that the entry _always_ applies;
	 * otherwise the entry only applies when an item in the map matches a test bucket.
	 */
	@JsonProperty("abTests") // without this Jackson can't associate this method with the field because of casing, i.e. getABTests vs getAbTests
	public Map<String, Set<String>> getABTests() {
		return abTests;
	}

	/**
	 * The bidder-specific Prebid config for this entry.
	 * e.g. { "bidder": "code", "params": { ... } }
	 */
	public Map<String, Object> getPrebidConfig() {
		return prebidConfig;
	}

	/**
	 * hashCode and equals have to be overridden because bidder entries are collected in a set in PrebidConfiguration.
	 * They are collected in a set instead of a list for deduping purposes.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((abTests == null) ? 0 : abTests.hashCode());
		result = prime * result + ((deviceCategories == null) ? 0 : deviceCategories.hashCode());
		result = prime * result + ((prebidConfig == null) ? 0 : prebidConfig.hashCode());
		result = prime * result + ((slot == null) ? 0 : slot.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BidderEntry other = (BidderEntry) obj;
		if (abTests == null) {
			if (other.abTests != null)
				return false;
		} else if (!abTests.equals(other.abTests))
			return false;
		if (deviceCategories == null) {
			if (other.deviceCategories != null)
				return false;
		} else if (!deviceCategories.equals(other.deviceCategories))
			return false;
		if (prebidConfig == null) {
			if (other.prebidConfig != null)
				return false;
		} else if (!prebidConfig.equals(other.prebidConfig))
			return false;
		if (slot == null) {
			if (other.slot != null)
				return false;
		} else if (!slot.equals(other.slot))
			return false;
		return true;
	}

}