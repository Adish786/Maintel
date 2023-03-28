package com.about.mantle.model.services;

import com.about.mantle.model.services.prebid.PrebidConfiguration;

/**
 * Service that provides the business-owned configuration of bidders that are assigned to ad slots.
 */
public interface PrebidConfigurationService {

	PrebidConfiguration getConfiguration();

}