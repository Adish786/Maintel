package com.about.mantle.model.feeds;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.about.hippodrome.config.CommonPropertyFactory;
import com.about.mantle.app.MantleExternalConfigKeys;
import com.google.common.collect.ImmutableList;
import com.netflix.archaius.api.PropertyFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.RSS2.Item;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHeadingEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHtmlEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHtmlEx.StructuredContentHtmlDataEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentImageEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentImageEx.StructuredContentImageDataEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentSubheadingEx;
import com.about.mantle.model.feeds.datamodel.rss.RssFeedItem;
import com.about.mantle.model.services.AuthorService;
import com.about.mantle.model.services.feeds.api.RssFeedService;
import com.about.mantle.model.services.feeds.response.RssFeedSearchItem;
import com.about.mantle.model.tasks.TaxeneRelationTask;
import com.about.mantle.render.MantleRenderUtils;

/**
 * This class extends base XML builder class and implements additional business logic to support Google producer news.
 * This was moved directly from Beauty to avoid breaking currently published feeds. While the dateRangeFilterQuery and
 * prepareItemPubDate are beauty-specific, the remaining functions should be general to any google news feeds.
 * This class should be cleaned up under ticket GLBE-7309.
 */
public class GoogleNewsFeedBuilder extends BaseXmlFeedBuilder {
	private static final Pattern INLINE_STYLE_PATTERN = Pattern.compile(" *style=\"[^\"]*\"");

	public static final String GOOGLE_NEWS_FEED_NAME = "google-news";

	// TODO this is a quick workaround for an SEO fire.  Needs to be revisited; properties should be injected
	// into the constructor
	protected static PropertyFactory PROPERTY_FACTORY = CommonPropertyFactory.INSTANCE.get();

	public GoogleNewsFeedBuilder(RssFeedService rssFeedSearchService, MantleRenderUtils renderUtils,
									   Vertical vertical, AuthorService authorService, TaxeneRelationTask taxeneRelationTask,
									   String feedDisplayName, String feedDescription) {
		super(rssFeedSearchService, renderUtils, vertical, authorService, taxeneRelationTask,
				PROPERTY_FACTORY.getProperty(MantleExternalConfigKeys.RSS_NEWS_NAME).asString(feedDisplayName).get(),
				PROPERTY_FACTORY.getProperty(MantleExternalConfigKeys.RSS_NEWS_DESCRIPTION).asString(feedDescription).get(),
				PROPERTY_FACTORY.getProperty(MantleExternalConfigKeys.RSS_NEWS_LINK).asString(null).get());
	}

	public GoogleNewsFeedBuilder(RssFeedService rssFeedSearchService, MantleRenderUtils renderUtils,
								 Vertical vertical, AuthorService authorService, TaxeneRelationTask taxeneRelationTask,
								 String feedDisplayName, String feedDescription, String feedLink) {
		super(rssFeedSearchService, renderUtils, vertical, authorService, taxeneRelationTask, feedDisplayName, feedDescription, feedLink);
	}

	/**
	 * Fetch newly written articles in last 21 days.
	 */
	@Override
	protected String dateRangeFilterQuery() {
		return "firstPublished:[NOW-21DAYS TO NOW]";
	}

	/**
	 * Adds check for newsType to SOLR query.
	 */
	@Override
	protected List<String> provideAdditionalFeedSpecificFilterQueries(RequestContext requestContext) {
		List<String> parentFilters = super.provideAdditionalFeedSpecificFilterQueries(requestContext);
		List<String> filters = new ArrayList<>();
		filters.addAll(parentFilters);
		filters.add("newsType: [* TO *]");
		return ImmutableList.copyOf(filters);
	}

	/**
	 * Displaying {@code'firstPublished'} date on {@code '<item>'}
	 */
	@Override
	protected DateTime prepareItemPubDate(RssFeedSearchItem aResult) {
		return aResult.getDocument().getDates().getFirstPublished();
	}

	@Override
	protected String prepareFeedItemDescription(BaseDocumentEx document) {
		return getDescriptionTextFromDocumentSummary(document);
	}

	@Override
	protected Item addCustomItemAttributes(RequestContext requestContext, RssFeedSearchItem searchResultItem,
										   Item item) {
		if (searchResultItem != null && searchResultItem.getDocument() != null && item instanceof RssFeedItem) {
			((RssFeedItem) item)
					.setContent(encloseWithCdata(prepareItemContent(requestContext, searchResultItem.getDocument())));
		}
		return item;
	}

