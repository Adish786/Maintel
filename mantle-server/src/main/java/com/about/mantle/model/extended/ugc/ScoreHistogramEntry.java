package com.about.mantle.model.extended.ugc;

import java.io.Serializable;

public class ScoreHistogramEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private Short score;
	private Integer count;

	public Short getScore() {
		return score;
	}
	public void setScore(Short score) {
		this.score = score;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}