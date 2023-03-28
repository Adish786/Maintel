package com.about.mantle.app;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.about.mantle.model.services.DocumentService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.model.EnvironmentConfig;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.extended.DeionSearchResultItemEx;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.about.mantle.model.services.DeionSearchService;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext;
import com.google.common.collect.ImmutableList;

/**
 * To facilitate rendering of vertical's legacy urls, load an in-memory map
 * containing all possible legacy documents which can be later used to lookup
 * docId when {@link VerticalUrlData} gets created, because globe expects url to
 * be in the form of slug-docId.
 */
public class LegacyUrlMapImpl implements LegacyUrlMap {
	private static final Logger logger = LoggerFactory.getLogger(LegacyUrlMapImpl.class);
	// Must use ConcurrentHashMap as multiple threads may read map while update is being made
	private static Map<String, Long> map = new ConcurrentHashMap<>();

	private Long minimumLegacyDocuments;
	private final DeionSearchService deionSearchService;
	private final Vertical vertical;
	private final EnvironmentConfig envConfig;

	@Deprecated
	public LegacyUrlMapImpl(DeionSearchService deionSearchService, Vertical vertical, Long minimumLegacyDocuments) {
		this(deionSearchService, vertical, minimumLegacyDocuments, null);
	}

	public LegacyUrlMapImpl(DeionSearchService deionSearchService, Vertical vertical, Long minimumLegacyDocuments,
							EnvironmentConfig envConfig) {
		this.deionSearchService = deionSearchService;
		this.minimumLegacyDocuments = minimumLegacyDocuments;
		this.envConfig = envConfig;
		if (vertical == null) {
			throw new GlobeException("Vertical name can not be null, failed to create legacy url map.");
		}
		this.vertical = vertical;

		// Populate map here by calling Selene Deion service at startup and get all
		// legacy urls up-front.
		populateMapWithDeionSearchResultItems(fulfillDeionSearchResultItems(false), false);
	}

	private List<DeionSearchResultItemEx> fulfillDeionSearchResultItems(boolean repopulate) {
		int batchCounter = 0;
		int limitBatchSize = DeionSearchService.SUGGESTED_MAX_LIMIT;
		int offset = batchCounter * limitBatchSize;
		DeionSearchResultEx searchResult = null;
		List<DeionSearchResultItemEx> itemsAggregated = new ArrayList<>(Math.max(map.size(), limitBatchSize));
		try {
			// first pass, get total number of documents available in SOLR
			if(!repopulate) {
				logger.debug("Querying {} legacy documents in SOLR: offset={}, limit={}, items-aggregated={}", vertical,
						offset, limitBatchSize, itemsAggregated.size());
			}

			searchResult = performDeionSearch(offset, limitBatchSize);

			if(!repopulate) {
				logger.debug("Found {} legacy documents in SOLR:{}", vertical, searchResult.getNumFound());
			}

			itemsAggregated.addAll(searchResult.getItems());

			/*
			 * following pass(es), keep fulfilling until found amount in SOLR is met
			 */
			while (CollectionUtils.isNotEmpty(searchResult.getItems())) {
				batchCounter++;
				offset = batchCounter * limitBatchSize;

				if(!repopulate) {
					logger.debug("Querying {} legacy documents in SOLR: offset={}, limit={}, items-aggregated={}", vertical,
							offset, limitBatchSize, itemsAggregated.size());
				}

				searchResult = performDeionSearch(offset, limitBatchSize);
				if (!searchResult.getItems().isEmpty()) {
					Long lastDocIdRecorded = itemsAggregated.get(itemsAggregated.size() - 1).getDocId();
					Long firstDocIdFromCurrentSearch = searchResult.getItems().get(0).getDocId();
					if (lastDocIdRecorded > firstDocIdFromCurrentSearch) {
						logger.warn("Deion search sort for legacy documents is out of order. Last docId {} is ahead of the new search's first docId {} in a search sorted by docId in ascending order",
								lastDocIdRecorded, firstDocIdFromCurrentSearch);
					}
					itemsAggregated.addAll(searchResult.getItems());
				}

				if(!repopulate) {
					logger.debug("Found {} legacy documents in SOLR:{}", vertical, searchResult.getNumFound());
				}
			}

			// Adding prod check to simplify qa environment legacy url configuration during data conversions
			// Verticals using deprecated constructor (no env config) will be treated as prod for backwards
			// compatibility.
			boolean isProd = (envConfig == null || "prod".equalsIgnoreCase(envConfig.getAccountName()));
			if (isProd && CollectionUtils.isEmpty(itemsAggregated)) {
				throw new GlobeException("Legacy documents are not found in SOLR");
			}

		} catch (Exception ex) {
			throw new GlobeException("Error while getting results back from Selene Deion search", ex);
		}
		return itemsAggregated;
	}

