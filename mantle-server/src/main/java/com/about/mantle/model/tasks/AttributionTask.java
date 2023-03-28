package com.about.mantle.model.tasks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.attribution.AttributionDate;
import com.about.mantle.model.attribution.AttributionModel;
import com.about.mantle.model.extended.AuthorEx;
import com.about.mantle.model.extended.attribution.Attribution;
import com.about.mantle.model.extended.attribution.AttributionType;
import com.about.mantle.model.extended.attribution.AttributionTypeEnum;
import com.about.mantle.model.extended.BaseAuthor;
import com.about.mantle.model.extended.curatedlist.CuratedListEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BioDocumentEx;
import com.about.mantle.model.extended.docv2.CuratedDocumentTaxeneComposite;
import com.about.mantle.model.extended.docv2.DocumentAttribution;
import com.about.mantle.model.extended.docv2.DocumentTaxeneComposite;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.AttributionService;
import com.about.mantle.model.services.AuthorService;
import com.about.mantle.model.services.DocumentService;
import com.about.mantle.model.services.ugc.UGCUserService;
import com.about.mantle.model.services.ugc.dto.UGCUserDto;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tasks
public class AttributionTask {

	private static final Logger logger = LoggerFactory.getLogger(AttributionTask.class);

	protected final AuthorService authorService;
	protected final AttributionService attributionService;
	protected final DocumentService documentService;
	protected final UGCUserService ugcUserService;
	private final boolean isAttributionEnabled;

	public AttributionTask(AuthorService authorService, AttributionService attributionService,
						   DocumentService documentService, UGCUserService ugcUserService, boolean isAttributionEnabled) {
		this.authorService = authorService;
		this.attributionService = attributionService;
		this.documentService = documentService;
		this.ugcUserService = ugcUserService;
		this.isAttributionEnabled = isAttributionEnabled;
	}

	@Task(name = "ATTRIBUTION")
	@TimedComponent(category = "task")
	public AttributionModel fetchAttributionById(@TaskParameter(name = "id") String id) {
		Attribution attribution = attributionService.getById(id);
		return getAttributionModel(attribution);
	}

	@Task(name = "ATTRIBUTIONS")
	@TimedComponent(category = "task")
	public List<AttributionModel> fetchAttributionsByDocument(@TaskParameter(name = "document") BaseDocumentEx document) {
		return fetchAttributionsByDocument(document, null);
	}

	/**
	 * If templates remove the document parameter, a task containing the types parameter must exist or an error
	 * is thrown since no taskMethod can be found
	 * @param types
	 * @return
	 */
	@Task(name = "ATTRIBUTIONS")
	@TimedComponent(category = "task")
	public List<AttributionModel> fetchAttributionsByDocument(@TaskParameter(name = "types") List<String> types) {
		return null;
	}

	@Task(name = "ATTRIBUTIONS")
	@TimedComponent(category = "task")
	public List<AttributionModel> fetchAttributionsByDocument(@TaskParameter(name = "document") BaseDocumentEx document,
															  @TaskParameter(name = "types") List<String> types) {
		if (document == null) return null;

		return mapAttributions(filterTypes(attributions(document), types))
				.collect(Collectors.toList());
	}

	@Task(name = "GROUPEDATTRIBUTIONS")
	@TimedComponent(category = "task")
	public Map<String, List<AttributionModel>> groupedAttributions(@TaskParameter(name = "attributions") List<AttributionModel> attributions) {
		return attributions.stream()
				// Taglines will never have author or fact-checker so this sorter will not change their order.
			    // The sort should only impact bylines.
				.sorted(Comparator.comparingInt(attribution -> getCompareValueOfAttributionType(attribution.getAttribution())))
				.collect(
				// Need a LinkedHashMap to preserve the insertion order
				Collectors.groupingBy(attributionModel -> attributionModel.getAttribution().getType(), LinkedHashMap::new, Collectors.toList()));
	}

