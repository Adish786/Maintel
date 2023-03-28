package com.about.mantle.model.services.prebid;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

/**
 * Fully loaded prebid configuration containing the relevant bidder entries.
 */
public class PrebidConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Bidder entries are collected inside a set instead of a list for deduping purposes.
	 */
	private final Set<BidderEntry> bidderEntries;

	@JsonCreator
	public PrebidConfiguration(@JsonProperty("bidderEntries") Set<BidderEntry> bidderEntries) {
		this.bidderEntries = bidderEntries;
	}

	public Set<BidderEntry> getBidderEntries() {
		return bidderEntries;
	}

	public static class Builder {

		private final ImmutableSet.Builder<BidderEntry> bidderEntries = ImmutableSet.builder();

		public void addBidderEntry(BidderEntry bidderEntry) {
			bidderEntries.add(bidderEntry);
		}

		public PrebidConfiguration build() {
			return new PrebidConfiguration(bidderEntries.build());
		}

	}

}