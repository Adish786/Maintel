package com.about.mantle.model.services.news;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.extended.DeionSearchResultItemEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.DeionSearchService;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext;

public class GoogleNewsSitemapServiceImpl implements GoogleNewsSitemapService {
	
	private static final int SEARCH_BATCH_COUNT = 50;

	private final DeionSearchService deionSearchService;
	
	
	public GoogleNewsSitemapServiceImpl(DeionSearchService deionSearchService) {
		this.deionSearchService = deionSearchService;
	}

	@Override
	public List<BaseDocumentEx> getGoogleNewsDocuments(String domain) {

		return getNewsDocumentsFromDeionSearch(domain);
	}
	
	public static DeionSearchRequestContext.Builder getDeionSearchRequestContextBuilder(String domain) {
		// @formatter:off
		DeionSearchRequestContext.Builder builder = new DeionSearchRequestContext.Builder();
		builder.setNoCache(true).setDomain(domain)
		        .setQuery(getSitemapSearchQuery())
		        .setSort("displayed DESC")
				.setIncludeDocumentSummaries(true)
				.setLimit(SEARCH_BATCH_COUNT); // Currently this is the limit from selene Side, revisit once SVC-5362 is addressed
		// @formatter:on
		return builder;
	}
	
	private static String getSitemapSearchQuery() {
		return DeionSearchRequestContext.getDefaultTemplateTypesFilterOutQuery() + " AND -noIndex:true AND state:ACTIVE AND newsType:[* TO *] AND displayed:[NOW-2DAYS TO NOW]";
	}
	
	private List<BaseDocumentEx> getNewsDocumentsFromDeionSearch(String domain) {
		List<BaseDocumentEx> answer = new ArrayList<>();
		List<BaseDocumentEx> results = new ArrayList<>();
		DeionSearchRequestContext.Builder builder = getDeionSearchRequestContextBuilder(domain);

		try {
			int count = 0;
			do {
				builder.setOffset(count * SEARCH_BATCH_COUNT);
				DeionSearchResultEx deionSearchResultEx = deionSearchService.search(builder.build());
				List<DeionSearchResultItemEx> items = deionSearchResultEx.getItems();
				results = items != null ? getDocumentsFromItems.apply(items) : new ArrayList<>();
				answer.addAll(results);
				count++;
			} while (!results.isEmpty());
		} catch (Exception e) {
			// need to raise the exception or we risk caching incomplete/inaccurate results
			throw new GlobeException("Error while getting back results from Deion search for Google News Sitemap", e);
		}

		return answer;
	}
	
	private Function<List<DeionSearchResultItemEx>, List<BaseDocumentEx>> getDocumentsFromItems = (items) -> items
			.stream().filter(item -> item != null && item.getDocument() != null)
			.map(item -> item.getDocument())
			.collect(Collectors.toCollection(ArrayList::new));



}