	/**
	 * Goes through the attribution types and returns the group of attributions (if they exist) that match the attribution.
	 * Only the first type that exists will be returned.
	 * @param attributions bylines or taglines of document
	 * @param attributionTypes the prioritized list of attribution types, ex: ${{'MEDICAL_REVIEWER', 'FACT_CHECKER', 'AUTHOR'}}
	 * @return The group of attributions described above
	 */
	@Task(name = "FILTEREDATTRIBUTIONS")
	@TimedComponent(category = "task")
	public List<AttributionModel> filteredAttributions(@TaskParameter(name = "attributions") List<AttributionModel> attributions,
														  @TaskParameter(name = "attributionTypes") List<String> attributionTypes) {
		for (String attributionType : attributionTypes) {
			List<AttributionModel> filteredAttributions = attributions.stream()
					.filter(attribution -> attribution.getAttribution().getType().equals(attributionType))
					.collect(Collectors.toList());
			if (filteredAttributions.size() != 0) {
				return filteredAttributions;
			}
		}
		return null;
	}

	/**
	 * Consolidates the logic of producing a one-line byline text featuring a single attribution type.
	 * Also considers the document's guest author for overriding the attribution when the type is AUTHOR.
	 * @param document
	 * @param attributionTypes the prioritized list of attribution types, ex: ${{'MEDICAL_REVIEWER', 'FACT_CHECKER', 'AUTHOR'}}
	 * @return Byline text string by concatenating author's display names and/or
	 *         "and" and/or comma (as per Oxford comma definition) for multiple attributions of a particular type.
	 *
	 *         <p>
	 *         <b> For e.g. </b></br>
	 *         By Foo </br>
	 *         By Foo and Bar </br>
	 *         By Foo, Bar, and Baz </br>
	 *         </p>
	 */
	@Task(name = "BYLINETEXT")
	@TimedComponent(category = "task")
	public String getBylineText(@TaskParameter(name = "document") BaseDocumentEx document,
	                            @TaskParameter(name = "attributionTypes") List<String> attributionTypes) {
		if (document == null || CollectionUtils.isEmpty(attributionTypes)) return null;

		List<AttributionModel> filteredAttributions = filteredAttributions(fetchBylinesByDocument(document, false), attributionTypes);
		if (filteredAttributions == null) return null;

		String commonDescriptorPerFilteredAttribution = filteredAttributions.get(0).getAttribution().getDescriptor();

		if (AttributionTypeEnum.AUTHOR.name().equals(filteredAttributions.get(0).getAttribution().getType())) {
			String guestAuthorName = document.getGuestAuthor() == null || document.getGuestAuthor().getLink() == null ? null :
			                         StringUtils.stripToNull(document.getGuestAuthor().getLink().getText());
			if (guestAuthorName != null) {
				return commonDescriptorPerFilteredAttribution + " " + guestAuthorName;
			}
		}

		StringBuilder bylineText = new StringBuilder(commonDescriptorPerFilteredAttribution);
		boolean isFilteredAttributionSizeGreaterThanTwo = filteredAttributions.size() > 2;

		for(int filteredAttributionIndex = 0; filteredAttributionIndex < filteredAttributions.size(); filteredAttributionIndex++) {
			boolean isNotFirstFilteredAttribution = filteredAttributionIndex > 0;
			boolean isLastFilteredAttribution = filteredAttributionIndex == filteredAttributions.size() - 1;

			//Oxford Comma Definition: a comma used before the last item in a list of three or more items, before 'and' or 'or'
			if(isNotFirstFilteredAttribution) {
				if(isLastFilteredAttribution) {
					if(isFilteredAttributionSizeGreaterThanTwo) {
						bylineText.append(", and");
					}else {
						bylineText.append(" and");
					}
				} else {
					bylineText.append(",");
				}
			}
			bylineText.append(" ").append(filteredAttributions.get(filteredAttributionIndex).getAuthor().getDisplayName());
		}

		return bylineText.toString();
	}

	/**
	 * Special case of {@link AttributionTask#getBylineText(BaseDocumentEx, List)} that operates on a list of document wrappers.
	 */
	@Task(name = "BYLINETEXT")
	@TimedComponent(category = "task")
	public List<String> getBylineText(@TaskParameter(name = "list") List<DocumentTaxeneComposite<BaseDocumentEx>> documents,
	                                  @TaskParameter(name = "attributionTypes") List<String> attributionTypes) {
		List<String> bylines = new ArrayList<>();
		if (documents != null) {
			for (DocumentTaxeneComposite<BaseDocumentEx> doc : documents) {
				bylines.add(getBylineText(doc.getDocument(), attributionTypes));
			}
		}
		return bylines;
	}

