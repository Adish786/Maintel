package com.about.mantle.model.extended.ugc;

import java.io.Serializable;

public class ThumbsSignalHistogramEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private ThumbsSignal thumbsSignal;
	private Integer count;

	public ThumbsSignal getThumbsSignal() {
		return thumbsSignal;
	}
	public void setThumbsSignal(ThumbsSignal thumbsSignal) {
		this.thumbsSignal = thumbsSignal;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}