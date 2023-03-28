package com.about.mantle.model.services;

import java.util.Map;

import com.about.globe.core.http.GeoData;

/**
 * Service for retrieving domain data that is used to construct the consent banner.
 */
public interface ConsentBannerService {

	public static enum Template { CCPA, GDPR }

	static Template getExpectedTemplate(GeoData geoData) {
		if (geoData != null) {
			if (geoData.isInEuropeanUnion()) {
				return Template.GDPR;
			}
			// We want to make the Do Not Sell My Information link appear across all regions of the US.
			// To do this, we return CCPA here along with making some geolocation rule changes for the
			// US in OneTrust. See AXIS-2900 for more information.
			if ("US".equals(geoData.getIsoCode())) {
				return Template.CCPA;
			}
		}
		return null;
	}

	/**
	 * JSON response
	 * PHASE 1: use data points to build our own banner (https://dotdash.atlassian.net/browse/AXIS-66)
	 * PHASE 2: have a way of getting the prepackaged HTML/CSS (https://dotdash.atlassian.net/browse/AXIS-193)
	 * Not putting too much thought into this interface at this time
	 * because it remains to be seen how PHASE 2 will look.
	 */
	Map<String, Object> getDomainData(GeoData geoData);

}
