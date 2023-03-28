package com.about.mantle.model.extended.ugc;

import java.io.Serializable;

public class AverageRating implements Serializable {
	private static final long serialVersionUID = 1L;

	private Float average;
	private Integer count;

	public Float getAverage() {
		return average;
	}
	public void setAverage(Float average) {
		this.average = average;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}