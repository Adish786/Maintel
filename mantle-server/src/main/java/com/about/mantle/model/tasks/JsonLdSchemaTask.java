package com.about.mantle.model.tasks;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.attribution.AttributionModel;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.DocumentAttribution;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.JwPlayerVideoDocumentEx;
import com.about.mantle.model.extended.docv2.MetaDataEx;
import com.about.mantle.model.extended.docv2.PrimaryVideoRefDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.StepByStepDocumentEx;
import com.about.mantle.model.extended.docv2.TaggedImage;
import com.about.mantle.model.extended.docv2.brightcovevideo.BrightcoveVideoDocumentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.HowToStructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.ListStructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.TermStructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentFaqEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentGalleryEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentGallerySlideEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentQuestionAndAnswerEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentItemEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentDefinitionEx.StructuredContentDefinitionDataEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHeadingEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHtmlEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentImageEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentSubheadingEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentVideoBlockEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentVideoBlockEx.StructuredContentVideoBlockDataEx;
import com.about.mantle.model.extended.docv2.sc.recipesc.RecipeStructuredContentDocumentEx;
import com.about.mantle.model.seo.jsonld.HowToSection;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tasks
public class JsonLdSchemaTask {

	protected static final Logger logger = LoggerFactory.getLogger(JsonLdSchemaTask.class);

	private static final List<String> REVIEWER_ATTRIBUTION_TYPES = Arrays.asList("FACT_CHECKER", "REVIEWER", "BEAUTY_WELLNESS_REVIEWER", "FINANCE_REVIEWER", "GARDENING_REVIEWER", "MEDICAL_REVIEWER",
			"RECIPE_TESTER", "RENOVATIONS_REPAIR_REVIEWER", "TECH_REVIEWER", "VET_REVIEWER", "WELLNESS_REVIEWER",
			"COMPLIANCE_REVIEWER", "CLEANING_REVIEWER", "NUTRITION_REVIEWER");
	private static final List<String> NO_INLINE_IMAGE_REVENUE_GROUPS = Arrays.asList("COMMERCE",
			"FINANCIALSERVICES", "PERFORMANCEMARKETING");

	private static final List<String> MEDICAL_REVIEWER_ATTRIBUTION_TYPES = Arrays.asList("MEDICAL_REVIEWER", "VET_REVIEWER");
	private static final List<String> EDITOR_ATTRIBUTION_TYPES = Arrays.asList("EDITOR");
	private static final List<String> CONTRIBUTOR_ATTRIBUTION_TYPES = Arrays.asList("ORIGINAL_WRITER", "RESEARCH_ANALYSIS", "ADDITIONAL_RESEARCHER", "ADDITIONAL_REPORTER", "UPDATER", "TESTER");
	private static final List<String> AUTHOR_ATTRIBUTION_TYPES = Arrays.asList("AUTHOR", "UGC_RECIPE");

	private final DocumentTask documentTask;
	private final AttributionTask attributionTask;
	private final boolean isAttributionEnabled;

	public JsonLdSchemaTask(DocumentTask documentTask, AttributionTask attributionTask, boolean isAttributionEnabled) {
		this(documentTask, attributionTask, isAttributionEnabled, null);
	}

	@Deprecated
	public JsonLdSchemaTask(DocumentTask documentTask, AttributionTask attributionTask, boolean isAttributionEnabled, String defaultPlaceholderImage) {
		this.documentTask = documentTask;
		this.attributionTask = attributionTask;
		this.isAttributionEnabled = isAttributionEnabled;
	}

