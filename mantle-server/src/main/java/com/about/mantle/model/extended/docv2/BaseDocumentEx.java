package com.about.mantle.model.extended.docv2;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.stripToNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.about.hippodrome.config.CommonPropertyFactory;
import com.about.hippodrome.config.HippodromePropertyFactory;
import org.joda.time.DateTime;

import com.about.globe.core.exception.GlobeInvalidTaskParameterException;
import com.about.mantle.htmlslicing.HtmlSlice;
import com.about.mantle.infocat.model.InfoCatRecordPair;
import com.about.mantle.model.extended.AuthorEx;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.TaggedImage.UsageFlag;
import com.about.mantle.model.extended.docv2.brightcovevideo.BrightcoveVideoDocumentEx;
import com.about.mantle.model.extended.docv2.sc.AmazonOSPDocumentEx;
import com.about.mantle.model.extended.docv2.sc.EntityReferenceDocumentEx;
import com.about.mantle.model.extended.docv2.sc.HowToStructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.ListStructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.ReviewStructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.TaxonomyStructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.TermStructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.recipesc.RecipeStructuredContentDocumentEx;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, visible = true, include = As.EXISTING_PROPERTY, property = "templateType")
@JsonSubTypes({ @Type(value = AmazonOSPDocumentEx.class, name = "AMAZONOSP"),
		@Type(value = BioDocumentEx.class, name = "BIO"),
		@Type(value = BrightcoveVideoDocumentEx.class, name = "BRIGHTCOVEVIDEO"),
		@Type(value = FlexibleArticleDocumentEx.class, name = "FLEXIBLEARTICLE"),
		@Type(value = ImageGalleryDocumentEx.class, name = "IMAGEGALLERY"),
		@Type(value = JwPlayerVideoDocumentEx.class, name = "JWPLAYERVIDEO"),
		@Type(value = LandingPageDocumentEx.class, name = "LANDINGPAGE"),
		@Type(value = LegacyDocumentEx.class, name = "LEGACY"),
		@Type(value = LegacyDocumentEx.class, name = "LEGACY_NO_CONTAINER"),
		@Type(value = ListDocumentEx.class, name = "LIST"),
		@Type(value = ProgrammedSummaryDocumentEx.class, name = "PROGRAMMEDSUMMARY"),
		@Type(value = QuizDocumentEx.class, name = "QUIZ"),
		@Type(value = RecipeDocumentEx.class, name = "RECIPE"),
		@Type(value = RedirectDocumentEx.class, name = "REDIRECT"),
        @Type(value = ReviewStructuredContentDocumentEx.class, name = "REVIEWSC"),
		@Type(value = StepByStepDocumentEx.class, name = "STEPBYSTEP"),
		@Type(value = StructuredContentDocumentEx.class, name = "STRUCTUREDCONTENT"),
		@Type(value = ListStructuredContentDocumentEx.class, name = "LISTSC"),
		@Type(value = RecipeStructuredContentDocumentEx.class, name = "RECIPESC"),
		@Type(value = TermStructuredContentDocumentEx.class, name ="TERMSC"),
		@Type(value = TaxonomyDocumentEx.class, name = "TAXONOMY"),
		@Type(value = TopicDocumentEx.class, name = "TOPIC"),
		@Type(value = TopicMapDocumentEx.class, name = "TOPICMAP"),
		@Type(value = UserPathDocumentEx.class, name = "USERPATH"),
		@Type(value = HowToStructuredContentDocumentEx.class, name = "HOWTO"),
		@Type(value = TaxonomyStructuredContentDocumentEx.class, name = "TAXONOMYSC"),
		@Type(value = EntityReferenceDocumentEx.class, name = "ENTITYREFERENCE")})
public abstract class BaseDocumentEx implements Serializable {

	private static final long serialVersionUID = 1L;

	private final HippodromePropertyFactory propertyFactory = CommonPropertyFactory.INSTANCE.get();