	/**
	 * Special case of {@link AttributionTask#getBylineText(BaseDocumentEx, List)} that operates on a curated list of document wrappers.
	 */
	@Task(name = "BYLINETEXT")
	@TimedComponent(category = "task")
	public List<String> getBylineText(@TaskParameter(name = "curatedList") CuratedListEx<CuratedDocumentTaxeneComposite> documents,
	                                  @TaskParameter(name = "attributionTypes") List<String> attributionTypes) {
		List<String> bylines = new ArrayList<>();
		if (documents != null) {
			for (CuratedDocumentTaxeneComposite doc : documents.getData()) {
				bylines.add(getBylineText(doc.getDocument(), attributionTypes));
			}
		}
		return bylines;
	}

	@Task(name = "BYLINES")
	@TimedComponent(category = "task")
	public List<AttributionModel> fetchBylinesByDocument(@TaskParameter(name = "document") BaseDocumentEx document) {
		return fetchBylinesByDocument(document, null, true);
	}

	@Task(name = "BYLINES")
	@TimedComponent(category = "task")
	public List<AttributionModel> fetchBylinesByDocument(@TaskParameter(name = "document") BaseDocumentEx document,
														 @TaskParameter(name = "includeBioDocument") Boolean includeBioDocument) {
		return fetchBylinesByDocument(document, null, includeBioDocument);
	}

	@Task(name = "BYLINES")
	@TimedComponent(category = "task")
	public List<AttributionModel> fetchBylinesByDocument(@TaskParameter(name = "document") BaseDocumentEx document,
														 @TaskParameter(name = "types") List<String> types) {
		return fetchBylinesByDocument(document, types, true);
	}


	@Task(name = "BYLINES")
	@TimedComponent(category = "task")
	public List<AttributionModel> fetchBylinesByDocument(@TaskParameter(name = "document") BaseDocumentEx document,
														@TaskParameter(name = "types") List<String> types,
														@TaskParameter(name = "includeBioDocument") Boolean includeBioDocument) {
		if (document == null) return null;

		return mapAttributions(filterTypes(bylines(document), types), includeBioDocument)
				.collect(Collectors.toList());
	}

	@Task(name = "TAGLINES")
	public List<AttributionModel> fetchTaglinesByDocument(@TaskParameter(name = "document") BaseDocumentEx document) {
		return fetchTaglinesByDocument(document, null);
	}

	@Task(name = "TAGLINES")
	public List<AttributionModel> fetchTaglinesByDocument(@TaskParameter(name = "document") BaseDocumentEx document,
														  @TaskParameter(name = "types") List<String> types) {
		if (document == null) return null;

		return mapAttributions(filterTypes(taglines(document), types))
				.collect(Collectors.toList());
	}

	/**
	 * Get all attribution types
	 */
	@Task(name = "ATTRIBUTIONTYPES")
	public Map<String, AttributionType> getAttrbutionTypes() {
		return getAttributionTypes();
	}

	/**
	 * Get Attribution types for a category e.g. get all types which are marked as REVIEWER in category
	 */
	@Task(name = "ATTRIBUTIONTYPES")
	public Map<String, AttributionType> getAttrbutionTypesForCategory(@TaskParameter(name = "category") String category) {
		Map<String, AttributionType> answer = getAttributionTypes().entrySet().stream()
				.filter(entry -> entry.getValue().getTags() != null && entry.getValue().getTags().getCategory() != null)
				.filter(entry -> entry.getValue().getTags().getCategory().equals(category))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
		return answer;
	}

	/**
	 * Get Attribution type by name.
	 */
	@Task(name = "ATTRIBUTIONTYPES")
	public AttributionType getAttrbutionTypeByName(@TaskParameter(name = "filterType") String filterType) {
		return getAttributionTypes().get(filterType);
	}

	/**
	 * Gets the attribution types present in the bylines field of the document.
	 */
	@Task(name = "BYLINESATTRIBUTIONTYPES")
	public List<String> getBylinesAttrbutionTypes(@TaskParameter(name = "document") BaseDocumentEx document) {
		return distinctTypes(bylines(document));
	}

	/**
	 * Gets the attribution types present in the taglines field of the document.
	 */
	@Task(name = "TAGLINESATTRIBUTIONTYPES")
	public List<String> getTaglinesAttrbutionTypes(@TaskParameter(name = "document") BaseDocumentEx document) {
		return distinctTypes(taglines(document));
	}

	@Task(name = "ATTRIBUTIONENABLED")
	public boolean isAttributionEnabled() {
		return isAttributionEnabled;
	}

