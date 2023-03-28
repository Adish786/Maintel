package com.about.mantle.model.feeds;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.hippodrome.models.response.Error;
import com.about.hippodrome.models.response.Status;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHeadingEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHeadingEx.StructuredContentHeadingDataEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHtmlEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHtmlEx.StructuredContentHtmlDataEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentSubheadingEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentSubheadingEx.StructuredContentSubheadingDataEx;
import com.about.mantle.model.services.feeds.api.RssFeedService;
import com.about.mantle.model.services.feeds.response.RssFeedSearchItem;
import com.about.mantle.model.services.feeds.response.RssFeedSearchResponse;

/**
 * This class provides common functionality that is shared between multiple
 * feeds (XML and JSON alike). It has also listed a few abstract functionality
 * that must be implemented by implementing feed builder classes(s). Note:
 * Extending this class alone will never result in a functional feed out of the
 * box.
 *
 */
public abstract class BaseFeedBuilder implements FeedBuilder {

	private static final Logger logger = LoggerFactory.getLogger(BaseFeedBuilder.class);
	private static final DateTimeFormatter ISO8601_COMPLIANT_DATETIME_FORMAT = DateTimeFormat
			.forPattern("yyyy-MM-dd'T'HH:mm:ss+00:00").withZoneUTC();

	private final RssFeedService rssFeedSearchService;
	private final Vertical vertical;

	public BaseFeedBuilder(RssFeedService rssFeedSearchService, Vertical vertical) {
		this.rssFeedSearchService = rssFeedSearchService;
		this.vertical = vertical;
	}

	/**
	 * Aggregate text from all HTML and HEADING blocks from a RSS search item to be
	 * consumed either in a 'content' of XML feed or 'body' of JSON feed. Note:
	 * Ideally this element should contain full article body but business currently
	 * wants to add only certain SC blocks that can be appended quickly to shorten
	 * production release cycle. For example, TABLE SC block would be time consuming
	 * to re-construct with all of its relative Html tags. However, HEADING and HTML
	 * are relatively simple. Also (according to business), Yahoo is going to
	 * consume RSS feed <content> element by feeding their AI systems for model
	 * training instead of rendering it anywhere on their website thus de-risking
	 * our brand image. TODO: Add remaining SC blocks INVRP-526
	 */
	protected String prepareFeedItemContent(BaseDocumentEx document) {
		StringBuilder contentBuilder = new StringBuilder("");

		((StructuredContentBaseDocumentEx) document).getContentsStream().forEach(aBlock -> {
			if (aBlock instanceof StructuredContentHeadingEx) {
				contentBuilder.append(encloseScHeadingWithH2Tags((StructuredContentHeadingEx) aBlock));
			} else if (aBlock instanceof StructuredContentSubheadingEx) {
				contentBuilder.append(encloseScSubheadingWithH3Tags((StructuredContentSubheadingEx) aBlock));
			} else if (aBlock instanceof StructuredContentHtmlEx) {
				contentBuilder.append(((StructuredContentHtmlDataEx) aBlock.getData()).getHtml());
			}
		});
		return decodeHtmlText(contentBuilder.toString());
	}

	/**
	 * Enclose SC heading text within a h2 html tag.
	 */
	protected String encloseScHeadingWithH2Tags(StructuredContentHeadingEx headingBlock) {
		String heading = "";
		if (headingBlock.getData() != null) {
			heading = encloseTextWithHtmlTag(((StructuredContentHeadingDataEx) headingBlock.getData()).getText(), "h2");
		}
		return heading;
	}

	/**
	 * Enclose SC subheading text within a h3 html tag.
	 */
	protected String encloseScSubheadingWithH3Tags(StructuredContentSubheadingEx subheadingBlock) {
		String heading = "";
		if (subheadingBlock.getData() != null) {
			heading = encloseTextWithHtmlTag(((StructuredContentSubheadingDataEx) subheadingBlock.getData()).getText(), "h3");
		}
		return heading;
	}

	/**
	 * Enclose text within provided tags.
	 */
	protected String encloseTextWithHtmlTag(String text, String tag) {
		StringBuilder enclosedText = new StringBuilder("");
		if (StringUtils.isNotBlank(text) && StringUtils.isNotBlank(tag)) {
			enclosedText.append("<");
			enclosedText.append(tag);
			enclosedText.append(">");
			enclosedText.append(text);
			enclosedText.append("</");
			enclosedText.append(tag);
			enclosedText.append(">");
		}
		return enclosedText.toString();
	}

	/**
	 * Provide and decode text from 1st HTML SC content block
	 */
	protected String getTextFromFirstScHtmlBlock(BaseDocumentEx document) {
		StringBuilder contentBuilder = new StringBuilder("");
		String htmlTextInFirstScBlock = ((StructuredContentHtmlEx) ((StructuredContentBaseDocumentEx) document)
				.getContentsStreamOfType("HTML").findFirst().get()).getData().getHtml();
		contentBuilder.append(htmlTextInFirstScBlock);
		return decodeHtmlText(contentBuilder.toString());
	}

