package com.about.mantle.model.extended.ratings;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UGCAggregateRating extends AggregateRating implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	@JsonProperty("average") // This Jackson annotation lets "average" field from service response
								// deserialized to rating field in data model 
	public Float getRating() {
		return super.getRating();
	}

}
