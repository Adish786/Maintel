package com.about.mantle.model.tasks;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.ua.DeviceCategory;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.globe.core.testing.GlobeBucket;
import com.about.mantle.model.services.PrebidConfigurationService;
import com.about.mantle.model.services.prebid.BidderEntry;
import com.about.mantle.model.services.prebid.PrebidConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tasks associated with the Prebid RTB (realtime bidding) plugin.
 */
@Tasks
public class PrebidTask {

	private static final Logger logger = LoggerFactory.getLogger(PrebidTask.class);

	private final PrebidConfigurationService prebidConfigurationService;

	public PrebidTask(PrebidConfigurationService prebidConfigurationService) {
		this.prebidConfigurationService = prebidConfigurationService;
	}

	/**
	 * Gets the Prebid config which specifies the bidders for each ad slot.
	 */
	@Task(name = "prebidConfig")
	public Map<String, List<Map<String, Object>>> prebidConfig(@RequestContextTaskParameter RequestContext requestContext) {
		final DeviceCategory deviceCategory = requestContext.getUserAgent().getDeviceCategory();
		if (deviceCategory == null) {
			logger.error("Unknown device category");
			return null;
		}

		PrebidConfiguration prebidConfig = prebidConfigurationService.getConfiguration();
		return prebidConfig.getBidderEntries().stream()
			.filter(bidderEntry -> bidderEntry.getDeviceCategories().contains(deviceCategory))
			.filter(bidderEntry -> {
				// If AB tests are not specified then the entry always applies.
				if (bidderEntry.getABTests() == null) {
					return true;
				}
				for (Map.Entry<String, Set<String>> abTest : bidderEntry.getABTests().entrySet()) {
					GlobeBucket globeBucket = requestContext.getTests().get(abTest.getKey());
					// If in test then use bucket, otherwise control matches. (Same logic as Filterable.java)
					String bucketName = globeBucket != null ? globeBucket.getName() : "control";
					if (abTest.getValue().contains(bucketName)) {
						return true;
					}
				}
				return false;
			})
			.collect(Collectors.groupingBy(BidderEntry::getSlot, Collectors.mapping(BidderEntry::getPrebidConfig, Collectors.toList())));
	}

	/**
	 * Checks if a specified bidder is in the prebid config.
	 */
	@Task(name = "prebidConfigHasBidder")
	public boolean prebidConfigHasBidder(@TaskParameter(name = "prebidConfig") Map<String, List<Map<String, Object>>> prebidConfig,
	                                     @TaskParameter(name = "bidderCode") String bidderCode) {
		if (prebidConfig != null && bidderCode != null) {
			// Return as soon as we find the presence of the bidder in at least one of the configs.
			for (List<Map<String, Object>> listOfBidderConfigs : prebidConfig.values()) { // list of configs applying to each slot
				for (Map<String, Object> bidderConfigs : listOfBidderConfigs) {
					if (bidderCode.equals(bidderConfigs.get("bidder"))) {
						return true;
					}
				}
			}
		}
		return false;
	}

}