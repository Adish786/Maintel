package com.about.mantle.venus.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public enum GeoIps {

	US("US"),
	UK("GB"),
	CANADA("CA"),
	INDIA("IN"),
	AUSTRALIA("AU"),
	BRAZIL("BR"),
	FRANCE("FR"),
	CALIFORNIA("US-CA"),
	TEXAS("US-TX"),
	NEWYORK("US-NY"),
	MISSING_STATE_CODE("US-?");

	private List<NameValuePair> geoCodeNvp;

	private GeoIps(String countryCode_iso2) {
		if (countryCode_iso2.contains("US-")) {
			String geoRegion = countryCode_iso2;
			this.geoCodeNvp = Arrays.asList(new BasicNameValuePair("country_code_iso2", "US"),
					new BasicNameValuePair("geo_region", geoRegion));
		} else if (countryCode_iso2.equals("CA")) {
			String geoRegion = "ON";
			this.geoCodeNvp = Arrays.asList(new BasicNameValuePair("country_code_iso2", countryCode_iso2),
					new BasicNameValuePair("geo_region", geoRegion));
		} else {
			String countryCode = countryCode_iso2;
			if (countryCode_iso2.equals("FR") || countryCode_iso2.equals("GB"))
				countryCode = "EU";
			this.geoCodeNvp = Arrays.asList(new BasicNameValuePair("country_code_iso2", countryCode_iso2),
					new BasicNameValuePair("country_code", countryCode));
		}
	}

	public List<NameValuePair> geoLocation() {
		return geoCodeNvp;
	}

}