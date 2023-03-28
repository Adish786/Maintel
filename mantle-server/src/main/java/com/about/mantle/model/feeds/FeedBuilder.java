package com.about.mantle.model.feeds;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.feeds.datamodel.Feed;

/**
 * Generates data model for XML (RSS) or Json feeds.
 */
public interface FeedBuilder {

	/**
	 * Stitch together all feed elements in this method and return a feed object
	 * representing entire response. Which can be later marshaled or use with a ftl
	 * template.
	 * 
	 * @param requestContext
	 * @return Feed
	 */
	Feed prepareFeed(RequestContext requestContext);

}
