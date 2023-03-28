package com.about.mantle.model.extended.ratings;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AggregateRating implements Serializable {

	private static final long serialVersionUID = 1L;

	protected static final NumberFormat nf = new DecimalFormat("##.#");
	
	protected Float rating;
	protected Integer count;

	public Float getRating() {
		return rating;
	}

	public void setRating(Float rating) {
		this.rating = rating;
	}
	
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
	@JsonIgnore
	public String getAverageRoundedToHalf() {
		if(rating == null) return null;
		return nf.format(Math.round(rating * 2) / 2.0);
	}

	@JsonIgnore
	public String getAverageRoundedToTenth() {
		if(rating == null) return null;
		return nf.format(Math.round(rating * 10) / 10.0);
	}
	
	@Override
	public String toString() {
		return "DisqusRating [rating=" + rating + "]";
	}
}
