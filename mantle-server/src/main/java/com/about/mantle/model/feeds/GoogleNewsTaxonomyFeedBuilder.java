package com.about.mantle.model.feeds;

import java.util.List;
import java.util.Map;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.about.mantle.model.services.AuthorService;
import com.about.mantle.model.services.feeds.api.RssFeedService;
import com.about.mantle.model.tasks.TaxeneRelationTask;
import com.about.mantle.render.MantleRenderUtils;
import com.google.common.collect.ImmutableList;

/**
 * Provides a Google News feed with articles restricted to a given taxonomy. This class is moved directly from beauty
 * as larger refactoring of RSS feeds in Mantle is planned.
 * It may be preferable to merge this with the GoogleNewsFeedBuilder, as otherwise verticals could not override the
 * vertical-specific functionality of GoogleNewsFeedBuilder and still easily leverage the taxonomy feed builder.
 * This class should be cleaned up under ticket GLBE-7309.
 */
public class GoogleNewsTaxonomyFeedBuilder extends GoogleNewsFeedBuilder {

	public static final String GOOGLE_NEWS_TAXONOMY_FEED_NAME = "taxonomy";

	public GoogleNewsTaxonomyFeedBuilder(RssFeedService rssFeedSearchService, MantleRenderUtils renderUtils,
											   Vertical vertical, AuthorService authorService, TaxeneRelationTask taxeneRelationTask,
											   String feedDisplayName, String feedDescription) {
		super(rssFeedSearchService, renderUtils, vertical, authorService, taxeneRelationTask, feedDisplayName,
				feedDescription);
	}

	public GoogleNewsTaxonomyFeedBuilder(RssFeedService rssFeedSearchService, MantleRenderUtils renderUtils,
										 Vertical vertical, AuthorService authorService, TaxeneRelationTask taxeneRelationTask,
										 String feedDisplayName, String feedDescription, String feedLink) {
		super(rssFeedSearchService, renderUtils, vertical, authorService, taxeneRelationTask, feedDisplayName,
				feedDescription, feedLink);
	}

	/**
	 * Append taxonomy filter query to existing list of filter queries determined by
	 * upstream class(es).
	 */
	@Override
	protected List<String> provideAdditionalFeedSpecificFilterQueries(RequestContext requestContext) {
		return ImmutableList.<String>builder()
				.addAll(super.provideAdditionalFeedSpecificFilterQueries(requestContext))
				.build();
	}

	@Override
	protected Map<String, Object> provideAdditionalQueryParams(RequestContext requestContext) {
		return extractAndAddTaxonomyFeedQueryParamsIfRequired(requestContext, true);
	}
}