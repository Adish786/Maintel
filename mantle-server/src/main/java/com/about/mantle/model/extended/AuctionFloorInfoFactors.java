package com.about.mantle.model.extended;

import java.io.Serializable;

public class AuctionFloorInfoFactors implements Serializable {

	private static final long serialVersionUID = 1L;

	private String geoLocation;
	private String vertical;
	private String slot;
	private String os;
	private String deviceCategory;
	private String tier;

	public String getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(String geoLocation) {
		this.geoLocation = geoLocation;
	}

	public String getVertical() {
		return vertical;
	}

	public void setVertical(String vertical) {
		this.vertical = vertical;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getDeviceCategory() {
		return deviceCategory;
	}

	public void setDeviceCategory(String deviceCategory) {
		this.deviceCategory = deviceCategory;
	}

	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append("AuctionFloorInfoFactors [");
		str.append("geoLocation=").append(geoLocation).append(", ");
		str.append("vertical=").append(vertical).append(", ");
		str.append("slot=").append(slot).append(", ");
		str.append("os=").append(os).append(", ");
		str.append("deviceCategory=").append(deviceCategory).append(", ");
		str.append("tier=").append(tier).append("]");

		return str.toString();
	}
}