	@Task(name = "jsonLdSchemaComponent")
	public String jsonLdSchemaComponent(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "document") BaseDocumentEx document) {
		switch (document.getTemplateType()) {
		
		case CATEGORY:
		case IMAGEGALLERY:
		case JWPLAYERVIDEO:
		case LANDINGPAGE:
		case LEGACY:
		case PROGRAMMEDSUMMARY:
		case QUIZ:
		case RECIPE:
		case REDIRECT:
		case STEPBYSTEP:
		case TOPIC:
		case TOPICMAP:
		case USERPATH:
			// known legacy templates that are definitely not supported yet
			logger.error("mainEntity mapping is not supported for templateType [{}]", document.getTemplateType());
			return null;
		case REVIEWSC:
			return "review";
		case RECIPESC:
			return "recipe";
		case BIO:
			return "bio";
		case HOWTO:
			return "article";
		case TAXONOMY:
			return "taxonomy";
		case TAXONOMYSC:
			return "taxonomysc";
		default:
			return getTemplateNameUsingViewType(document, "article");
		}
	}
	
	@Task(name = "jsonLdSchemaVideos")
	public List<StructuredContentVideoBlockEx> jsonLdSchemaVideos(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "document") BaseDocumentEx document) {
		List<StructuredContentVideoBlockEx> videos = new ArrayList<>();
		if (document instanceof StructuredContentBaseDocumentEx) {
			StructuredContentBaseDocumentEx scBaseDoc = (StructuredContentBaseDocumentEx) document;
			videos.addAll(scBaseDoc.getContentsStreamOfType("INLINEVIDEO")
			                       .map(block -> (StructuredContentVideoBlockEx) block).collect(Collectors.toList()));
		}
		if (document.getVertical().isCarbon() && document.getPrimaryVideo() != null &&
				document.getPrimaryVideo().getDocument() != null) {
			// primary video should contribute to schema for carbon verticals.
			// legacy Dotdash verticals should exclude primary video from schema as it is often used for ad purposes
			// Converting to video block to simplify schema logic
			PrimaryVideoRefDocumentEx primaryVideo = document.getPrimaryVideo();
			StructuredContentVideoBlockEx primaryVideoBlock = new StructuredContentVideoBlockEx();
			StructuredContentVideoBlockDataEx primaryVideoData = new StructuredContentVideoBlockDataEx();
			primaryVideoData.setDocId(primaryVideo.getDocId());
			primaryVideoData.setDocument(primaryVideo.getDocument());
			primaryVideoBlock.setData(primaryVideoData);
			videos.add(primaryVideoBlock);
		}
		return videos;
	}

	@Task(name = "authorAttributions")
	public List<AttributionModel> getAuthorAttribution(@TaskParameter(name = "document") BaseDocumentEx document) {
		return getValidSchemaAttributions(document, AUTHOR_ATTRIBUTION_TYPES);
	}

	@Task(name = "contributorAttributions")
	public List<AttributionModel> getContributorAttribution(@TaskParameter(name = "document") BaseDocumentEx document) {
		return getValidSchemaAttributions(document, CONTRIBUTOR_ATTRIBUTION_TYPES);
	}

	@Task(name = "editorAttributions")
	public List<AttributionModel> getEditorAttributions(@TaskParameter(name = "document") BaseDocumentEx document) {
		return getValidSchemaAttributions(document, EDITOR_ATTRIBUTION_TYPES);
	}

	@Task(name = "reviewerAttributions")
	public List<AttributionModel> getReviewerAttributions(@TaskParameter(name = "document") BaseDocumentEx document) {
		return getValidSchemaAttributions(document, REVIEWER_ATTRIBUTION_TYPES);
	}

	private DateTime getLastReviewedDate(BaseDocumentEx document, String timeZone) {
		DateTime answer = null;
		List<AttributionModel> reviewerAttributions = getValidSchemaAttributions(document, REVIEWER_ATTRIBUTION_TYPES);
		Optional<DocumentAttribution> latestReviewedDateAttribution = Optional.empty();
		if(reviewerAttributions != null) {
			latestReviewedDateAttribution = reviewerAttributions.stream()
					.map(attributionModel -> {
						if (attributionModel != null && attributionModel.getAttribution() instanceof DocumentAttribution) {
							return (DocumentAttribution)attributionModel.getAttribution();
						}
						return null;
					})
					.filter(da -> da != null && da.getLastModified() != null)
					.sorted((da, db) -> db.getLastModified() // sort in descending order
							.compareTo(da.getLastModified()))
					.findFirst();
		}

		if(latestReviewedDateAttribution.isPresent()) {
			if(latestReviewedDateAttribution.get().getLastModified().isAfter(DateTime.now().minusYears(3))){
				answer = latestReviewedDateAttribution.get().getLastModified().withZone(DateTimeZone.forID(timeZone));
			}
		}
		return answer;
	}

	@Task(name = "newestDateBasedOnReview")
	public String getNewestDateBasedOnReview(@TaskParameter(name = "document") BaseDocumentEx document,
									  @TaskParameter(name = "timeZone") String timeZone,
									  @TaskParameter(name = "isForLastReviewedDateField") Boolean isForLastReviewedDateField) {
		DateTime updatedDate = document.getDates().getDisplayed();
		DateTime lastReviewedDate = getLastReviewedDate(document, timeZone);

		// lastReviewDate will be null if there are no reviewers.
		if (lastReviewedDate == null && BooleanUtils.isTrue(isForLastReviewedDateField)) {
			return "";
		}
		DateTime latestDate =  (lastReviewedDate != null && lastReviewedDate.isAfter(updatedDate)) ? lastReviewedDate : updatedDate;
		return latestDate.withZone(DateTimeZone.forID(timeZone)).toString();
	}

	private List<AttributionModel> getValidSchemaAttributions(BaseDocumentEx document, List<String> types) {
		List<AttributionModel> answer = attributionTask.fetchAttributionsByDocument(document, types);
		if(answer != null) {
			answer = answer.stream().filter(attributionModel -> {
				return attributionModel == null || attributionModel.getAuthor() == null ? false :
						!StringUtils.equalsAnyIgnoreCase(attributionModel.getAuthor().getDisplayName(), "Staff Author", "", null);
			}).collect(Collectors.toList());
		}
		return answer;
	}

	/**
	 * Returns the viewType if applicable otherwise return the default schema entity type.
	 */
	private String getTemplateNameUsingViewType(BaseDocumentEx document, String defaultTemplate) {
        String viewType = document.getViewType();

        if (viewType == null) {
            return defaultTemplate;
        }

        switch (viewType) {
            case "CREDITCARDROUNDUP":
            	return "creditcardroundup";
			case "CORPORATE": /* SC View Type */
				return "about";
			default:
				return defaultTemplate;
        }
    }

	/**
	 * Returns the primary or summary image of the document taking into account whether Proteus is supported.
	 * If it doesn't exist and it is an SC document, then it returns the first inline image. See PCR-111
	 * 
	 * Rules of building the schema: 
	 *  If document has a primary image, use it for schema
	 * 	If document has no primary image, use the first inline image or list image for schema
	 *	For commerce lists pulling in Amazon images, at least one image will be stored in the list in Selene – use this one for schema
	 *	If document has no images, then no image schema should be applied
	 */
	@Task(name ="jsonLdSchemaImage")
	public ImageEx jsonLdSchemaImage(@TaskParameter(name = "document") BaseDocumentEx document) {
		return document instanceof StructuredContentBaseDocumentEx ?
				jsonLdSchemaImageStructuredContentDocument((StructuredContentBaseDocumentEx) document)
				: jsonLdSchemaImageLegacyDocument(document);
	}

	/**
	 * Determines the type of main entity component to be included in the schema
	 * @param requestContext
	 * @param document
	 * @return
	 */
	@Task(name = "jsonLdSchemaMainEntityOfPage")
	public String jsonLdSchemaMainEntityOfPage(@RequestContextTaskParameter RequestContext requestContext,
										 @TaskParameter(name = "document") BaseDocumentEx document) {
		if (document == null) {
			logger.error("unable to determine type of main entity of page because document is null");
			return "web-page";
		}

		String viewType = document.getViewType();
		if(viewType != null && (viewType.equals("ALPHA-INDEX") || viewType.equals("ALPHA-PAGE"))){
			return "medical-web-page";
		}

		TemplateTypeEx templateType = document.getTemplateType();
		if (TemplateTypeEx.TAXONOMY.equals(templateType) || TemplateTypeEx.TAXONOMYSC.equals(templateType)) {
			return "taxonomy-collection-page";
		}

		if (isAttributionEnabled && isMedicalReviewerAttributionPresent(document)){
			return "medical-web-page";
		}
		MetaDataEx metaData = document.getMetaData();
		if (metaData != null) {
			MetaDataEx.Review review = metaData.getReview();
			if (review != null && !isAttributionEnabled) {

				if ("|medical_approved|".equals(review.getType()) || "|vet_approved|".equals(review.getType())) {
					return "medical-web-page";
				}
			}
		}
		//Note the MedicalWebPage check is a temp solution
		//see https://dotdash.atlassian.net/browse/PCR-215?focusedCommentId=700222&oldIssueView=true&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-700222
		if (containsMedicalWebPageSchema(document.getWebPageSchemaType())){
			return "medical-web-page";
		}

		return "web-page";
	}

	private boolean isMedicalReviewerAttributionPresent(BaseDocumentEx document) {
		boolean answer = false;
		List<AttributionModel> medicalReviewerAttributions = getValidSchemaAttributions(document, MEDICAL_REVIEWER_ATTRIBUTION_TYPES);
		if(medicalReviewerAttributions != null && !medicalReviewerAttributions.isEmpty()) {
			answer = true;
		}
		return answer;
	}

	protected boolean containsMedicalWebPageSchema(String webPageSchemaType){
		return "MedicalWebPage".equals(webPageSchemaType);
	}

	protected ImageEx jsonLdSchemaImageStructuredContentDocument(StructuredContentBaseDocumentEx document){
		ImageEx image = null;

		// Final Project Image for HOWTO Documents
		if (document instanceof HowToStructuredContentDocumentEx) {
			image = document.getImageForUsage(TaggedImage.UsageFlag.FINALPROJECT);
			if (isNonEmptyImage(image)) {
				return image;
			}
		}

		// Primary Image
		image = document.getImageForUsage(TaggedImage.UsageFlag.PRIMARY);
		if (isNonEmptyImage(image)) {
			return image;
		}

		// Inline image
		boolean disallowInlineImage = NO_INLINE_IMAGE_REVENUE_GROUPS.contains(document.getRevenueGroup())
			&& !"STRUCTUREDCONTENT".equals(document.getTemplateType());
		if (!disallowInlineImage) {
			// Inline image (content block)
			Optional<AbstractStructuredContentContentEx<?>> inlineImage = document.getContentsStreamOfType("IMAGE").findFirst();
			if (inlineImage.isPresent()) {
				StructuredContentImageEx inlineImageEx = (StructuredContentImageEx) inlineImage.get();
				image = inlineImageEx.getData().getImage();
				if (isNonEmptyImage(image)) {
					return image;
				}
			}

			if (TemplateTypeEx.REVIEWSC.equals(document.getTemplateType())) {
				//Use image from PRODUCTRECORD if there is no image block
				Optional<AbstractStructuredContentContentEx<?>> inlineProduct = document.getContentsStreamOfType("PRODUCTRECORD")
						.filter(inlineProductEx -> ((StructuredContentProductRecordEx) inlineProductEx).getData().getProduct().getImageForUsage("PRIMARY") != null)
						.findFirst();
				if (inlineProduct.isPresent()) {
					StructuredContentProductRecordEx inlineProductEx = (StructuredContentProductRecordEx) inlineProduct.get();
					image = inlineProductEx.getData().getProduct().getImageForUsage("PRIMARY");
					if (isNonEmptyImage(image)) {
						return image;
					}
				}
			}
		}

		// Resolve video document to pull thumbnail from
		BaseDocumentEx videoDoc = null;
		if (document.getVertical().isCarbon()) {
			// Primary video thumbnail - for carbon verticals
			if (document.getPrimaryVideo() != null) {
				videoDoc = document.getPrimaryVideo().getDocument();
			}
		} else {
			// Inline image thumbnail - for non-carbon verticals
			Optional<AbstractStructuredContentContentEx<?>> inlineVideo = document.getContentsStreamOfType("INLINEVIDEO").findFirst();
			if (inlineVideo.isPresent()) {
				StructuredContentVideoBlockEx inlineVideoEx = (StructuredContentVideoBlockEx) inlineVideo.get();
				videoDoc = inlineVideoEx.getData().getDocument();
			}
		}

		// Map video document to video thumbnail
		if (videoDoc != null) {
			String thumbnailUrl = null;
			if (videoDoc instanceof JwPlayerVideoDocumentEx) {
				thumbnailUrl = ((JwPlayerVideoDocumentEx) videoDoc).getThumbnailUrl();
			} else if (videoDoc instanceof BrightcoveVideoDocumentEx) {
				thumbnailUrl = ((BrightcoveVideoDocumentEx) videoDoc).getThumbnailUrl();
			}
			if (thumbnailUrl != null) {
				image = new ImageEx();
				image.setUrl(thumbnailUrl);
				return image;
			}
		}

		return image;
	}

	protected static boolean isNonEmptyImage(ImageEx image) {
		return image != null && !image.isEmpty();
	}
	
	/**
	 *Handles legacy document logic for building image schema. Currently the only known special case is Step by Step 
	 */
	protected ImageEx jsonLdSchemaImageLegacyDocument(BaseDocumentEx document){
		ImageEx image = document.getImageForUsage(TaggedImage.UsageFlag.PRIMARY);

		// Use primary image if present
		if(isNonEmptyImage(image)) {
			return image;
		}
		
		switch (document.getTemplateType()) {
		case STEPBYSTEP:
			//If we have images on a step by step use the first one in the list
			StepByStepDocumentEx sbsDocument = ((StepByStepDocumentEx)document);
			image = sbsDocument.getImages() !=null && sbsDocument.getImages().size() > 0 ? sbsDocument.getImages().get(0) : null;
			break;
		default:
			//So far only Step By Step has shown to have issues 
			image = null;
		}
		return image;
	}

	@Task(name = "jsonLdSchemaHowToSections")
	public List<HowToSection> jsonLdSchemaHowToSections(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "document") BaseDocumentEx document) {
		if (document == null) {
			logger.error("failed to get howto sections because document is null");
			return null;
		}
		switch (document.getTemplateType()) {
		case RECIPESC:
			return getHowToSectionsFromInstructions(((RecipeStructuredContentDocumentEx) document).getInstructionContents(), false);
		case HOWTO:
			return getHowToSectionsFromInstructions(((HowToStructuredContentDocumentEx) document).getInstructionContents(), true);
		default:
			return null;
		}
	}

	@Task(name = "definitions")
	public List<SchemaDefinedTerm> getDefinitions(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "document") BaseDocumentEx document) {
		List<SchemaDefinedTerm> answer = new ArrayList<>();

		if (document instanceof StructuredContentBaseDocumentEx) {
			answer.addAll(((StructuredContentBaseDocumentEx) document).getContentsStreamOfType("definition")
					.filter(block -> block != null)
					.map(block -> (StructuredContentDefinitionDataEx) block.getData())
					.filter(data -> StringUtils.isNotEmpty(data.getContent())
							&& StringUtils.isNotEmpty(data.getHeading()))
					.map(data -> new SchemaDefinedTerm(data.getHeading(), data.getContent()))
					.collect(Collectors.toCollection(ArrayList::new)));
		}
		if (document instanceof TermStructuredContentDocumentEx &&
				StringUtils.isNotEmpty(((TermStructuredContentDocumentEx) document).getShortDefinition())) {
			SchemaDefinedTerm schemaDefinedTerm = new SchemaDefinedTerm(document.getTitle(),
					((TermStructuredContentDocumentEx) document).getShortDefinition());
			answer.add(schemaDefinedTerm);
		}
		return answer;
	}


	protected List<HowToSection> getHowToSectionsFromInstructions(SliceableListEx<AbstractStructuredContentContentEx<?>> instructions, boolean treatSubheadingsAsStepName) {
		// TODO: we hope one day that RECIPESC will have a data point specifically for "short" instructions
		//       see https://iacpublishing.atlassian.net/browse/GLBE-6123
		// in the meantime we will map to each group of paragraphs in the content well reserved for instructions
		// delineated by the "stepType" property and grouped by HEADING blocks

		List<HowToSection> answer = instructions.stream()
				.collect(() -> new ArrayList<HowToSection.Builder>(), (builders, block) -> {
					
					
					if (block instanceof StructuredContentHeadingEx) { //Creating and naming How To Sections 
						StructuredContentHeadingEx headingBlock = (StructuredContentHeadingEx) block;
						builders.add(HowToSection.Builder.fromName(headingBlock.getData().getText()));
						
					} else { //handles building of steps for a how to section
						
						// NOTE: we are currently only considered numbered steps as the start of a new step
						if ("NUMBERED".equals(block.getData().getStepType())) {
							
							if (builders.size() == 0) { //Initializes the How To Section while creating the new step
								String step = getStep(block);
								if(step != null){
									//Check to see if the schema treats h4s as a name or text, and if it does see if an h4 is present
									if(isStepName(block, treatSubheadingsAsStepName)){
										builders.add(HowToSection.Builder.fromStepName(step));
									}else{
										builders.add(HowToSection.Builder.fromStep(step));
									}
									
								}else if(block instanceof StructuredContentImageEx){
									builders.add(HowToSection.Builder.fromImages(Collections.singletonList(((StructuredContentImageEx) block).getData().getImage())));
								}else if(block instanceof StructuredContentGalleryEx) {
									builders.add(HowToSection.Builder.fromImages(getImagesFromGalleryBlock((StructuredContentGalleryEx)block)));
								}else{
									builders.add(HowToSection.Builder.fromEmptyStep()); //Add new empty builder, we don't have any block information yet
								}
								
							} else { //Adds the How To Step to the already created How To Section 
								String step = getStep(block);
								if(step != null){
									if(isStepName(block, treatSubheadingsAsStepName)){
										builders.get(builders.size() - 1).newStepFromName(step);
									}else{
										builders.get(builders.size() - 1).newStep(step);
									}
									
								}else if(block instanceof StructuredContentImageEx){
									builders.get(builders.size() - 1).newStepfromImages(Collections.singletonList(((StructuredContentImageEx) block).getData().getImage()));
								}else if(block instanceof StructuredContentGalleryEx){
									builders.get(builders.size() - 1).newStepfromImages(getImagesFromGalleryBlock((StructuredContentGalleryEx)block));
								}else{
									builders.get(builders.size() - 1).newEmptyStep(); //No info to add to the construction of the step, but we need to know we're in it
								}
								
							}
							
						} else if (isBlank(block.getData().getStepType()) && !builders.isEmpty() &&
								builders.get(builders.size() - 1).hasSteps()) { //Case for where we've entered a step, which is derived by having at least one builder, 
																				//the block in question has no step type, and the current builder already has a step block added to it.  
							String step = getStep(block);
							if(step != null){
								if(isStepName(block, treatSubheadingsAsStepName)){
									builders.get(builders.size() - 1).appendStepName(step);
								}else{
									builders.get(builders.size() - 1).appendStep(step);
								}
								
							}else if(block instanceof StructuredContentImageEx){
								builders.get(builders.size() - 1).appendImagesToStep(Collections.singletonList(((StructuredContentImageEx) block).getData().getImage()));
							}else if(block instanceof StructuredContentGalleryEx) {
								builders.get(builders.size() - 1).appendImagesToStep(getImagesFromGalleryBlock((StructuredContentGalleryEx)block));
							}//If it isn't an HTML block, GALLERY block or Image block, do nothing.
						} 
					}
					
				}, ArrayList::addAll)
				.stream()
				.map(builder -> builder.build())
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		return answer;
	}

	private List<ImageEx> getImagesFromGalleryBlock(StructuredContentGalleryEx block) {
		List<ImageEx> images = new ArrayList<>();

		for(AbstractStructuredContentContentEx gallerySlideContent : block.getNestingContents()) {
			if(gallerySlideContent instanceof StructuredContentGallerySlideEx) {
				for(AbstractStructuredContentContentEx imageContent : ((StructuredContentGallerySlideEx) gallerySlideContent).getNestingContents()) {
					if(imageContent instanceof StructuredContentImageEx) {
						images.add(((StructuredContentImageEx)imageContent).getData().getImage());
					}
				}
			}
		}

		return images;
	}

	@Task(name = "sanitizedNewsType")
	public String sanitizeNewsType(@TaskParameter(name = "document") BaseDocumentEx document) {
		String answer = null;
		if(StringUtils.isNotBlank(document.getNewsType())) {
			if("NEWS".equalsIgnoreCase(document.getNewsType())) {
				answer = "NewsArticle";
 			} else {
				answer = document.getNewsType().trim();
			}
		}
		return answer;
	}

	/**
	 * Wraps items from list sc documents to expose additional calculated fields for rendering
	 * by schema components.
	 * @param document
	 * @param items
	 * @return
	 */
	@Task(name = "schemaListItems")
	public List<SchemaListItem> getSchemaListItems(
			@TaskParameter(name = "document") BaseDocumentEx document,
			@TaskParameter(name = "items") SliceableListEx<StructuredContentItemEx> items) {

		List<SchemaListItem> listItems = new ArrayList<>();
		String docUrl = document.getUrl();

		if (items == null) return null;

		for (StructuredContentItemEx item : items) {
			String anchor = null;
			for (AbstractStructuredContentContentEx<?> block : item.getContents().getList()) {
				if (block instanceof StructuredContentHeadingEx) {
					// Grab anchor of first heading
					anchor = TableOfContentsTask.generateAnchorId(
							(StructuredContentHeadingEx) block);
					break;
				} else if (block instanceof StructuredContentProductRecordEx) {
					// Grab anchor of product record
					anchor = TableOfContentsTask.generateAnchorId(
							(StructuredContentProductRecordEx) block);
					break;
				}
			}
			SchemaListItem listItem = new SchemaListItem();
			listItem.setItem(item);
			if (anchor != null) {
				listItem.setUrl(docUrl + "#" + anchor);
			}
			listItems.add(listItem);
		}

		return listItems;
	}


	/**
	 * Gathers all Questions and Answers from the FAQ block and QUESTIONANDANSWER block in order
	 * @param document
	 * @return List of Questions and answers
	 */
	@Task(name = "listOfSchemaQuestionAndAnswers")
	public List<SchemaQuestionAndAnswer> getListOfSchemaQuestionAndAnswers(@TaskParameter(name = "document") BaseDocumentEx document) {
		List<SchemaQuestionAndAnswer> schemaQuestionAndAnswers = new ArrayList<>();
		if (document instanceof StructuredContentBaseDocumentEx) {
			for (AbstractStructuredContentContentEx<?> block : ((StructuredContentBaseDocumentEx) document).getContentsList()) {
				if (block instanceof StructuredContentQuestionAndAnswerEx) {
					StructuredContentQuestionAndAnswerEx questionAndAnswerBlock = (StructuredContentQuestionAndAnswerEx) block;
					schemaQuestionAndAnswers.add(new SchemaQuestionAndAnswer(questionAndAnswerBlock.getData().getQuestion(), questionAndAnswerBlock.getData().getAnswer()));
				} else if (block instanceof StructuredContentFaqEx) {
					StructuredContentFaqEx faqBlock = (StructuredContentFaqEx) block;
					for (StructuredContentFaqEx.Faq faq : faqBlock.getData().getFaqs().getList()) {
						schemaQuestionAndAnswers.add(new SchemaQuestionAndAnswer(faq.getQuestion(), faq.getAnswer()));
					}
				}
			}
		}
		return schemaQuestionAndAnswers;
	}

	@Task(name = "isLiveBlogPostDataAvailable")
	public boolean isLiveBlogPostDataAvailable(@TaskParameter(name = "document") BaseDocumentEx document) {
		if(document instanceof StructuredContentDocumentEx) {
			StructuredContentDocumentEx scDoc = (StructuredContentDocumentEx)document;
			if(scDoc.getLiveBlogPost() != null) {
				return true;
			}
		}else if(document instanceof ListStructuredContentDocumentEx) {
			ListStructuredContentDocumentEx listScDoc = (ListStructuredContentDocumentEx)document;
			if(listScDoc.getLiveBlogPost() != null) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns content for String-based block steps.
	 * @param block
	 * @return
	 */
	private String getStep(AbstractStructuredContentContentEx<?> block) {
		if (block instanceof StructuredContentHtmlEx) {
			return ((StructuredContentHtmlEx) block).getData().getHtml();
		} else if (block instanceof StructuredContentSubheadingEx) {
			return ((StructuredContentSubheadingEx) block).getData().getText();
		}
		return null;
	}

	/**
	 * Determines whether a block's text should be a step name or step body.
	 * @param block
	 * @param treatSubheadingsAsStepName
	 * @return
	 */
	private boolean isStepName(AbstractStructuredContentContentEx<?> block, boolean treatSubheadingsAsStepName) {
		if (block instanceof StructuredContentHtmlEx) {
			return treatSubheadingsAsStepName && getStep(block).toLowerCase().contains("h4");
		} else if (block instanceof StructuredContentSubheadingEx) {
			return treatSubheadingsAsStepName;
		}
		return false;
	}

	public static class SchemaListItem {

		public String url;
		public StructuredContentItemEx item;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public StructuredContentItemEx getItem() {
			return item;
		}

		public void setItem(StructuredContentItemEx item) {
			this.item = item;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			result = prime * result + ((item == null) ? 0 : item.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof SchemaListItem)) {
				return false;
			}
			SchemaListItem other = (SchemaListItem) obj;
			if (url == null) {
				if (other.url != null) {
					return false;
				}
			} else if (!url.equals(other.url)) {
				return false;
			}
			if (item == null) {
				if (other.item != null) {
					return false;
				}
			} else if (!item.equals(other.item)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "SchemaListItem [url=" + url +
					", item=" + item + "]";
		}
	}

	public static class SchemaQuestionAndAnswer {
		private final String question;
		private final String answer;

		public SchemaQuestionAndAnswer(String question, String answer) {
			this.question = question;
			this.answer = answer;
		}

		public String getQuestion() {
			return question;
		}

		public String getAnswer() {
			return answer;
		}
	}

	public static class SchemaDefinedTerm {
		private final String name;
		private final String description;

		public SchemaDefinedTerm(String name, String description) {
			this.name = name;
			this.description = description;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}
	}
}
