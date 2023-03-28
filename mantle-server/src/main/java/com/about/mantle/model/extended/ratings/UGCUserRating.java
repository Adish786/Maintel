package com.about.mantle.model.extended.ratings;

import java.io.Serializable;

public class UGCUserRating implements Serializable {

	private static final long serialVersionUID = 1L;
	private Short ratingScore;
	private Integer count; // supposedly not used? too afraid to touch and hoping this whole class goes away anyway

	public static UGCUserRating fromScore(Short score) {
		UGCUserRating rating = new UGCUserRating();
		rating.setRatingScore(score);
		return rating;
	}

	public Short getRatingScore() {
		return ratingScore;
	}

	public void setRatingScore(Short ratingScore) {
		this.ratingScore = ratingScore;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
