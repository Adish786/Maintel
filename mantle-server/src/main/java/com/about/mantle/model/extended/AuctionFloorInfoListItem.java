package com.about.mantle.model.extended;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AuctionFloorInfoListItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer floor;
	private String id;

	private String slot;

	private AuctionFloorInfoFactors auctionFloorFactors;

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getSlot() {
		return this.slot;
	}

	@JsonIgnore
	public Map<String, String> getFloorConfig() {
		Map<String, String> floorConfig = new HashMap();
		String floorValue = floor instanceof Integer ? String.valueOf(floor) : "50";

		floorConfig.put("floor", floorValue);
		floorConfig.put("id", id);
		return floorConfig;
	}

	public AuctionFloorInfoFactors getAuctionFloorFactors() {
		return auctionFloorFactors;
	}

	public void setAuctionFloorFactors(AuctionFloorInfoFactors auctionFloorFactors) {
		if(auctionFloorFactors.getSlot() != null) {
			setSlot(auctionFloorFactors.getSlot());
		}

		this.auctionFloorFactors = auctionFloorFactors;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();

		str.append("AuctionFloorInfoListItem [");
		str.append("floor=").append(floor.toString()).append(", ");
		str.append("id=").append(id).append(", ");
		str.append("factors=").append(auctionFloorFactors.toString());
		str.append("]");

		return str.toString();
	}
}
