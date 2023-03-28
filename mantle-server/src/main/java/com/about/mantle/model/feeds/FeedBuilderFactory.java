package com.about.mantle.model.feeds;

import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.about.mantle.model.services.feeds.api.RssFeedService;
import com.about.mantle.render.MantleRenderUtils;

@Deprecated
public abstract class FeedBuilderFactory {

	protected final RssFeedService rssFeedSearchService;
	protected final MantleRenderUtils renderUtils;
	protected final Vertical vertical;

	public FeedBuilderFactory(RssFeedService rssFeedSearchService, MantleRenderUtils renderUtils, Vertical vertical) {
		this.rssFeedSearchService = rssFeedSearchService;
		this.renderUtils = renderUtils;
		this.vertical = vertical;
	}

	/**
	 * By incorporate implementing vertical logic, provide a builder class for
	 * corresponding feed.
	 */
	public abstract FeedBuilder create(String feedname);
}