	/**
	 * Finds the latest date the doc was edited with. If that date is the published or updated date we will return that date and
	 * the author attribution. If the latest date is a date from one of the reviewers or users that isn't the author then
	 * we will return that date and the reviewer/user attribution.
	 * @param document
	 * @return an object that contains the latest date an object was updated with and the attribution associated with that date
	 */
	@Task(name = "BYLINESATTRIBUTIONDATE")
	public AttributionDate getAttributionDate(@TaskParameter(name = "document") BaseDocumentEx document) {
		AttributionDate attributionDate = new AttributionDate();
		SliceableListEx<DocumentAttribution> attributions = document.getBylines();

		DateTime publishedDate = document.getDates().getFirstPublished();
		DateTime updatedDate = document.getDates().getDisplayed();
		DateTime latestDate = (publishedDate != null && publishedDate.isAfter(updatedDate)) ? publishedDate : updatedDate;

		AttributionDate reviewerAttributionDate = getReviewerAttributionDate(attributions);
		if (reviewerAttributionDate != null && reviewerAttributionDate.getDate().isAfter(latestDate)) {
			return reviewerAttributionDate;
		}

		DocumentAttribution authorAttributionWithLatestDate = attributions.stream()
				.filter(attribution -> !isReviewer(attribution))
				.max(Comparator.comparing(DocumentAttribution::getLastModified, Comparator.nullsFirst(Comparator.naturalOrder())))
				.orElse(null);
		AttributionTypeEnum authorAttributionType =
				(authorAttributionWithLatestDate != null && BaseAuthor.UGC_USER.equals(authorAttributionWithLatestDate.getAuthorType()))
						? AttributionTypeEnum.UGC_RECIPE : AttributionTypeEnum.AUTHOR;

		attributionDate.setAttributionType(authorAttributionType.name());
		attributionDate.setDate(latestDate);
		return attributionDate;
	}

	/**
	 * This task is going to be used in {@link DeionSearchServiceTask#getArticlesByAuthorId(String, Integer, String, Integer)}.
	 * It will be used in a selene query that needs this list in order.
	 * I want a list of attribution id's from an authorId.
	 * The first item in that list will be the most important attributionId.
	 * The order is important because we will use the order in the list, to populate the first section of the recirc.
	 * The first item will usually be the author, but if there is no author then it will be some sort of reviewer type.
	 * The rest of the items will be their reviewer types.
	 * @param authorId - ex: 230271
	 * @return
	 */
	public ImmutableList<String> generateOrderedAttributionIdsFromAuthorId(String authorId) {
		SliceableListEx<Attribution> attributions = attributionService.getAttributionsByAuthorId(authorId);
		if (attributions == null) return null;
		List<String> ids = attributions.stream()
				// I would like the author to be first and then I do not care about the order of the rest. PCR-447
				.sorted(Comparator.comparingInt(this::getCompareValueOfAttributionType))
				.filter(attribution -> !attribution.getType().equals(AttributionTypeEnum.UPDATER.name()))
				.map(attribution -> attribution.getId())
				.collect(Collectors.toList());
		return ImmutableList.copyOf(ids);
	}

	/**
	 * Finds the latest date a reviewer edited the doc.
	 * @param attributions the bylines of the document
	 * @return an object that contains the latest date an editor edited and the attribution associated with that date
	 */
	@Task(name = "REVIEWERATTRIBUTIONDATE")
	public AttributionDate getReviewerAttributionDate(@TaskParameter(name = "attributions") SliceableListEx<DocumentAttribution> attributions) {
		DocumentAttribution reviewerAttributionWithLatestDate = attributions.stream()
				.filter(attribution -> isReviewer(attribution))
				.max(Comparator.comparing(DocumentAttribution::getLastModified, Comparator.nullsFirst(Comparator.naturalOrder())))
				.orElse(null);

		if (reviewerAttributionWithLatestDate != null) {
			AttributionDate attributionDate = new AttributionDate();
			attributionDate.setAttributionType(reviewerAttributionWithLatestDate.getType());
			attributionDate.setDate(reviewerAttributionWithLatestDate.getLastModified());
			return attributionDate;
		}

		return null;
	}

