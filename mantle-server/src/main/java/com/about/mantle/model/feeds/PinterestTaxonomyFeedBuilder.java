package com.about.mantle.model.feeds;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.RSS2.Item;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.TaggedImage;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentImageEx.StructuredContentImageDataEx;
import com.about.mantle.model.feeds.datamodel.rss.MultiEnclosureRssFeedItem;
import com.about.mantle.model.feeds.datamodel.rss.RssFeedItem;
import com.about.mantle.model.services.AuthorService;
import com.about.mantle.model.services.feeds.api.RssFeedService;
import com.about.mantle.model.services.feeds.response.RssFeedSearchItem;
import com.about.mantle.model.tasks.TaxeneRelationTask;
import com.about.mantle.render.MantleRenderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides a Pinterest feed with articles restricted to a given taxonomy.
 */
public class PinterestTaxonomyFeedBuilder extends BaseXmlFeedBuilder {

	public static final String PINTEREST_TAXONOMY_FEED_NAME = "pinterest-taxonomy";


	public PinterestTaxonomyFeedBuilder(RssFeedService rssFeedSearchService, MantleRenderUtils renderUtils,
										Vertical vertical, AuthorService authorService, TaxeneRelationTask taxeneRelationTask,
										String feedDisplayName, String feedDescription, String feedLink) {
		super(rssFeedSearchService, renderUtils, vertical, authorService, taxeneRelationTask, feedDisplayName,
				feedDescription, feedLink);
	}

	@Override
	protected String prepareFeedItemDescription(BaseDocumentEx document) {
		return getDescriptionTextFromDocumentSummary(document);
	}

	/**
	 * Fetch newly written articles or material publish in last 4 days.
	 */
	@Override
	protected String dateRangeFilterQuery() {
		return "firstPublished:[NOW-4DAYS TO NOW] OR displayed:[NOW-4DAYS TO NOW]";
	}

	@Override
	protected String provideSortParameter() {
		return "displayed DESC";
	}

	@Override
	protected Map<String, Object> provideAdditionalQueryParams(RequestContext requestContext) {
		return extractAndAddTaxonomyFeedQueryParamsIfRequired(requestContext, true);
	}

	@Override
	protected Item addCustomItemAttributes(RequestContext requestContext, RssFeedSearchItem searchResultItem,
										   Item item) {
		MultiEnclosureRssFeedItem multiEnclosureRssFeedItem = new MultiEnclosureRssFeedItem((RssFeedItem) item);
		multiEnclosureRssFeedItem.setEnclosures(prepareItemInlineImages(requestContext, searchResultItem.getDocument()));
		return multiEnclosureRssFeedItem;
	}


	/**
	 * Add inline Images. If there are no inline images then add the pinterest image
	 * Note we are only adding structured content and listSC docs as that's what the feed looks for
	 */
	private List<Item.Enclosure> prepareItemInlineImages(RequestContext requestContext, BaseDocumentEx document) {
		List<Item.Enclosure> inlineImages = new ArrayList<>();

		// Covers ListSC and SC docs
		if (document instanceof StructuredContentBaseDocumentEx) {
			((StructuredContentBaseDocumentEx) document).getContentsStreamOfType("IMAGE").forEach(aBlock ->
					inlineImages.add(prepareItemEnclosure(requestContext, ((StructuredContentImageDataEx) aBlock.getData()).getImage())));
		}

		ImageEx pinterestImage = document.getImageForUsage(TaggedImage.UsageFlag.PINTEREST);
		if (inlineImages.size() == 0 && pinterestImage != null) {
			inlineImages.add(prepareItemEnclosure(requestContext, pinterestImage));
		}

		return inlineImages;
	}
}