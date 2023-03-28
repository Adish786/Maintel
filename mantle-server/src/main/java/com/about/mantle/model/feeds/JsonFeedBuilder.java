package com.about.mantle.model.feeds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.about.mantle.model.feeds.datamodel.FeedItem;
import com.about.mantle.model.services.feeds.api.RssFeedService;
import com.about.mantle.model.services.feeds.response.RssFeedSearchItem;

/**
 * Base builder class which contains majority of the common functionality to get
 * started with a JSON based feed. Note: Based on your vertical business needs,
 * this class will not provide a working feed out of the box until it is
 * appropriately extended.
 * 
 * Note: prepareFeed template method is purposefully not provided in base
 * {@link JsonFeedBuilder} class. Unlike {@link XmlFeedBuilder} which follows a
 * pre-defined structure of RSS specs, the only JSON based implementation of
 * {@link JsonFeedBuilder} is {@link BloombergFeedBuilder} which follows very
 * specific business requirements.
 */
public abstract class JsonFeedBuilder extends BaseFeedBuilder {

	private static final Logger logger = LoggerFactory.getLogger(JsonFeedBuilder.class);
	protected static final DateTimeFormatter DEFAULT_DATETIME_FORMAT = DateTimeFormat
			.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZoneUTC();

	public JsonFeedBuilder(RssFeedService rssFeedSearchService, Vertical vertical) {
		super(rssFeedSearchService, vertical);
	}

	protected String convertDatetimeStringToSolrFormat(String datetime) {
		return DEFAULT_DATETIME_FORMAT.parseDateTime(datetime).toString();
	}

	/**
	 * Prepare individual feed item by transforming individual RSS search items.
	 */
	protected abstract FeedItem prepareFeedItem(RssFeedSearchItem searchItem);

	/**
	 * Prepare individual feed item based on implementing verticals business specs.
	 */
	protected abstract List<FeedItem> prepareFeedItems(String startDatetime, String endDatetime);

	/**
	 * Date-time duration of data to pull from Selene. Provided by client.
	 */
	protected abstract String datetimeRangeFilterQuery(String startDatetime, String endDatetime);

	protected boolean isMissingMandatoryField(String field) {
		return StringUtils.isBlank(field);
	}

	protected void logMissingFields(RssFeedSearchItem searchItem) {
		logger.warn("Missing mandatory fields 'title' and/or 'body' for document: {}", searchItem.getUrl());
	}

	/**
	 * For a given date-time duration client can get a maximum of 1000 because
	 * Selene calls including full document response are expensive.
	 */
	protected int provideFeedLimit() {
		return 1000;
	}

	/**
	 * Keep SC as default contents.
	 */
	@Override
	protected String provideMandatoryTemplatetypeFilterQuery() {
		return "templateType:STRUCTUREDCONTENT";
	}

	@Override
	protected List<String> aggregateFilterQueries(RequestContext requestContext) {
		List<String> aggregatedFilterQueries = new ArrayList<>();
		String templateTypeFilterQuery = provideMandatoryTemplatetypeFilterQuery();
		aggregatedFilterQueries.add(templateTypeFilterQuery);
		return aggregatedFilterQueries;
	}

	@Override
	protected boolean searchShouldMapExchanges() {
		return false;
	}

	@Override
	protected boolean searchShouldIncludeFullDocuments() {
		return true;
	}

	@Override
	protected String provideSortParameter() {
		return "";
	}

	@Override
	protected Map<String, Object> provideAdditionalQueryParams(RequestContext requestContext) {
		return Collections.emptyMap();
	}
}
