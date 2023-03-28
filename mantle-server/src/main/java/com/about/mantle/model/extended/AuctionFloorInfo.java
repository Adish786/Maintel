package com.about.mantle.model.extended;

import java.io.Serializable;

public class AuctionFloorInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer floor;
	private String id;

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

}
