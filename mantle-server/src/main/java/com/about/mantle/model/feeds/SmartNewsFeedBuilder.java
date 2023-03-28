package com.about.mantle.model.feeds;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.AuthorService;
import com.about.mantle.model.services.feeds.api.RssFeedService;
import com.about.mantle.model.services.feeds.response.RssFeedSearchItem;
import com.about.mantle.model.tasks.TaxeneRelationTask;
import com.about.mantle.render.MantleRenderUtils;

import java.util.List;

/**
 * Will be updated if necessary as part of AXIS-1658
 */
public class SmartNewsFeedBuilder extends GoogleNewsFeedBuilder {

    public SmartNewsFeedBuilder(RssFeedService rssFeedSearchService, MantleRenderUtils renderUtils, BaseDocumentEx.Vertical vertical, AuthorService authorService, TaxeneRelationTask taxeneRelationTask, String feedDisplayName, String feedDescription, String feedLink) {
        super(rssFeedSearchService, renderUtils, vertical, authorService, taxeneRelationTask, feedDisplayName, feedDescription, feedLink);
    }

    /**
     * This method is overridden because we want to filter sponsored items
     * for the Smart News feed but not the Google News feed
     * @return List of {@link RssFeedSearchItem}
     */
    @Override
    protected List<RssFeedSearchItem> acquireItems(RequestContext requestContext) {
        List<RssFeedSearchItem> searchItems = super.acquireItems(requestContext);
        return filterSponsoredItems(searchItems);
    }
}