	/**
	 * Provide the description text from doc summary
	 */
	protected String getDescriptionTextFromDocumentSummary(BaseDocumentEx document) {
		String description = "";
		if (document != null && document.getSummary() != null
				&& StringUtils.isNotBlank(document.getSummary().getDescription())) {
			description = document.getSummary().getDescription();
		}
		return description;
	}

	/**
	 * Unescape HTML entities.
	 */
	protected String decodeHtmlText(String encodedHtmlText) {
		String decodedHtml = null;
		if (StringUtils.isNotBlank(encodedHtmlText)) {
			decodedHtml = StringEscapeUtils.unescapeHtml4(encodedHtmlText);
		}
		return decodedHtml;
	}

	/**
	 * Remove HTML tags; persist text within in tags though.
	 */
	protected String stripHtmlFromText(String htmlText) {
		StringBuilder htmlFreeText = new StringBuilder();
		try {
			htmlFreeText.append(Jsoup.parse(htmlText).text());
		} catch (Exception ex) {
			logger.warn("HTML tags were not successfully removed from the HTML SC block text");
		}
		return htmlFreeText.toString();
	}

	/**
	 * Call Selene RSS search end-point in batches until requested limit is met. In
	 * the first pass, get total number of documents found in Selene. Then keep
	 * fulfilling until needed amount is met. Factor in possible duplicates.
	 * 
	 * @param requestContext Note: not all implementing methods will utilize
	 *                       requestContext.
	 */
	protected List<RssFeedSearchItem> fulfillFeedItems(List<String> additionalFilterQueries, RequestContext requestContext) {
		int offset = 0;
		int batchSize = 100;
		int itemsNeeded = provideFeedLimit();
		List<String> preparedFilterQueries = prepareFilterQueries(additionalFilterQueries, requestContext);
		Map<String, Object> additionalQueryParams = provideAdditionalQueryParams(requestContext);
		RssFeedSearchResponse response = performSeleneRssSearch(preparedFilterQueries, batchSize, offset, additionalQueryParams);

		int itemsFoundInSolr = response.getData().getTotalSize();
		itemsNeeded = itemsFoundInSolr >= itemsNeeded ? itemsNeeded : itemsFoundInSolr;
		List<RssFeedSearchItem> itemsPreDedupe = validateRetrievedItemsFromSelene(response);
		List<RssFeedSearchItem> itemsFulfilled = dedupeAndSortItems(itemsPreDedupe);
		int itemsPending = itemsNeeded - itemsFulfilled.size();

		while (itemsPending > 0 && CollectionUtils.isNotEmpty(itemsPreDedupe)) {
			offset += itemsPreDedupe.size(); // skip previously searched items in SOLR
			batchSize = itemsPending < batchSize ? itemsPending : batchSize; // determine limit for search call
			response = performSeleneRssSearch(preparedFilterQueries, batchSize, offset, additionalQueryParams);
			itemsPreDedupe = validateRetrievedItemsFromSelene(response);
			itemsFulfilled.addAll(itemsPreDedupe); // create opportunity to find duplicates
			itemsFulfilled = dedupeAndSortItems(itemsFulfilled); // eliminate duplicates
			itemsPending = itemsNeeded - itemsFulfilled.size();
		}
		logger.info("Found {} documents in Selene. Showing {} documents from Selene.", itemsFoundInSolr,
				itemsFulfilled.size());

		return itemsFulfilled;
	}

	/**
	 * Combine filter queries aggregated by individual set of feeds along with any
	 * additional filter queries that can be set by individual feed (see
	 * BloombergFeedBuilder).
	 */
	private List<String> prepareFilterQueries(List<String> additionalFilterQueries, RequestContext requestContext) {
		String verticalFilterQuery = new StringBuilder("vertical:").append(vertical).toString();
		List<String> aggregatedFilterQueries = aggregateFilterQueries(requestContext);
		List<String> filterQueries = new ArrayList<>();
		filterQueries.add(verticalFilterQuery);
		filterQueries.addAll(aggregatedFilterQueries);
		filterQueries.addAll(additionalFilterQueries);

		return filterQueries;
	}

	protected Map<String, Object> extractAndAddTaxonomyFeedQueryParamsIfRequired(RequestContext requestContext, boolean taxonomyRequired) {
		String taxonomyDocId = extractTaxDocIdFromQueryParams(requestContext, taxonomyRequired);
		if (taxonomyDocId != null) {
			return addTaxonomyFeedQueryParams(requestContext, taxonomyDocId);
		}
		return new LinkedHashMap<>();
	}

	protected Map<String, Object> addTaxonomyFeedQueryParams(RequestContext requestContext, String taxonomyDocId) {
		//Why LinkedHashMap ? to preserve the order and it's used in cache key of CachedRssFeedService
		Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("traversalStartDocId", taxonomyDocId);
		queryParams.put("traversalRelationship", "primaryParent");
		return queryParams;
	}

