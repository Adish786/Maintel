package com.about.mantle.model.tasks;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.feeds.FeedBuilder;
import com.about.mantle.model.feeds.FeedBuilderFactory;
import com.about.mantle.model.feeds.datamodel.Feed;

/**
 * Generates data model for feed. Feed nature is determined based on the
 * provided implementation of {@link FeedBuilderFactory} and it is agnostic
 * here. Presentation layer determines how to display this feed i.e. use FTL to
 * render as RSS feed or JSON string representation.
 */
@Tasks
public class FeedTask {

	protected final FeedBuilderFactory feedBuilderFactory;
	private Map<String,FeedBuilder> feedBuilderMap;

	public FeedTask(FeedBuilderFactory feedBuilderFactory, Map<String,FeedBuilder> feedBuilderMap) {
		this.feedBuilderFactory = feedBuilderFactory;
		this.feedBuilderMap = feedBuilderMap;
	}

	/**
	 * Return feed prepared by the builder by using the provided feed name.
	 */
	@Task(name = "feed")
	public Feed getFeed(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "feedName") String feedName) {
		Feed preparedFeed = null;
		FeedBuilder builder = getFeedBuilder(feedName);
		if (builder != null) {
			preparedFeed = builder.prepareFeed(requestContext);
		}
		return preparedFeed;
	}

	private FeedBuilder getFeedBuilder(String feedName) {
		FeedBuilder builder = null;

		if (isFeedNameValid(feedName)) {
			if(feedBuilderFactory != null) {
				builder = feedBuilderFactory.create(feedName.toLowerCase());
			}else if(feedBuilderMap != null) {
				builder = feedBuilderMap.get(feedName);
			}else {
				throw new GlobeException("Either feedBuilderFactory or feedBuilderMap must be present to get feed builder");
			}
		}
		
		return builder;
	}

	private boolean isFeedNameValid(String feedName) {
		boolean flag = StringUtils.isBlank(feedName);
		if (flag) {
			throw new GlobeException("Mandatory parameter feedname can not be empty or null");
		}
		return !flag;
	}
}