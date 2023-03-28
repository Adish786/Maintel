package com.about.mantle.model.feeds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.about.mantle.model.TaxeneLevelType;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.RSS2.Channel;
import com.about.mantle.model.RSS2.Item;
import com.about.mantle.model.RSS2.Item.Enclosure;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.Vertical;
import com.about.mantle.model.extended.docv2.TaggedImage.UsageFlag;
import com.about.mantle.model.extended.docv2.GuestAuthorEx;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.LinkEx;
import com.about.mantle.model.feeds.datamodel.rss.RssFeedItem;
import com.about.mantle.model.services.AuthorService;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.Direction;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.NodeType;
import com.about.mantle.model.services.TaxeneRelationService.TaxeneTraverseRequestContext.TraverseStrategy;
import com.about.mantle.model.services.feeds.api.RssFeedService;
import com.about.mantle.model.services.feeds.response.RssFeedSearchItem;
import com.about.mantle.model.tasks.TaxeneRelationTask;
import com.about.mantle.render.MantleRenderUtils;
import com.google.common.collect.ImmutableList;

/**
 * A common-builder class for XML feeds which contains majority of the shared functionality.
 *
 * TODO: Possibly inject authorService() and taxeneRelationTask()
 * Note that almost all verticals extend this class. This means they have inject authorService() and taxeneRelationTask()
 * when both usage and implementation live in Mantle. A better way would be to somehow inject these two dependencies
 * directly inside here via Spring app context or some other mechanism (@Autowired?) in order to keep it loosely coupled.
 */
