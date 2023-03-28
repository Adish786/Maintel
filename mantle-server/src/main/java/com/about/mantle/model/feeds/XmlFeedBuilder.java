package com.about.mantle.model.feeds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.RSS2.Channel;
import com.about.mantle.model.RSS2.Item;
import com.about.mantle.model.RSS2.Item.Enclosure;
import com.about.mantle.model.RSS2.RssFeed;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.feeds.datamodel.Feed;
import com.about.mantle.model.services.feeds.api.RssFeedService;
import com.about.mantle.model.services.feeds.response.RssFeedSearchItem;
import com.about.mantle.render.MantleRenderUtils;

/**
 * Base builder class which contains majority of the common functionality to get
 * started with a RSS (XML based) feed. Note: Based on your vertical business
 * needs, this class will not provide a working feed out of the box until it is
 * appropriately extended.
 */
public abstract class XmlFeedBuilder extends BaseFeedBuilder {

	private static Logger logger = LoggerFactory.getLogger(XmlFeedBuilder.class);
	private final MantleRenderUtils renderUtils;

	public XmlFeedBuilder(RssFeedService rssFeedSearchService, MantleRenderUtils renderUtils, Vertical vertical) {
		super(rssFeedSearchService, vertical);
		this.renderUtils = renderUtils;
	}

	@Override
	public Feed prepareFeed(RequestContext requestContext) {
		RssFeed preparedFeed = new RssFeed();
		Channel preparedChannel = prepareChannel(requestContext);
		preparedChannel.setItems(prepareItems(requestContext, acquireItems(requestContext)));
		preparedFeed.setChannel(preparedChannel);

		return preparedFeed;
	}

	/**
	 * Prepare <code>channel</code> element which will contain
	 * <code>title, link, description, language, etc</code> information about the
	 * feed.
	 */
	protected abstract Channel prepareChannel(RequestContext requestContext);

	/**
	 * Prepare <code>item</code> child element of <code>channel</code> element which
	 * will contain
	 * <code>title, link, description, pubDate, enclosure, guid and dc:creator</code>
	 * information.
	 */
	protected abstract List<Item> prepareItems(RequestContext requestContext,
			List<RssFeedSearchItem> searchResultItems);

	/**
	 * Validate for the presence of certain mandatory fields where absence of one or
	 * more can make item unusable.
	 *
	 * @return boolean result of mandatory fields validation
	 */
	protected boolean validateAndLogInvalid(RssFeedSearchItem aResult) {
		return checkForNulls(aResult) && isValidSearchResult(aResult);
	}

	/**
	 * Validate result item and log if it is missing.
	 */
	private boolean checkForNulls(RssFeedSearchItem aResult) {
		boolean isValid = true;

		if (aResult == null) {
			logger.error("Got null RSS item from {}", getFeedDisplayName());
			isValid = false;
		}
		return isValid;
	}

	/**
	 * Overridden by subclasses to perform per-feed validation of the search result.
	 * If this method returns false, the item will be skipped and logged. True by
	 * default.
	 *
	 * All feeds by default will adhere to following mandatory fields: title and
	 * link. Note: This overrides RSS specs of mandatory either title or description
	 * fields. If a link if missing then it makes the item useless.
	 *
	 * {@see https://validator.w3.org/feed/docs/error/ItemMustContainTitleOrDescription.html}
	 *
	 * @return boolean result of mandatory fields validation
	 */
	protected boolean isValidSearchResult(RssFeedSearchItem aResult) {
		boolean isValid;
		Set<String> missingFields = new HashSet<>();

		if (StringUtils.isBlank(aResult.getTitle())) {
			missingFields.add("title");
		}
		if (StringUtils.isBlank(aResult.getDescription()) && StringUtils.isBlank(aResult.getTitle())) {
			missingFields.add("description");// missing description makes item useless only if title is also missing
		}
		if (StringUtils.isBlank(aResult.getUrl())) {
			missingFields.add("url");
		}
		isValid = missingFields.isEmpty();
		if (!isValid) {
			logger.warn("Invalid RSS search result because of missing mandatory fields: " + String.join(",", missingFields));
		}
		return isValid;
	}

	/**
	 * If provided, append query parameters to item link (url).
	 */
	protected String provideItemLinkQueryParameter() {
		return "";
	}