	/**
	 * Currently the assumption is that Selene computes on POST and adds a
	 * legacyDocument flag to any legacy document during content migration. This
	 * flag is also indexed in SOLR.
	 */
	private DeionSearchResultEx performDeionSearch(Integer offset, Integer limit) {
		String allInclusiveQuery = "*:*";
		List<String> filters = ImmutableList.of("vertical:" + vertical, "legacyDocument:true");
		List<String> fields = ImmutableList.of("url", "docId");
		String sort = "docId ASC";
		DeionSearchRequestContext.Builder builder = new DeionSearchRequestContext.Builder();
		builder.setQuery(allInclusiveQuery).setFilterQueries(filters).setFields(fields).setOffset(offset)
				.setLimit(limit).setSort(sort).setNoCache(true).build();
		return deionSearchService.search(builder.build());
	}

	/**
	 * Builds in memory map by extracting uri paths from urls, and using docIds from
	 * SOLR search results.
	 */
	private void populateMapWithDeionSearchResultItems(List<DeionSearchResultItemEx> items, boolean repopulate) {
		for (DeionSearchResultItemEx anItem : items) {
			String url = anItem.getUrl();
			Long docId = anItem.getDocId();
			TemplateTypeEx template = anItem.getTemplateTypeEx();

			addLegacyDocument(url, docId, template, repopulate);
		}

		if(!repopulate) {
			logger.info("Populated in-memory map with {} legacy documents:{}", vertical, map.size());
		}
		
		if (minimumLegacyDocuments != null && minimumLegacyDocuments > 0L) {
			checkForLegacyMapDeprivationAndHaltApp(minimumLegacyDocuments, map.size());
		}
	}

	/**
	 * Adds a legacy document entry into the map
	 * @param url
	 * @param docId
	 * @param template
	 */
	private void addLegacyDocument(String url, Long docId, TemplateTypeEx template, boolean repopulate) {
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			logger.error("Failed to parse url:{} with docId:{}", url, docId);
		}

		if (uri != null && StringUtils.isNotBlank(uri.getPath()) && !"/".equals(uri.getPath())
				&& docId != null) {
			/**
			 * If the legacy url is already in the map then there must have been some docs added and
			 * the deion search sort is out of order. This will cause errors for ops. Logging an error to keep track.
			 */
			String mapKey = uri.getPath();
			if (map.containsKey(mapKey)) {

				//If we are repopulating then we should expect there to be duplicates, this only happens on the timer than re-runs the search once
				//Also if for some reason we get the same path with the same docId we should not report the redundant error
				if(!repopulate && !docId.equals(map.get(mapKey))) {
					logger.warn("Duplicate legacy url in map. Potential overwriting doc that is ignored - docId: {} and url: {} and templateType: {}. " +
									"Already recorded with this docId: {}",
							docId, url, template != null ? template.toString() : null, map.get(mapKey));
				}
			} else {
				map.put(uri.getPath(), docId);
			}
		} else {
			logger.warn("Found an illegal legacy document:{} with docId:{}", uri, docId);
		}
	}

	private void checkForLegacyMapDeprivationAndHaltApp(long expectedSize, long actualSize) {
		if (actualSize < expectedSize) {
			throw new GlobeException("Legacy map is deprived of legacy documents. Expected minimum" + expectedSize
					+ " legacy documents but fulfilled only " + actualSize);
		}
	}

	/**
	 * Returns the docId for a legacy document based on its uri path
	 */
	@Override
	public Long getDocId(String uriPath) {
		return map.get(preProcessUriPath(uriPath));
	}

	/**
	 * Adds an additional document to the list of legacy documents
	 * @param document
	 */
	@Override
	public void addLegacyDocument(BaseDocumentEx document) {
		if (document != null) {
			addLegacyDocument(document.getUrl(), document.getDocumentId(), document.getTemplateType(), true);
		}
	}

	@Override
	public void repopulateMap(){
		populateMapWithDeionSearchResultItems(fulfillDeionSearchResultItems(true), true);
	}

	@Override
	public void addPathsToExclude(List<String> paths) {
		//Do nothing this is for the Permissive Class
	}

	/**
	 * Provide this hook method for verticals to do some pre-processing on uri paths
	 * before the map look-up. For example: Beauty vertical validate and normalize
	 * uri paths by removing trailing '/'
	 */
	protected String preProcessUriPath(String uriPath) {
		return uriPath;
	}
}