	private static final List<Vertical> CARBON_VERTICALS = Arrays.asList(Vertical.CARBON, Vertical.PEOPLE,
			Vertical.ENTERTAINMENTWEEKLY, Vertical.PEOPLEENESPANOL, Vertical.ALLRECIPES, Vertical.MYRECIPES,
			Vertical.EATINGWELL, Vertical.FOODANDWINE, Vertical.BHG, Vertical.MARTHASTEWART, Vertical.REALSIMPLE,
			Vertical.SOUTHERNLIVING, Vertical.INSTYLE, Vertical.HELLOGIGGLES, Vertical.MYWEDDING, Vertical.MORE,
			Vertical.SHAPE, Vertical.HEALTHCOM, Vertical.PARENTS, Vertical.PARENTING, Vertical.SERPADRES,
			Vertical.TRAVELANDLEISURE, Vertical.RACHAELRAYMAG, Vertical.SIEMPREMUJER, Vertical.AGRICULTURE,
			Vertical.WOODMAGAZINE, Vertical.ALLPEOPLEQUILT, Vertical.COOKINGLIGHT, Vertical.MIDWESTLIVING,
			Vertical.DAILYPAWS);

	private String key;
	private String revisionKey;
	private String authorKey;
	private String actorId;
	private String actorGid;
	private String lastEditingAuthorId;
	private String lastEditingUserId;
	private String ownerKey;
	private String url;
	private String rootUrl;
	private Boolean legacyDocument;
	private TemplateTypeEx templateType;
	private DocumentState documentState;
	private Dates dates;
	private Boolean medicalBoardApproved;
	private String editingAuthorKey;
	private Boolean noIndex;
	private Boolean hidden;
	private String title;
	private String socialTitle;
	private SliceableListEx<String> keywords = SliceableListEx.emptyList();
	private String metaDescription;
	private String description;
	private String promoDescription;
	private String heading;
	private AuthorEx author;
	private Stamp stamp;
	private SummaryEx summary = SummaryEx.EMPTY;
	private MetaDataEx metaData = MetaDataEx.empty();
	private Long documentId;
	private String slug;
	private PrimaryVideoRefDocumentEx primaryVideo;
	/**
	 * @deprecated Use {@link #sponsors} instead
	 */
	@Deprecated
	private Sponsor sponsor;
	private SliceableListEx<Sponsor> sponsors = SliceableListEx.emptyList();
	private SliceableListEx<String> sources = SliceableListEx.emptyList();
	private SliceableListEx<Citation> citations = SliceableListEx.emptyList();
	private SliceableListEx<DocumentAttribution> bylines = SliceableListEx.emptyList();
	private SliceableListEx<DocumentAttribution> taglines = SliceableListEx.emptyList();
	private Vertical vertical;
	private SliceableListEx<String> tickers = SliceableListEx.emptyList();
	private String viewType;
	private SliceableListEx<TaggedImage> taggedImages = SliceableListEx.emptyList();
	private Map<String, ImageEx> taggedImageMap;
	
	private List<InfoCatRecordPair> infoCatProductRecords;

	//TODO: Add default of false when hasTableOfContents is removed from MetaDataEx (https://iacpublishing.atlassian.net/browse/GLBE-5717)
	private Boolean hasTableOfContents;
	private Boolean hasSummaryList = true;

	private Disclaimer disclaimer;
	private String methodology;
	private String revenueGroup;
	private String newsType;
	private String webPageSchemaType;
	private Boolean isOpinionContent;

	private LegacyMeredithEx legacyMeredith;

	private SliceableListEx<String> entityReferenceTags;

	// NOTE: GuestAuthor is technically _not_ part of BaseDocument but we include it here for convenience.
	private GuestAuthorEx guestAuthor;

	// NOTE: shortHeading is technically _not_ part of BaseDocument but we include it here for convenience.
	private String shortHeading;

	// NOTE: subheading is technically _not_ part of BaseDocument but we include it here for convenience.
	private String subheading;

	// NOTE: primaryAndSecondaryAncestors is _not_ part of BaseDocument but we include it here
	// as it may be set in a preprocessor if the feature is enabled via config, otherwise will remain null.
	private SliceableListEx<Long> primaryAndSecondaryAncestors;

	public BaseDocumentEx() {
	}