	/**
	 * Prepare <code>enclosure</code> child element of <code>item</code> element
	 * which will contain image information.
	 */
	protected Enclosure prepareItemEnclosure(RequestContext requestContext, ImageEx image) {
		Enclosure enclosure = null;
		if (image != null) {
			String imageUrl = generateImageUrl(requestContext, image);
			if (StringUtils.isNotBlank(imageUrl)) {
				enclosure = new Enclosure();
				enclosure.setUrl(imageUrl);
				/*
				 * Per spec explanation here:
				 * http://www.rssboard.org/rss-profile#element-channel-item-enclosure
				 * "When an enclosure's size cannot be determined, a publisher should use a length of 0."
				 * Based on current requirements image size in bytes is not needed, use default
				 * value of 0.
				 */
				enclosure.setLength("0");
				enclosure.setType("image/jpeg");
			}
		}
		return enclosure;
	}

	/**
	 * Generate thumbor image url based on provided image object.
	 */
	protected String generateImageUrl(RequestContext requestContext, ImageEx image) {
		String imageUrl = "";
		String objectId = image.getObjectId();
		Integer width = image.getWidth();
		Integer height = image.getHeight();

		if (StringUtils.isNotBlank(objectId) && (width != null && width > 0) && (height != null && height > 0)) {
			imageUrl = renderUtils.getThumborUrl(image, width, height, "", true, requestContext, null, null);
		}
		return imageUrl;
	}

	/**
	 * Allows subclasses to add feed-specific fields to their RSS Item element.
	 */
	protected Item addCustomItemAttributes(RssFeedSearchItem searchResultItem, Item item) {
		return item;
	}

	/**
	 * Returns a list of RSS Feed search items from the Selene service. For most
	 * services it is just an envelope method that returns List of items from the
	 * search result. Specific services can override this method. E.g. by calling
	 * Selene service multiple times and combine/transform RSS Feed search items per
	 * their business specs.
	 * 
	 * @param requestContext Note: not all implementing methods will utilize
	 *                       requestContext.
	 */
	protected List<RssFeedSearchItem> acquireItems(RequestContext requestContext) {
		List<RssFeedSearchItem> searchItems = fulfillFeedItems(Collections.emptyList(), requestContext);
		return searchItems;
	}

	/**
	 * Get plain text feed name.
	 */
	protected abstract String getFeedDisplayName();

	@Override
	protected int provideFeedLimit() {
		return 100;
	}

	@Override
	protected List<String> aggregateFilterQueries(RequestContext requestContext) {
		List<String> aggregatedFilterQueries = new ArrayList<>();
		String dateRangeFilterQuery = dateRangeFilterQuery();
		String templateTypeFilterQuery = provideMandatoryTemplatetypeFilterQuery();
		List<String> additionalFilterQueries = provideAdditionalFeedSpecificFilterQueries(requestContext);

		aggregatedFilterQueries.add(dateRangeFilterQuery);
		aggregatedFilterQueries.add(templateTypeFilterQuery);
		aggregatedFilterQueries.addAll(additionalFilterQueries);

		return aggregatedFilterQueries;
	}

	/**
	 * Number of days of data to pull from Selene. Defaulted to last 7 days
	 * (lastPublishedDate Selene field) for all XML based RSS feeds.
	 */
	protected String dateRangeFilterQuery() {
		return "lastPublished:[NOW-7DAYS TO NOW]";
	}

	/**
	 * Most feeds require SC documents. Whichever feeds require other template-type
	 * can provide their own template-types.
	 */
	@Override
	protected String provideMandatoryTemplatetypeFilterQuery() {
		return "templateType:STRUCTUREDCONTENT";
	}

	/**
	 * Individual feeds can add more filter queries based on business specs. This
	 * further filters Selene RssFeedService search results using these filters.
	 * 
	 * E.g. tickers filter query or Taxonomies docIds filter query
	 * 
	 * @param requestContext Note: not all implementing methods will utilize
	 *                       requestContext.
	 */
	protected List<String> provideAdditionalFeedSpecificFilterQueries(RequestContext requestContext) {
		return Collections.emptyList();
	}

	@Override
	protected boolean searchShouldIncludeFullDocuments() {
		return false;
	}

	@Override
	protected boolean searchShouldMapExchanges() {
		return false;
	}

	/**
	 * Prepare publish date for the feed individual item element in items list.
	 */
	protected abstract DateTime prepareItemPubDate(RssFeedSearchItem aResult);

	/**
	 * Enclose text with Character Data (CDATA). Assuming that enclosed text is
	 * already HTML decoded. Enclosing in CDATA means that you are telling client
	 * parser not to further parse the text and consume it as it is.
	 */
	protected String encloseWithCdata(String text) {
		StringBuilder sb = new StringBuilder("<![CDATA[");
		sb.append(text);
		sb.append("]]>");

		return sb.toString();
	}
	
	@Override
	protected String provideSortParameter() {
		return "";
	}
}