	private String extractTaxDocIdFromQueryParams(RequestContext requestContext, boolean taxonomyRequired) {
		Map<String, String[]> urlQueryParameters = requestContext.getParameters();
		if (MapUtils.isEmpty(urlQueryParameters) && taxonomyRequired) {
			throw new GlobeException("Query parameter is not provided.");
		}
		String[] retrievedValues = urlQueryParameters.get("taxDocId");
		if (ArrayUtils.isEmpty(retrievedValues) && taxonomyRequired) {
			throw new GlobeException("Mandatory query parameter [taxDocId] is not provided.");
		}
		return retrievedValues != null ? retrievedValues[0] : null;
	}

	/**
	 * This calls Selene RSS feed search end-point with a certain combination of
	 * filter queries (and other) parameters formulated for individual feeds. Full
	 * document and MapExchanges data for each search item can be requested based on
	 * individual underlying feed.
	 */
	private final RssFeedSearchResponse performSeleneRssSearch(List<String> filterQueries, int limit, int offset,  Map<String, Object> additionalQueryParams) {
		String allInclusiveQuery = "*:*";
		boolean mapExchangesEnabled = searchShouldMapExchanges();
		boolean includeFullDocuments = searchShouldIncludeFullDocuments();
		String sort = provideSortParameter();

		RssFeedSearchResponse response;

		try {
			response = rssFeedSearchService.search(allInclusiveQuery, filterQueries, mapExchangesEnabled, limit, offset,
					includeFullDocuments, sort, additionalQueryParams);
		} catch (Exception ex) {
			throw new GlobeException("Error while getting results back from Selene", ex);
		}
		reportSeleneErrors(response); // Note that we still continue even if Selene gives an error

		return response;
	}

	/**
	 * validate selene has returned search items.
	 */
	private List<RssFeedSearchItem> validateRetrievedItemsFromSelene(RssFeedSearchResponse response) {
		List<RssFeedSearchItem> items = new ArrayList<>();
		if (response != null && response.getData() != null
				&& CollectionUtils.isNotEmpty(response.getData().getList())) {
			items = response.getData().getList();
		}
		return items;
	}

	protected List<RssFeedSearchItem> dedupeAndSortItems(List<RssFeedSearchItem> searchItems) {
		SortedSet<RssFeedSearchItem> sortedSet = new TreeSet<>(itemSortingComparator());
		sortedSet.addAll(searchItems);
		return new ArrayList<>(sortedSet);
	}

	/**
	 * Dictates the sorting order used in {@link #dedupeAndSortItems(List)}
	 * If null, the natural ordering of {@code RssFeedSearchItem} will be used
	 */
	protected Comparator<RssFeedSearchItem> itemSortingComparator() {
		return null;
	}

	/**
	 * Remove any documents that have a non-null Sponsors property
	 */
	protected List<RssFeedSearchItem> filterSponsoredItems(List<RssFeedSearchItem> searchItems) {
		return searchItems.stream()
				.filter(searchItem -> searchItem.getDocument() == null
						|| searchItem.getDocument().getSponsors().isEmpty())
				.collect(Collectors.toList());
	}

	/**
	 * Log any errors that came from Selene
	 */
	private void reportSeleneErrors(RssFeedSearchResponse response) {
		Status status = response.getStatus();

		if (!"SUCCESS".equals(status.getCode().name())) {

			for (Error error : status.getErrors()) {
				logger.error("Selene RSS service error '{}': {}", error.getCode(), error.getMessage());
			}
		}
	}

	/**
	 * 
	 * Convert datetime to RFC-822/ISO 8601 compliant format. Use UTC/GMT as offset.
	 *
	 * @see <a href=
	 *      "https://validator.w3.org/feed/docs/error/InvalidRFC2822Date.html">
	 *      RFC822</a>
	 */
	protected String convertTimestampToCompliantFormat(long datetime) {
		return ISO8601_COMPLIANT_DATETIME_FORMAT.print(datetime);
	}

	/**
	 * Overridden by subclasses if the `mapExchanges` flag should be set to true
	 * when querying Selene. False by default.
	 *
	 * @return boolean if a feed requires map exchange data
	 */
	protected abstract boolean searchShouldMapExchanges();

	/**
	 * If the implementing feed requires Selene RSS Search endpoint to return
	 * document summary for each search item
	 */
	protected abstract boolean searchShouldIncludeFullDocuments();

	/**
	 * Max number of items to get from Selene and display in the feed.
	 */
	protected abstract int provideFeedLimit();

	/**
	 * Aggregate shared and individual filter queries from a sub-set of feeds.
	 * Example: all XML based RSS feeds share certain filter queries like
	 * lastPublishedDate but some of them rely on ticker date for stocks.
	 * 
	 * @param requestContext Note: not all implementing methods will utilize
	 *                       requestContext.
	 */
	protected abstract List<String> aggregateFilterQueries(RequestContext requestContext);

	/**
	 * Individual feeds can override and provide their own filter query for
	 * different template types. Default can be SC.
	 */
	protected abstract String provideMandatoryTemplatetypeFilterQuery();
	
	/**
	 * Sort search results based on provided sort parameter. Parameter can be a
	 * document field followed by the sort direction i.e. asc/ASC for ascending or
	 * desc/DESC for descending.
	 */
	protected abstract String provideSortParameter();

	protected abstract Map<String, Object> provideAdditionalQueryParams(RequestContext requestContext);
}
