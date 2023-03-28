package com.about.mantle.model.feeds;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.RSS2.Item;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.about.mantle.model.feeds.datamodel.rss.RssFeedItem;
import com.about.mantle.model.services.AuthorService;
import com.about.mantle.model.services.feeds.api.RssFeedService;
import com.about.mantle.model.services.feeds.response.RssFeedSearchItem;
import com.about.mantle.model.tasks.TaxeneRelationTask;
import com.about.mantle.render.MantleRenderUtils;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

/**
 * Working implementation of base XML builder class. This class captures all
 * business requirements and logic to support Flipboard integration.
 */
public class FlipboardFeedBuilder extends BaseXmlFeedBuilder {

    public static final String FLIPBOARD_FEED_NAME = "flipboard";

    public FlipboardFeedBuilder(RssFeedService rssFeedSearchService, MantleRenderUtils renderUtils,
                                      Vertical vertical, AuthorService authorService, TaxeneRelationTask taxeneRelationTask,
                                      String feedDisplayName, String feedDescription) {
        super(rssFeedSearchService, renderUtils, vertical, authorService, taxeneRelationTask, feedDisplayName,
                feedDescription);
    }

    /**
     * Add additional attributes for item element in addition to what base builder
     * is already providing. For example category element can be set with parent
     * taxonomy short-heading.
     */
    @Override
    protected Item addCustomItemAttributes(RequestContext requestContext, RssFeedSearchItem searchResultItem,
                                           Item item) {
        if (searchResultItem.getDocument() != null) {
            ((RssFeedItem) item).setCategories(prepareItemCategories(searchResultItem.getDocument().getDocumentId()));
        }
        return item;
    }

    /**
     * Prepare and populate item's category element with respective document's
     * parent taxonomy short heading.
     */
    private List<String> prepareItemCategories(Long docId) {
        return ImmutableList.of(getTaxonomyShortHeading(docId));
    }

    @Override
    protected String prepareFeedItemDescription(BaseDocumentEx document) {
        return prepareExcerpt(document, 350);
    }

    /**
     * Prepare excerpt from SC HTML content blocks based on limit. Trail it with
     * ellipsis if text length is greater than asked character limit.
     */
    private String prepareExcerpt(BaseDocumentEx document, int characterLimit) {
        String preProcessedText = getTextFromFirstScHtmlBlock(document);
        String htmlFreeText = stripHtmlFromText(preProcessedText);
        StringBuilder answer = new StringBuilder();

        if (htmlFreeText.length() > characterLimit) {
            answer.append(htmlFreeText.substring(0, characterLimit));
            answer.append("...");
        } else {
            answer.append(htmlFreeText);
        }
        return answer.toString();
    }

    @Override
    protected Map<String, Object> provideAdditionalQueryParams(RequestContext requestContext) {
        return extractAndAddTaxonomyFeedQueryParamsIfRequired(requestContext, false);
    }
}