	/**
	 * This is making the assumption that all reviewers are just not authors.
	 * This is okay because bylines do not include tags like UPDATER or EDITOR (though taglines do)
	 * In some cases though product would like fact-checkers to not be lumped in with reviewers.
	 * Do not use this method if you need to separate fact-checkers. Documentation on reviewers here:
	 * https://dotdash.atlassian.net/wiki/spaces/DG/pages/1883308069/Bylines+Taglines+Part+One#Flow-%2F-Review-Stamping
	 * @param attribution
	 * @return boolean based on if it is not an author
	 */
	private boolean isReviewer(Attribution attribution) {
		return !attribution.getType().equals(AttributionTypeEnum.AUTHOR.name()) && !attribution.getType().equals(AttributionTypeEnum.UGC_RECIPE.name());
	}

	/**
	 * 	Product wants to sort bylines to make sure authors come first and fact checkers come last.
	 * 	Any type of reviewer to be in between. AXIS-2430
	 * @param attribution
	 * @return sorting value
	 */
	private int getCompareValueOfAttributionType(Attribution attribution) {
		AttributionTypeEnum attributionTypeEnum = AttributionTypeEnum.valueOf(attribution.getType());
		if (attributionTypeEnum == AttributionTypeEnum.AUTHOR || attributionTypeEnum == AttributionTypeEnum.UGC_RECIPE) {
			return -1;
		} else if (attributionTypeEnum == AttributionTypeEnum.FACT_CHECKER) {
			return 1;
		} else {
			return 0;
		}
	}

	private Map<String, AttributionType> getAttributionTypes() {
		return this.attributionService.getAttributionTypes();
	}

	private Stream<DocumentAttribution> attributions(BaseDocumentEx doc) {
		return Stream.concat(bylines(doc), taglines(doc));
	}

	private Stream<DocumentAttribution> bylines(BaseDocumentEx doc) {
		return doc.getBylines().stream();
	}

	private Stream<DocumentAttribution> taglines(BaseDocumentEx doc) {
		return doc.getTaglines().stream();
	}

	private List<String> distinctTypes(Stream<DocumentAttribution> attributionStream) {
		return attributionStream.map(attribution -> attribution.getType()).distinct().collect(Collectors.toList());
	}

	private Stream<DocumentAttribution> filterTypes(Stream<DocumentAttribution> attributionStream, List<String> types) {
		if (types == null) return attributionStream;
		return attributionStream.filter(attribution -> types.contains(attribution.getType()));
	}

	private Stream<AttributionModel> mapAttributions(Stream<DocumentAttribution> attributionStream) {
		return mapAttributions(attributionStream, true);
	}

	private Stream<AttributionModel> mapAttributions(Stream<DocumentAttribution> attributionStream,
													 Boolean includeBioDocument) {
		return attributionStream.map(attribution -> getAttributionModel(attribution, includeBioDocument));
	}

	private AttributionModel getAttributionModel(Attribution attribution) {
		return getAttributionModel(attribution, true);
	}

	private AttributionModel getAttributionModel(Attribution attribution, Boolean includeBioDocument) {
		BaseAuthor author = getAssociatedAuthor(attribution);
		AttributionModel.Builder builder = AttributionModel.builder().attribution(attribution).author(author);
		// Bio documents for UGC authors will be coming at a later date
		if (includeBioDocument && !(BaseAuthor.UGC_USER.equals(attribution.getAuthorType()))) {
			BioDocumentEx bioDocument = getAssociatedBioDocument((AuthorEx) author);
			builder = builder.bioDocument(bioDocument);
		}

		return builder.build();
	}

	private BaseAuthor getAssociatedAuthor(Attribution attribution) {
		BaseAuthor author = null;
		if (attribution != null && StringUtils.isNotBlank(attribution.getAuthorId())) {
			if (BaseAuthor.UGC_USER.equals(attribution.getAuthorType())) {
				UGCUserDto ugcUserDto = ugcUserService.getUgcUserDtoById(attribution.getAuthorId());
				author = ugcUserDto != null ? ugcUserDto.toUgcUser() : null;
			} else {
				author = authorService.getAuthorById(attribution.getAuthorId());
			}
		}
		return author;
	}

	private BioDocumentEx getAssociatedBioDocument(AuthorEx author) {
		BioDocumentEx bioDocument = null;
		if (author != null && StringUtils.isNotBlank(author.getBioUrl())) {
			try {
				BaseDocumentEx document = documentService.getDocument(DocumentService.createDocumentRequestContext(author.getBioUrl(), null, null));
				if (document instanceof BioDocumentEx) {
					bioDocument = (BioDocumentEx) document;
				}
			} catch (Exception e) {
				logger.error("Failed to get bio document associated with author [{}]", author, e);
			}
		}
		return bioDocument;
	}

}