public abstract class BaseXmlFeedBuilder extends XmlFeedBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseXmlFeedBuilder.class);
	private final AuthorService authorService;
	private final TaxeneRelationTask taxeneRelationTask;
	private String feedDisplayName;
	private String feedDescription;
	private String feedLink;

	public BaseXmlFeedBuilder(RssFeedService rssFeedSearchService, MantleRenderUtils renderUtils, Vertical vertical,
								AuthorService authorService, TaxeneRelationTask taxeneRelationTask, String feedDisplayName,
								String feedDescription) {
		this(rssFeedSearchService, renderUtils, vertical, authorService, taxeneRelationTask, feedDisplayName,
				feedDescription, null);
	}

	public BaseXmlFeedBuilder(RssFeedService rssFeedSearchService, MantleRenderUtils renderUtils, Vertical vertical,
							  AuthorService authorService, TaxeneRelationTask taxeneRelationTask, String feedDisplayName,
							  String feedDescription, String feedLink) {
		super(rssFeedSearchService, renderUtils, vertical);
		this.authorService = authorService;
		this.taxeneRelationTask = taxeneRelationTask;
		this.feedDisplayName = feedDisplayName;
		this.feedDescription = feedDescription;
		this.feedLink = feedLink;
	}

	@Override
	protected final Channel prepareChannel(RequestContext requestContext) {
		Channel channel = new Channel();
		channel.setTitle(feedDisplayName);
		channel.setLink(feedLink != null ? feedLink : "https://" + requestContext.getServerName());
		channel.setDescription(feedDescription);
		channel.setLanguage(requestContext.getLocale().getLanguage());

		return channel;
	}

	@Override
	protected final List<Item> prepareItems(RequestContext requestContext, List<RssFeedSearchItem> searchResultItems) {
		List<Item> items = new ArrayList<>();

		for (RssFeedSearchItem aResult : searchResultItems) {
			if (validateAndLogInvalid(aResult)) {
				Item anItem = new RssFeedItem();
				BaseDocumentEx aResultDoc = aResult.getDocument();

				anItem.setTitle(StringEscapeUtils.unescapeHtml4(aResultDoc.getBestTitle()));
				anItem.setLink(aResult.getUrl());
				anItem.setDescription(encloseWithCdata(prepareFeedItemDescription(aResultDoc)));
				anItem.setPubDate(prepareItemPubDate(aResult));
				anItem.setEnclosure(prepareItemEnclosure(requestContext, getRecircOrFallbackImage(aResultDoc)));
				/*
				 * According to RSS spec, "the <guid> element defines a unique identifier for
				 * the item". http://www.rssboard.org/rss-profile#element-channel-item-guid
				 * suggest using url of an item as unique identifier
				 */
				((RssFeedItem) anItem).setGuid(aResult.getUrl());
				((RssFeedItem) anItem).setCreator(prepareAuthorName(aResult));
				anItem = addCustomItemAttributes(requestContext, aResult, anItem);
				if (isValid(anItem)) {
					items.add(anItem);
				}
			}
		}
		return items;
	}

	/**
	 * Unlike parent implementation of @see
	 * {@link XmlFeedBuilder#addCustomItemAttributes}, this method allows extending
	 * classes to utilize requestContext object. E.g. requestContext is needed for
	 * image Thumbor url generation.
	 */
	protected Item addCustomItemAttributes(RequestContext requestContext, RssFeedSearchItem searchResultItem,
										   Item item) {
		return super.addCustomItemAttributes(searchResultItem, item);
	}

	/**
	 * Note: Document is considered mandatory for rss feeds. Certain
	 * mandatory fields are derived from it.
	 */
	@Override
	protected boolean isValidSearchResult(RssFeedSearchItem aResult) {
		boolean isValid;
		Set<String> missingFields = new HashSet<>();

		if (aResult.getDocument() == null) {
			missingFields.add("document");
		}
		if (StringUtils.isBlank(aResult.getUrl())) {
			missingFields.add("url");
		}
		isValid = missingFields.isEmpty();
		if (!isValid && LOGGER.isWarnEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Missing mandatory fields: ");
			sb.append(String.join(",", missingFields));
			LOGGER.warn(sb.toString());
		}
		return isValid;
	}

	@Override
	protected DateTime prepareItemPubDate(RssFeedSearchItem aResult) {
		return aResult.getDocument().getDates().getFirstPublished();
	}

	/**
	 * Prepare item description. This can be fed from any text resource. E.g.
	 * document meta description field. Or excerpt prepared from first n characters
	 * of SC HTML blocks. Current requirement asks for max of 350 characters.
	 */
	protected abstract String prepareFeedItemDescription(BaseDocumentEx document);

	/**
	 * Get RECIRC image. If not available then fall back to mantle default.
	 */
	protected ImageEx getRecircOrFallbackImage(BaseDocumentEx document) {
		ImageEx recircImage = document.getImageForUsage("RECIRC");
		return recircImage != null ? recircImage : document.getImageForUsage(UsageFlag.PRIMARY);
	}

	/**
	 * Selene returns `authorName` in the rss search result. But if we request full
	 * document then `authorName` is not included by default in the result item and
	 * we have to manually look it up. For manual lookup, we should give priority to
	 * guest author.
	 * 
	 */
	private String prepareAuthorName(RssFeedSearchItem result) {
		String authorName = "";
		BaseDocumentEx document = result.getDocument();

		// rssfeed endpoint may return author name directly
		authorName = result.getAuthorName();
		// GuestAuthor supersedes author or bylines / taglines
		if (StringUtils.isBlank(authorName)) {
			authorName = getGuestAuthorName(document);
		}
		// If no guest author, comma delimit AUTHOR type attribution author names together
		if (StringUtils.isBlank(authorName)) {
			authorName = getAuthorNameFromSelene(document);
		}
		return authorName;
	}

	/**
	 * Extract display name for guest author
	 */
	private String getGuestAuthorName(BaseDocumentEx document) {
		String guestAuthorName = "";
		GuestAuthorEx guestAuthor = document.getGuestAuthor();

		if (guestAuthor != null) {
			LinkEx guestAuthorLink = guestAuthor.getLink();
			if (guestAuthorLink != null && StringUtils.isNotBlank(guestAuthorLink.getText())) {
				guestAuthorName = guestAuthorLink.getText();
			}
		}
		return guestAuthorName;
	}

	/**
	 * Fetch author name from the Selene author service from author attributions
	 * extracted from the full document per search result item.
	 */
	private String getAuthorNameFromSelene(BaseDocumentEx document) {
		String authorName = "";

		try {
			List<String> authorNames = Stream.concat(document.getBylines().stream(),
					document.getTaglines().stream())
					.filter(byline -> "AUTHOR".equals(byline.getType()))
					.map(byline -> byline.getAuthorId())
					.distinct()
					.map(authorId -> authorService.getAuthorById(authorId))
					.filter(author -> StringUtils.isNotBlank(author.getDisplayName()))
					.map(author -> author.getDisplayName())
					.collect(Collectors.toList());

			authorName = String.join(", ", authorNames);

		} catch (Exception ex) {
			LOGGER.warn("Error while getting author info back from Selene", ex);
		}
		return authorName;
	}

	/**
	 * Return short-heading for the taxonomy parent of this document. Traversing all parent documents because a document's parent could be a programmed summary.
	 */
	protected String getTaxonomyShortHeading(Long docId) {
		TaxeneNodeEx taxeneNode = null;
		String shortHeading = "";

		try {
			taxeneNode = taxeneRelationTask.traverse(docId, Direction.OUT, TraverseStrategy.BREADTH_FIRST, "primaryParent",
					NodeType.INTERNAL_NODE, null, false, true, "{(templateType, shortHeading)}", null);
		} catch (Exception ex) {
			LOGGER.warn("Error while getting taxonomy data back from Selene", ex);
		}
		if (taxeneNode != null && taxeneNode.getRelationships().getTotalSize() >= 1) {
			BaseDocumentEx parentTaxonomyDoc = getParentTaxonomyDocument(taxeneNode);
			if (parentTaxonomyDoc != null && StringUtils.isNotBlank(parentTaxonomyDoc.getShortHeading())) {
				shortHeading = parentTaxonomyDoc.getShortHeading();
			}
		}

		return shortHeading;
	}

	private BaseDocumentEx getParentTaxonomyDocument(TaxeneNodeEx taxeneNode) {
		TaxeneLevelType taxLevel = taxeneRelationTask.getLevel(taxeneNode);
		TaxeneNodeEx parentNode = taxeneRelationTask.getAncestorNode(taxeneNode, taxLevel.getLevelId());
		return parentNode.getDocument();
	}

	@Override
	protected String provideSortParameter() {
		return "firstPublished DESC";
	}

	@Override
	protected Map<String, Object> provideAdditionalQueryParams(RequestContext requestContext) {
		return Collections.emptyMap();
	}

	/**
	 * This feed requires either {@code
	 *             STRUCTUREDCONTENT} or {@code LISTSC} as {@code templateType}.
	 * Note: It cannot use base class's implementation because it takes two template
	 * types as input.
	 */
	@Override
	protected String provideMandatoryTemplatetypeFilterQuery() {
		return "templateType:STRUCTUREDCONTENT OR templateType:LISTSC";
	}

	/**
	 * Override to last 7 days (Selene's firstPublished date field).
	 */
	@Override
	protected String dateRangeFilterQuery() {
		return "firstPublished:[NOW-7DAYS TO NOW]";
	}

	/**
	 * Query SOLR to return SC documents which have at least one HTML content block.
	 */
	@Override
	protected List<String> provideAdditionalFeedSpecificFilterQueries(RequestContext requestContext) {
		return ImmutableList.of("contentType:HTML", "-viewType:CORPORATE");
	}

	@Override
	protected boolean searchShouldIncludeFullDocuments() {
		return true;
	}

	/**
	 * Verify that certain attributes are present in item before discarding a feed
	 * item.
	 */
	protected boolean isValid(Item item) {
		return isValidEnclosure(item.getEnclosure()) && isValidTitle(item.getTitle()) && isValidLink(item.getLink());
	}

	/**
	 * If an image is missing then this item is not valid.
	 */
	private boolean isValidEnclosure(Enclosure enclosure) {
		return enclosure != null;
	}

	/**
	 * If title is missing then this item is not valid.
	 */
	private boolean isValidTitle(String title) {
		return StringUtils.isNotBlank(title);
	}

	/**
	 * If link is missing then this item is not valid.
	 */
	private boolean isValidLink(String link) {
		return StringUtils.isNotBlank(link);
	}

	@Override
	protected String getFeedDisplayName() {
		return feedDisplayName;
	}
}