	public BaseDocumentEx(BaseDocumentEx document) {
		this.key = document.getKey();
		this.revisionKey = document.getRevisionKey();
		this.authorKey = document.getAuthorKey();
		this.actorId = document.getActorId();
		this.actorGid = document.getActorGid();
		this.lastEditingAuthorId = document.getLastEditingAuthorId();
		this.lastEditingUserId = document.getLastEditingUserId();
		this.ownerKey = document.getOwnerKey();
		this.url = document.getUrl();
		this.rootUrl = document.getRootUrl();
		this.legacyDocument = document.getLegacyDocument();
		this.templateType = document.getTemplateType();
		this.documentState = document.getDocumentState();
		this.dates = document.getDates();
		this.medicalBoardApproved = document.getMedicalBoardApproved();
		this.editingAuthorKey = document.getEditingAuthorKey();
		this.noIndex = document.getNoIndex();
		this.title = document.getTitle();
		this.socialTitle = document.getSocialTitle();
		this.keywords = document.getKeywords();
		this.metaDescription = document.getMetaDescription();
		this.description = document.getDescription();
		this.promoDescription = document.getPromoDescription();
		this.heading = document.getHeading();
		this.subheading = document.getSubheading();
		this.author = document.getAuthor();
		this.stamp = document.getStamp();
		this.summary = document.getSummary();
		this.documentId = document.getDocumentId();
		this.slug = document.getSlug();
		this.primaryVideo = document.getPrimaryVideo();
		this.sponsor = document.getSponsor();
		this.sponsors = document.getSponsors();
		this.sources = document.getSources();
		this.citations = document.getCitations();
		this.bylines = document.getBylines();
		this.taglines = document.getTaglines();
		this.hasTableOfContents = document.getHasTableOfContents();
		this.hasSummaryList = document.getHasSummaryList();
		this.vertical = document.getVertical();
		this.tickers = document.getTickers();
		this.viewType = document.getViewType();
		this.taggedImages = document.taggedImages;
		this.taggedImageMap = document.taggedImageMap;
		this.disclaimer = document.disclaimer;
		this.methodology = document.methodology;
		this.revenueGroup = document.revenueGroup;
		this.newsType = document.newsType;
		this.guestAuthor = document.guestAuthor;
		this.shortHeading = document.shortHeading;
		this.infoCatProductRecords = document.getInfoCatProductRecords();
		this.webPageSchemaType = document.getWebPageSchemaType();
		this.isOpinionContent = document.getIsOpinionContent();
      	this.metaData = document.getMetaData();
	    this.primaryAndSecondaryAncestors = document.getPrimaryAndSecondaryAncestors();
		this.legacyMeredith = document.getLegacyMeredith();
		this.entityReferenceTags = document.getEntityReferenceTags();
	}

	@JsonIgnore
	private String getUrlTitle() {
		return getSlug() != null ? getSlug().replace('-', ' ') : null;
	}

	@JsonIgnore
	public int getCharacterCount() throws GlobeInvalidTaskParameterException {
		return getRemainingLength(0, 0);
	}

	@JsonIgnore
	public int getRemainingLength(int currentBlock) throws GlobeInvalidTaskParameterException {
		return getRemainingLength(currentBlock, 0);
	}

	@JsonIgnore
	public int getRemainingLength(int currentBlock, int currentPage) throws GlobeInvalidTaskParameterException {
		return getRemainingLength(currentBlock, currentPage, false);
	}

	@JsonIgnore
	public int getRemainingLength(int currentBlock, int currentPage, boolean currentPageOnly)
			throws GlobeInvalidTaskParameterException {

		return 0;
	}

	@JsonIgnore
	protected int getRemainingLength(List<HtmlSlice> blocks, int currentBlock) {
		int length = 0;
		for (int i = currentBlock; i < blocks.size(); i++) {
			length += blocks.get(i).getCharacterCount();
		}
		return length;
	}

	@JsonIgnore
	public List<CategoryLinkEx> getInNetworkCategoryLinks() {
		return Collections.emptyList();
	}

	@JsonIgnore
	public List<CategoryLinkEx> getOffNetworkCategoryLinks() {
		return Collections.emptyList();
	}

	@JsonIgnore
	public boolean isReview() {
		return false;
	}

	@JsonIgnore
	public int calculateImageCount() {
		return 0;
	}

