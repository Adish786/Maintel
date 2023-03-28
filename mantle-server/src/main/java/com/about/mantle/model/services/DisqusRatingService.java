package com.about.mantle.model.services;

import com.about.mantle.model.extended.ratings.DisqusAggregateRating;

public interface DisqusRatingService {

	DisqusAggregateRating getAggregateRating(Long documentId);
	
}
