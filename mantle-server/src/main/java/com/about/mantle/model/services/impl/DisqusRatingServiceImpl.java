package com.about.mantle.model.services.impl;

import javax.ws.rs.client.WebTarget;

import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.ratings.DisqusAggregateRating;
import com.about.mantle.model.extended.responses.DisqusRatingExResponse;
import com.about.mantle.model.services.DisqusRatingService;

public class DisqusRatingServiceImpl extends AbstractHttpServiceClient implements DisqusRatingService {
	
	private static final String SELENE_DISQUS_RATING_PATH = "/disqus/rating";
	
	public DisqusRatingServiceImpl(HttpServiceClientConfig config) {
		super(config);
	}

	@Override
	public DisqusAggregateRating getAggregateRating(Long documentId) {
		WebTarget webTarget = baseTarget.path(SELENE_DISQUS_RATING_PATH).path(documentId.toString());
		return readResponse(webTarget, DisqusRatingExResponse.class).getData();
	}

}
