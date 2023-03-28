package com.about.mantle.model.services.feeds.api;

import java.util.List;
import java.util.Map;

import com.about.mantle.model.services.feeds.response.RssFeedSearchResponse;

/**
 * This service retrieves a list of documents from Selene rss/build endpoint
 * which are then used to construct RSS feeds based on individual verticals
 * business requirements.
 */
public interface RssFeedService {
	RssFeedSearchResponse search(String query, List<String> filterQueries, Boolean mapExchanges, Integer limit,
			Integer offset, Boolean includeFullDocument, String sort, Map<String, Object> additionalQueryParams);

}