	/**
	 * Add full article content. Note: As of now, only HTML, HEADING, SUBHEADING, and IMAGE SC
	 * blocks are being aggregated. Also, add PRIMARY image as article hero image.
	 */
	private String prepareItemContent(RequestContext requestContext, BaseDocumentEx document) {
		StringBuilder contentBuilder = new StringBuilder("");
		if (document instanceof StructuredContentBaseDocumentEx) {
			contentBuilder.append(prepareFigureElement(requestContext, imageSourceForPrimaryHeroImage(document), true));

			((StructuredContentBaseDocumentEx) document).getContentsStream().forEach(aBlock -> {
				if (aBlock instanceof StructuredContentHeadingEx) {
					contentBuilder
							.append(stripInlineStyles(encloseScHeadingWithH2Tags((StructuredContentHeadingEx) aBlock)));
				} else if (aBlock instanceof StructuredContentSubheadingEx) {
					contentBuilder.append(
							stripInlineStyles(encloseScSubheadingWithH3Tags((StructuredContentSubheadingEx) aBlock)));
				} else if (aBlock instanceof StructuredContentHtmlEx) {
					contentBuilder.append(((StructuredContentHtmlDataEx) aBlock.getData()).getHtml());
				} else if (aBlock instanceof StructuredContentImageEx) {
					contentBuilder.append(prepareFigureElement(requestContext,
							((StructuredContentImageDataEx) aBlock.getData()).getImage(), false));
				}
			});
		}
		return decodeHtmlText(contentBuilder.toString());
	}

	private ImageEx imageSourceForPrimaryHeroImage(BaseDocumentEx document) {
		ImageEx primaryImage = document.getImageForUsage("PRIMARY");
		if (ImageEx.EMPTY.equals(primaryImage)) {
			primaryImage = null;
		}
		return primaryImage;
	}

	/**
	 * Enclosed inline image (SC IMAGE block) in '<figure>' element see:
	 * https://support.google.com/news/producer/answer/6170026?hl=en&ref_topic=6179927#Images
	 */
	private String prepareFigureElement(RequestContext requestContext, ImageEx image, boolean isPrimaryImage) {
		StringBuilder figureElement = new StringBuilder();
		StringBuilder figureElementBody = new StringBuilder();

		if (image != null && !image.equals(ImageEx.EMPTY)) {
			figureElementBody.append(prepareImgTag(requestContext, image, isPrimaryImage));
			figureElementBody.append(prepareFigureCaptionElement(image));
		}

		if (StringUtils.isNotBlank(figureElementBody)) {
			figureElement.append("<figure>");
			figureElement.append(figureElementBody.toString());
			figureElement.append("</figure>");
		}
		return figureElement.toString();
	}

	/**
	 * @isPrimaryImage Note: Somewhat ambiguous usage of 'isPrimaryImage' word, what
	 *                 Google refers to primary image (hero image) is RECIRC image
	 *                 for us.
	 */
	private String prepareImgTag(RequestContext requestContext, ImageEx image, boolean isPrimaryImage) {
		StringBuilder imgTag = new StringBuilder();

		if (StringUtils.isNotBlank(image.getObjectId())) {
			String generatedUrl = generateImageUrl(requestContext, image);

			if (StringUtils.isNotBlank(generatedUrl)) {

				imgTag.append("<img");
				imgTag.append(" src=\"" + generatedUrl + "\"");

				if (isPrimaryImage) {
					imgTag.append(" class=\"type:primaryImage\"");
				}

				if (StringUtils.isNotBlank(image.getAlt())) {
					imgTag.append(" alt=\"" + StringEscapeUtils.escapeHtml4(image.getAlt()) + "\"");
				}

				if (image.getWidth() != null) {
					imgTag.append(" width=\"" + image.getWidth() + "\"");
				}

				if (image.getHeight() != null) {
					imgTag.append(" height=\"" + image.getHeight() + "\"");
				}
				imgTag.append(" />");
			}
		}
		return imgTag.toString();
	}

	private String prepareFigureCaptionElement(ImageEx image) {
		StringBuilder figureCaptionElement = new StringBuilder();
		StringBuilder figureCaptionElementBody = new StringBuilder();

		if (StringUtils.isNotBlank(image.getCaption())) {
			figureCaptionElementBody.append(image.getCaption());
		}
		if (StringUtils.isNotBlank(image.getOwner())) {
			figureCaptionElementBody
					.append("<span class=\"copyright\">" + normalizeString(image.getOwner()) + "</span>");
		}

		if (StringUtils.isNotBlank(figureCaptionElementBody)) {
			figureCaptionElement.append("<figcaption>");
			figureCaptionElement.append(figureCaptionElementBody);
			figureCaptionElement.append("</figcaption>");
		}
		return figureCaptionElement.toString();
	}

	/**
	 *
	 * Normalize string by: 1) Removing HTML, 2) Condensing multiple spaces to one
	 * space, 3) Removing leading + trailing space(s).
	 */
	private String normalizeString(String str) {
		return StringUtils.strip(StringUtils.normalizeSpace(stripHtmlFromText(str)));
	}

	/**
	 * Returns html content without any inline styling (e.g. 'style="..."')
	 *
	 * @param html the html content to strip inline styles from
	 * @return the same html content without style="..."
	 */
	private String stripInlineStyles(String html) {
		String inlineStyleStrippedHtml = html;

		if (html.contains("style=\"")) {
			inlineStyleStrippedHtml = INLINE_STYLE_PATTERN.matcher(inlineStyleStrippedHtml).replaceAll("");
		}

		return inlineStyleStrippedHtml;
	}
}