	public String getRevenueGroup() {
		return revenueGroup;
	}

	public void setRevenueGroup(String revenueGroup) {
		this.revenueGroup = revenueGroup;
	}

	public String getNewsType() {
		return newsType;
	}

	public void setNewsType(String newsType) {
		this.newsType = newsType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getRevisionKey() {
		return revisionKey;
	}

	public void setRevisionKey(String revisionKey) {
		this.revisionKey = revisionKey;
	}

	public String getAuthorKey() {
		return authorKey;
	}

	public void setAuthorKey(String authorKey) {
		this.authorKey = authorKey;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getLastEditingAuthorId() {
		return lastEditingAuthorId;
	}

	public void setLastEditingAuthorId(String lastEditingAuthorId) {
		this.lastEditingAuthorId = lastEditingAuthorId;
	}

	public String getLastEditingUserId() {
		return lastEditingUserId;
	}

	public void setLastEditingUserId(String lastEditingUserId) {
		this.lastEditingUserId = lastEditingUserId;
	}

	public String getOwnerKey() {
		return ownerKey;
	}

	public void setOwnerKey(String ownerKey) {
		this.ownerKey = ownerKey;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRootUrl() {
		return rootUrl;
	}

	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}

	public Boolean getLegacyDocument() {
		return legacyDocument;	
	}

	public void setLegacyDocument(Boolean legacyDocument) {
		this.legacyDocument = legacyDocument;
	}

	public TemplateTypeEx getTemplateType() {
		return templateType;
	}

	public void setTemplateType(TemplateTypeEx templateType) {
		this.templateType = templateType;
	}

	public DocumentState getDocumentState() {
		return documentState;
	}

	public void setDocumentState(DocumentState documentState) {
		this.documentState = documentState;
	}

	public Dates getDates() {
		return dates;
	}

	public void setDates(Dates dates) {
		this.dates = dates;
	}

	public Boolean getMedicalBoardApproved() {
		return medicalBoardApproved;
	}

	public void setMedicalBoardApproved(Boolean medicalBoardApproved) {
		this.medicalBoardApproved = medicalBoardApproved;
	}

	public String getEditingAuthorKey() {
		return editingAuthorKey;
	}

	public void setEditingAuthorKey(String editingAuthorKey) {
		this.editingAuthorKey = editingAuthorKey;
	}

	public Boolean getNoIndex() {
		return noIndex;
	}

	public void setNoIndex(Boolean noIndex) {
		this.noIndex = noIndex;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSocialTitle() {
		return socialTitle;
	}

	public void setSocialTitle(String socialTitle) {
		this.socialTitle = socialTitle;
	}

	public SliceableListEx<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(SliceableListEx<String> keywords) {
		this.keywords = SliceableListEx.emptyIfNull(keywords);
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPromoDescription() {
		return promoDescription;
	}

	public void setPromoDescription(String promoDescription) {
		this.promoDescription = promoDescription;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	@JsonIgnore
	public ImageEx getImageForUsage(UsageFlag usageFlag) {
		return getImageForUsage(Objects.toString(usageFlag, "").toUpperCase());
	}

	@JsonIgnore
	public ImageEx getImageForUsage(String usageFlag) {
		String cleanedUpUsageFlag = Objects.toString(usageFlag, "").toUpperCase();
		ImageEx answer = null;
		if (taggedImageMap != null) {
			answer = this.taggedImageMap.get(cleanedUpUsageFlag);
		}

		return ImageEx.emptyIfNull(answer);
	}

	public SliceableListEx<TaggedImage> getTaggedImages() {
		return this.taggedImages;
	}

	public void setTaggedImages(SliceableListEx<TaggedImage> taggedImages) {
		this.taggedImages = taggedImages;
		if (taggedImages != null && !taggedImages.isEmpty()) {
			this.taggedImageMap = new HashMap<>();
			for (TaggedImage taggedImage : this.taggedImages) {
				for (String usageFlag : taggedImage.getTags()) {
					this.taggedImageMap.putIfAbsent(usageFlag, taggedImage.getImage());
				}
			}
		} else {
		    this.taggedImageMap = null;
		}
	}

	public AuthorEx getAuthor() {
		return author;
	}

	public void setAuthor(AuthorEx author) {
		this.author = author;
	}

	public Stamp getStamp() {
		return stamp;
	}

	public void setStamp(Stamp stamp) {
		this.stamp = stamp;
	}


	/**
	 *	Changing bestTitle to be document heading, or if empty then document title, then URL title.
	 *	https://dotdash.atlassian.net/browse/AXIS-3966
	 */
	@JsonIgnore
	public String getBestTitle() {
		if (isNotEmpty(getHeading())) return getHeading();
		if (isNotEmpty(getTitle())) return getTitle();

		return stripToNull(getUrlTitle());
	}

	/**
	 * Many subclasses have derived subheadings, enough to add it to top level BaseDocument
	 * @return
	 */
	public String getSubheading() {
		return subheading;
	}

	public void setSubheading(String subheading) {
		this.subheading = subheading;
	}

	public SliceableListEx<Long> getPrimaryAndSecondaryAncestors() {
		return primaryAndSecondaryAncestors;
	}

	public void setPrimaryAndSecondaryAncestors(SliceableListEx<Long> primaryAndSecondaryAncestors) {
		this.primaryAndSecondaryAncestors = primaryAndSecondaryAncestors;
	}

	@JsonIgnore
	public Integer getContentLength() throws GlobeInvalidTaskParameterException {
		return getRemainingLength(0, 0);
	}

	public SummaryEx getSummary() {
		return summary;
	}

	public void setSummary(SummaryEx summary) {
		this.summary = SummaryEx.emptyIfNull(summary);
	}

	public MetaDataEx getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaDataEx metaData) {
		this.metaData = MetaDataEx.emptyIfNull(metaData);
	}

	public Long getDocumentId() {
		return documentId;
	}

	@JsonProperty("docId")
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getActorGid() {
		return actorGid;
	}

	public void setActorGid(String actorGid) {
		this.actorGid = actorGid;
	}

	public PrimaryVideoRefDocumentEx getPrimaryVideo() {
		return primaryVideo;
	}

	public void setPrimaryVideo(PrimaryVideoRefDocumentEx primaryVideo) {
		this.primaryVideo = primaryVideo;
	}

	/**
	 * @deprecated Use {@link #getSponsors} instead
	 */
	@Deprecated
	public Sponsor getSponsor() {
		return sponsor;
	}

	/**
	 * @deprecated Use {@link #setSponsors} instead
	 */
	@Deprecated
	public void setSponsor(Sponsor sponsor) {
		this.sponsor = sponsor;
	}

	public SliceableListEx<Sponsor> getSponsors() {
		return sponsors;
	}

	public void setSponsors(SliceableListEx<Sponsor> sponsors) {
		this.sponsors = sponsors;
	}

	public SliceableListEx<String> getSources() {
		return sources;
	}

	public void setSources(SliceableListEx<String> sources) {
		this.sources = SliceableListEx.emptyIfNull(sources);
	}

	public SliceableListEx<Citation> getCitations() {
		return citations;
	}

	public void setCitations(SliceableListEx<Citation> citations) {
		this.citations = SliceableListEx.emptyIfNull(citations);
	}

	public SliceableListEx<DocumentAttribution> getBylines() {
		return bylines;
	}

	public void setBylines(SliceableListEx<DocumentAttribution> bylines) {
		this.bylines = bylines;
	}

	public SliceableListEx<DocumentAttribution> getTaglines() {
		return taglines;
	}

	public void setTaglines(SliceableListEx<DocumentAttribution> taglines) {
		this.taglines = taglines;
	}

	public Boolean getHasTableOfContents() {
		return this.hasTableOfContents;
	}

	public void setHasTableOfContents(Boolean hasTableOfContents) {
		this.hasTableOfContents = hasTableOfContents;
	}

	public Boolean getHasSummaryList() {
		return hasSummaryList;
	}

	public void setHasSummaryList(Boolean hasSummaryList) {
		this.hasSummaryList = hasSummaryList;
	}

	public Vertical getVertical() {
		return vertical;
	}

	public void setVertical(Vertical vertical) {
		this.vertical = vertical;
	}

	public Disclaimer getDisclaimer() {
		return disclaimer;
	}

	public void setDisclaimer(Disclaimer disclaimer) {
		this.disclaimer = disclaimer;
	}

	public String getMethodology() {
		return methodology;
	}

	public void setMethodology(String methodology) {
		this.methodology = methodology;
	}

	@Override
	public String toString() {
		return "BaseDocumentEx{" + "templateType=" + templateType + ", documentId=" + documentId + ", bestTitle='"
				+ getBestTitle() + '\'' + '}';
	}

	public boolean hasCommerceInfo() {
		return false;
	}

	public SliceableListEx<String> getTickers() {
		return tickers;
	}

	public void setTickers(SliceableListEx<String> tickers) {
		this.tickers = tickers;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public GuestAuthorEx getGuestAuthor() {
		return guestAuthor;
	}

	public void setGuestAuthor(GuestAuthorEx guestAuthor) {
		this.guestAuthor = guestAuthor;
	}

	public String getShortHeading() {
		return shortHeading;
	}

	public void setShortHeading(String shortHeading) {
		this.shortHeading = shortHeading;
	}

	public List<InfoCatRecordPair> getInfoCatProductRecords() {
		return infoCatProductRecords;
	}

	public void setInfoCatProductRecords(List<InfoCatRecordPair> infoCatProductRecords) {
		this.infoCatProductRecords = infoCatProductRecords;
	}

	public String getWebPageSchemaType() {
		return webPageSchemaType;
	}

	public void setWebPageSchemaType(String webPageSchemaType) {
		this.webPageSchemaType = webPageSchemaType;
	}

	public Boolean getIsOpinionContent() {
		return isOpinionContent;
	}

	public void setIsOpinionContent(Boolean isOpinionContent) {
		this.isOpinionContent = isOpinionContent;
	}

	public LegacyMeredithEx getLegacyMeredith() {
		return legacyMeredith;
	}

	public void setLegacyMeredith(LegacyMeredithEx legacyMeredith) {
		this.legacyMeredith = legacyMeredith;
	}

	public SliceableListEx<String> getEntityReferenceTags() {
		return entityReferenceTags;
	}

	public void setEntityReferenceTags(SliceableListEx<String> entityReferenceTags) {
		this.entityReferenceTags = entityReferenceTags;
	}

	public enum Vertical {
		ABOUT,
		HEALTH,
		FITNESS,
		MONEY,
		TECH,
		LIFESTYLE,
		REFERENCE,
		WHITE_LABEL,
		TRAVEL,
		FASHION,
		ESPANOL,
		FAMILY,
		MIND,
		CAREERS,
		BUSINESS,
		MONEYHACKS, // needed for deserialization in spite of money hacks vertical no longer existing
		FOOD,
		PETS,
		CRAFTS,
		BEAUTY,
		DESIGN,
		FINANCE,
		RELIGION,
		WEDDINGS,
		LIQUOR,
		GREEN,
		SIMPLYRECIPES,
		SERIOUSEATS,
		CARBON,
		PEOPLE,
		ENTERTAINMENTWEEKLY,
		PEOPLEENESPANOL,
		ALLRECIPES,
		MYRECIPES,
		EATINGWELL,
		FOODANDWINE,
		BHG,
		MARTHASTEWART,
		REALSIMPLE,
		SOUTHERNLIVING,
		INSTYLE,
		HELLOGIGGLES,
		MYWEDDING,
		MORE,
		SHAPE,
		HEALTHCOM,
		PARENTS,
		PARENTING,
		SERPADRES,
		TRAVELANDLEISURE,
		RACHAELRAYMAG,
		SIEMPREMUJER,
		AGRICULTURE,
		WOODMAGAZINE,
		ALLPEOPLEQUILT,
		COOKINGLIGHT,
		MIDWESTLIVING,
		DAILYPAWS;

		@JsonIgnore
		public boolean isCarbon() {
			return CARBON_VERTICALS.contains(this);
		}
	}

	public enum State {
		DRAFT,
		PREVIEW,
		PENDING,
		ACTIVE;
	}

	public static class DocumentState implements Serializable {
		private static final long serialVersionUID = 1L;

		private State state;
		private DateTime activeDate;

		public State getState() {
			return state;
		}

		public void setState(State state) {
			this.state = state;
		}

		public DateTime getActiveDate() {
			return activeDate;
		}

		public void setActiveDate(DateTime activeDate) {
			this.activeDate = activeDate;
		}
	}

	public static class Dates implements Serializable {
		private static final long serialVersionUID = 1L;

		private DateTime created;
		private DateTime updated; // last update by expert
		private DateTime firstPublished; // first published by the CMS
		private DateTime lastPublished; // last published by CMS. This may vary greatly from updated because the CMS
										// re-publishes for a variety of reasons. I.E. social title
		private DateTime scheduledStart;
		private DateTime firstPending; // first pending by CMS
		private DateTime displayed; // last significant modification date

		public DateTime getCreated() {
			return created;
		}

		public void setCreated(DateTime created) {
			this.created = created;
		}

		public DateTime getUpdated() {
			return updated;
		}

		public void setUpdated(DateTime updated) {
			this.updated = updated;
		}

		public DateTime getFirstPublished() {
			return firstPublished;
		}

		public void setFirstPublished(DateTime firstPublished) {
			this.firstPublished = firstPublished;
		}

		public DateTime getLastPublished() {
			return lastPublished;
		}

		public void setLastPublished(DateTime lastPublished) {
			this.lastPublished = lastPublished;
		}

		public DateTime getScheduledStart() {
			return scheduledStart;
		}

		public void setScheduledStart(DateTime scheduledStart) {
			this.scheduledStart = scheduledStart;
		}

		public DateTime getFirstPending() {
			return firstPending;
		}

		public void setFirstPending(DateTime firstPending) {
			this.firstPending = firstPending;
		}

		public DateTime getDisplayed() {
			return (displayed != null) ? displayed : ((lastPublished != null) ? lastPublished: updated);
		}

		public void setDisplayed(DateTime displayed) {
			this.displayed = displayed;
		}
	}

	public static class Stamp implements Serializable {
		private static final long serialVersionUID = 1L;

		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private String url;
		private Long   docId;
		private String stampValue;
		private String adValue;
		private SliceableListEx<String> tags;

		public Stamp(){}

		public Stamp(String url, Long docId, String stampValue, SliceableListEx<String> tags){
			this.url = url;
			this.docId = docId;
			this.stampValue = stampValue;
			this.adValue = "pid"+stampValue;
			this.tags = tags;
		}

		public Stamp(String url, String stampValue){
			this(url, null, stampValue, SliceableListEx.emptyList());
		}

		public Stamp(Long docId, String stampValue){
			this(null, docId, stampValue, SliceableListEx.emptyList());
		}

		public String getStampValue() {
			return stampValue;
		}

		public void setStampValue(String stampValue) {
			this.stampValue = stampValue;
			this.adValue = "pid"+stampValue;
		}

		public String getAdValue() {
			return adValue;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Long getDocId() {
			return docId;
		}

		public void setDocId(Long docId) {
			this.docId = docId;
		}

		public SliceableListEx<String> getTags() {
			return tags;
		}

		public void setTags(SliceableListEx<String> tags) {
			this.tags = tags;
		}
	}

	public static class Citation implements Serializable {
		private static final long serialVersionUID = 1L;

		private String source;
		private Integer refId;

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public int getRefId() {
			return refId;
		}

		public void setRefId(Integer refId) {
			this.refId = refId;
		}
	}

	public static class Disclaimer implements Serializable {

		private static final long serialVersionUID = 1L;

		private Boolean affiliate;
		private String editorial;

		public Boolean getAffiliate() {
			return affiliate;
		}

		public void setAffiliate(Boolean affiliate) {
			this.affiliate = affiliate;
		}

		public String getEditorial() {
			return editorial;
		}

		public void setEditorial(String editorial) {
			this.editorial = editorial;
		}
	}

	public static class LegacyMeredithEx implements Serializable {

		private static final long serialVersionUID = 1L;

		private String cmsId;

		public String getCmsId() {
			return cmsId;
		}

		public void setCmsId(String cmsId) {
			this.cmsId = cmsId;
		}
	}
}
