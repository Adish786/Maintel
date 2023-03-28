package com.about.mantle.model.tasks;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.TaskParameters;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.journey.AbstractJourneyNode;
import com.about.mantle.model.journey.JourneyRelationshipType;
import com.about.mantle.model.journey.JourneyRoot;
import com.about.mantle.model.journey.JourneySection;
import com.about.mantle.model.journey.JourneyType;
import com.about.mantle.model.services.JourneyService;
import com.about.mantle.model.services.JourneyService.JourneyRequestContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import org.apache.commons.lang3.tuple.Pair;

@Tasks
public class JourneyTask {

	private final JourneyService journeyService;

	public JourneyTask(JourneyService journeyService) {
		this.journeyService = journeyService;
	}

	/**
	 * Returns true if the passed-in document is the journey root document.
	 * TODO this should really be a static helper function exposed to SPEL, not a task.
	 * @param model contains the docId and journeyRoot params
	 * @return
	 */
	@Task(name = "isJourneyRootDocument")
	public Boolean isJourneyRoot(@RequestContextTaskParameter RequestContext requestContext,
								 @TaskParameters IsJourneyRootDocumentModel model) {
		Long docId;
		JourneyRoot journeyRoot;
		if (model.getDocId() == null && model.getJourneyRoot() == null) {
			docId = getDocId(requestContext);
			journeyRoot = getJourneyStructure(docId);
		} else {
			docId = model.getDocId();
			journeyRoot = model.getJourneyRoot();
		}

		if (docId == null || !validJourney(journeyRoot)) return false;

		return journeyRoot.getDocument() != null && docId.equals(journeyRoot.getDocument().getDocumentId());
	}

	/**
	 * Returns the journey section that the provided document belongs to
	 * TODO this should really be a static helper function exposed to SPEL, not a task.
	 * @param model contains the docId and journeyRoot params
	 * @return
	 */
	@Task(name = "currentJourneySection")
	public JourneySection getCurrentJourneySection(@RequestContextTaskParameter RequestContext requestContext,
												   @TaskParameters CurrentJourneySectionModel model) {
		Long docId;
		JourneyRoot journeyRoot;
		if (model.getDocId() == null && model.getJourneyRoot() == null) {
			docId = getDocId(requestContext);
			journeyRoot = getJourneyStructure(docId);
		} else {
			docId = model.getDocId();
			journeyRoot = model.getJourneyRoot();
		}

		if (docId == null || journeyRoot == null) return null;

		// Iterate through journey sections
		for (JourneySection section : journeyRoot.getSections()) {
			// Check if given docId is actually the section's Id
			if (section.getNode() != null && docId.equals(section.getNode().getDocId())) return section;

			// Iterate through all documents of the section, and look for the given docId
			boolean docIdExists = section.getJourneyDocuments().stream().anyMatch(
					document -> document.getDocument() != null && docId.equals(document.getDocument().getDocumentId()));
			if (docIdExists) return section;
		}
		return null;
	}

	/**
	 * Returns the journey root given the docId.  Can be any document in the journey.
	 * @param docId
	 * @return
	 */
	@Deprecated
	public JourneyRoot getJourneyStructure(Long docId) {
		return getJourneyStructure(null, new JourneyStructureModel.Builder().setDocId(docId).build());
	}

	/**
	 * Returns the journey root (with documents populated) given the docId.  Can be any document in the journey.
	 * @param requestContext
	 * @param model An object that contains docId (the JourneyRoot doc ID), includeDocumentSummaries,
	 *              and projection
	 * @return a fully populated {@link JourneyRoot} object, which contains the full journey structure.
	 */
	@Task(name = "journeyStructure")
	@TimedComponent(category = "task")
	public JourneyRoot getJourneyStructure(@RequestContextTaskParameter RequestContext requestContext,
										   @TaskParameters JourneyStructureModel model) {
		Long docId = model.getDocId() != null
				? model.getDocId()
				: requestContext != null
						? getDocId(requestContext)
						: null;
		boolean includeDocumentSummaries = Objects.requireNonNullElse(model.getIncludeDocumentSummaries(), true);
		return getJourneyStructureAndRelationship(docId, includeDocumentSummaries, model.getProjection()).getLeft();
	}

	/**
	 * Returns all leaf documents for a given journey, across all sections.  Optionally includes the root at the top
	 * @param model containing the journeyRoot and includeRoot
	 * @return
	 */
	@Task(name = "getAllJourneyDocuments")
	public List<AbstractJourneyNode> getAllJourneyDocuments(@TaskParameters GetAllJourneyDocumentsModel model) {
		boolean includeRoot = Objects.requireNonNullElse(model.getIncludeRoot(), false);
		Builder<AbstractJourneyNode> documents = ImmutableList.builder();
		if (!validJourney(model.getJourneyRoot())) return documents.build();

		if (includeRoot) documents.add(model.getJourneyRoot());
		model.getJourneyRoot().getSections().forEach(section -> documents.addAll(section.getJourneyDocuments()));

		return documents.build();
	}

	/**
	 *
	 * @param documentList
	 * @param index
	 * @return
	 */
	protected List<AbstractJourneyNode> getDocumentsBefore(
			@TaskParameter(name = "documentList") List<AbstractJourneyNode> documentList,
			@TaskParameter(name = "index") int index) {
		if (index <= 0) return ImmutableList.of();

		return documentList.subList(0, index);
	}

	/**
	 * Returns all documents after the provided index
	 * @param documentList
	 * @param index
	 * @return
	 */
	protected List<AbstractJourneyNode> getDocumentsAfter(
			@TaskParameter(name = "documentList") List<AbstractJourneyNode> documentList,
			@TaskParameter(name = "index") int index) {
		if (index + 1 >= documentList.size()) return ImmutableList.of();
		
		return documentList.subList(index + 1, documentList.size());
	}

	/**
	 * Returns true if journey only has a single section
	 * @param journeyRoot
	 * @return
	 */
	@Task(name = "isSingleSectionJourney")
	public boolean isSingleSectionJourney(@TaskParameter(name = "journeyRoot") JourneyRoot journeyRoot) {
		return (validJourney(journeyRoot) && journeyRoot.getSections().size() == 1);
	}

	/**
	 * Returns {@link JourneyType} for a given journey root.  Will return null if the journeyRoot is not valid.
	 * @param journeyRoot
	 * @return
	 */
	@Task(name = "journeyType")
	public JourneyType getJourneyType(@TaskParameter(name = "journeyRoot") JourneyRoot journeyRoot) {

		JourneyType answer;
		if (!validJourney(journeyRoot)) {
			answer = null;
		} else if (journeyRoot.getDocument().getTemplateType().equals(TemplateTypeEx.PROGRAMMEDSUMMARY) && journeyRoot.getSections().size() == 1) {
			answer = JourneyType.MINI;
		} else {
			answer = JourneyType.STANDARD;
		}

		return answer;
	}

	@Task(name = "journeyRelationshipType")
	public JourneyRelationshipType getJourneyRelationshipType(@TaskParameter(name = "docId") Long docId) {
		return getJourneyStructureAndRelationship(docId, true, null).getRight();
	}

	/**
	 * Returns true if the JourneyRoot is a valid journey
	 * @param model A model containing the journeyRoot
	 * @return
	 */
	@Task(name = "isJourneyDocument")
	public boolean isJourneyDocument(@RequestContextTaskParameter RequestContext requestContext,
									 @TaskParameters IsJourneyDocumentModel model) {
		JourneyRoot journeyRoot;
		if (model.getJourneyRoot() == null) {
			Long docId = getDocId(requestContext);
			journeyRoot = getJourneyStructure(docId);
		} else {
			journeyRoot = model.getJourneyRoot();
		}
		return validJourney(journeyRoot);
	}

	/**
	 * Returns a list all documents in a Journey in the following order:
	 * 	 1. All documents after the specified document until the end of the Journey
	 * 	 2. All documents before the specified document from the first until the specified document
	 * 	 3. The specified document
	 * @param model contains the values for documentList, docId (the document around which to order), journeyRoot (the root document of the Journey)
	 *              and ignoreDocumentAbsence (return all journey documents even if the provided docId isn't within the journey)
	 * @return the list of ordered documents
	 */
	@Task(name = "journeyDocumentsAfterToBeforeInclusive")
	public List<AbstractJourneyNode> getDocumentsAfterToBeforeInclusive(
			@TaskParameters JourneyDocumentsAfterToBeforeInclusiveModel model) {
		boolean ignoreDocumentAbsence = Objects.requireNonNullElse(model.getIgnoreDocumentAbsence(), false);

		List<AbstractJourneyNode> documentList = null;
		if (model.getJourneyRoot() != null) {
			if (!validJourney(model.getJourneyRoot())) return ImmutableList.of();
			documentList = getAllJourneyDocuments(new GetAllJourneyDocumentsModel.Builder()
					.setJourneyRoot(model.getJourneyRoot())
					.setIncludeRoot(true)
					.build());
		} else {
			documentList = model.getDocumentList();
		}
		if (isEmpty(documentList)) return ImmutableList.of();

		OptionalInt index = getDocumentIndex(documentList, model.getDocId());

		if (!index.isPresent()) {
			if (Boolean.TRUE.equals(ignoreDocumentAbsence)) return documentList;

			return ImmutableList.of();
		}

		List<AbstractJourneyNode> result = new ArrayList<>();
		getDocumentsAfter(documentList, index.getAsInt()).stream()
				.filter(document -> !(document instanceof JourneyRoot)).forEach(document -> result.add(document));
		getDocumentsBefore(documentList, index.getAsInt()).stream()
				.filter(document -> !(document instanceof JourneyRoot)).forEach(document -> result.add(document));
		result.add(documentList.get(index.getAsInt()));

		return result;
	}

    /**
     * Returns a special map of journey sections, organized by those with only one document ("singleDocumentSections")
	 * and those with more ("multipleDocumentSections")
     *
     * @param model model containing the docId of any document in the journey
     *
     * @return the special map of journey sections
     */
    @Task(name = "journeySectionSizeMap")
    public Map<String, List<JourneySection>> journeySectionSizeMap(@RequestContextTaskParameter RequestContext requestContext,
																   @TaskParameters JourneySectionSizeMapModel model) {
		Long docId = model.getDocId() != null
				? model.getDocId()
				: requestContext != null
					? getDocId(requestContext)
					: null;
        JourneyRoot journey = getJourneyStructure(docId);
        List<JourneySection> singleDocSections = new ArrayList<>();
        List<JourneySection> multipleDocSections = new ArrayList<>();
        Map<String, List<JourneySection>> sections = new HashMap<>();

        for (JourneySection section : journey.getSections()) {
            if (section.getJourneyDocuments().size() == 1) {
                singleDocSections.add(section);
            } else if (section.getJourneyDocuments().size() > 1) {
                multipleDocSections.add(section);
            }
        }

        sections.put("singleDocumentSections", singleDocSections);
        sections.put("multipleDocumentSections", multipleDocSections);

        return sections;
    }

	protected OptionalInt getDocumentIndex(List<AbstractJourneyNode> documentList, Long docId) {
		return IntStream.range(0, documentList.size()).filter(i -> documentList.get(i).getDocument() != null
				&& docId.equals(documentList.get(i).getDocument().getDocumentId())).findFirst();
	}

	/**
	 * Checks to see if a journey is valid (eg, does it have sections)
	 * @param journeyRoot
	 * @return
	 */
	protected boolean validJourney(JourneyRoot journeyRoot) {
		return journeyRoot != null && !isEmpty(journeyRoot.getSections());
	}

	private Pair<JourneyRoot, JourneyRelationshipType> getJourneyStructureAndRelationship(Long docId,
																						  Boolean includeDocumentSummaries,
																						  String projection) {
		if (docId == null) return Pair.of(JourneyService.NULL_JOURNEY, JourneyRelationshipType.NONE);

		JourneyRequestContext reqCtx = new JourneyRequestContext.Builder()
				.setDocId(docId)
				.setIncludeDocumentSummaries(includeDocumentSummaries)
				.setProjection(projection)
				.build();

		return journeyService.getJourneyRootAndRelationship(reqCtx);
	}

    /**
     * Returns the docId of the current URL
     *
     * @param requestContext
     *
     * @return the docId of the current URL
     */
	private static Long getDocId(RequestContext requestContext) {
		return ((VerticalUrlData) requestContext.getUrlData()).getDocId();
	}

	public static class JourneyStructureModel {
		private Long docId;
		private Boolean includeDocumentSummaries;
		private String projection;

		public JourneyStructureModel() {}
		public JourneyStructureModel(Builder builder) {
			docId = builder.docId;
			includeDocumentSummaries = builder.includeDocumentSummaries;
			projection = builder.projection;
		}

		public static class Builder {
			private Long docId;
			private Boolean includeDocumentSummaries;
			private String projection;

			public Builder setDocId(Long docId) {
				this.docId = docId;
				return this;
			}

			public Builder setIncludeDocumentSummaries(Boolean includeDocumentSummaries) {
				this.includeDocumentSummaries = includeDocumentSummaries;
				return this;
			}

			public Builder setProjection(String projection) {
				this.projection = projection;
				return this;
			}

			public JourneyStructureModel build() {
				return new JourneyStructureModel(this);
			}
		}

		public Long getDocId() {
			return docId;
		}

		public void setDocId(Long docId) {
			this.docId = docId;
		}

		public Boolean getIncludeDocumentSummaries() {
			return includeDocumentSummaries;
		}

		public void setIncludeDocumentSummaries(Boolean includeDocumentSummaries) {
			this.includeDocumentSummaries = includeDocumentSummaries;
		}

		public String getProjection() {
			return projection;
		}

		public void setProjection(String projection) {
			this.projection = projection;
		}
	}

	public static class GetAllJourneyDocumentsModel {
		private JourneyRoot journeyRoot;
		private Boolean includeRoot;

		public GetAllJourneyDocumentsModel() {}
		public GetAllJourneyDocumentsModel(Builder builder) {
			journeyRoot = builder.journeyRoot;
			includeRoot = builder.includeRoot;
		}

		public static class Builder {
			private JourneyRoot journeyRoot;
			private Boolean includeRoot;

			public Builder setJourneyRoot(JourneyRoot journeyRoot) {
				this.journeyRoot = journeyRoot;
				return this;
			}

			public Builder setIncludeRoot(Boolean includeRoot) {
				this.includeRoot = includeRoot;
				return this;
			}

			public GetAllJourneyDocumentsModel build() {
				return new GetAllJourneyDocumentsModel(this);
			}
		}

		public JourneyRoot getJourneyRoot() {
			return journeyRoot;
		}

		public void setJourneyRoot(JourneyRoot journeyRoot) {
			this.journeyRoot = journeyRoot;
		}

		public Boolean getIncludeRoot() {
			return includeRoot;
		}

		public void setIncludeRoot(Boolean includeRoot) {
			this.includeRoot = includeRoot;
		}
	}

	public static class JourneyDocumentsAfterToBeforeInclusiveModel {
		private JourneyRoot journeyRoot;
		private List<AbstractJourneyNode> documentList;
		private Long docId;
		private Boolean ignoreDocumentAbsence;

		public JourneyRoot getJourneyRoot() {
			return journeyRoot;
		}

		public void setJourneyRoot(JourneyRoot journeyRoot) {
			this.journeyRoot = journeyRoot;
		}

		public List<AbstractJourneyNode> getDocumentList() {
			return documentList;
		}

		public void setDocumentList(List<AbstractJourneyNode> documentList) {
			this.documentList = documentList;
		}

		public Long getDocId() {
			return docId;
		}

		public void setDocId(Long docId) {
			this.docId = docId;
		}

		public Boolean getIgnoreDocumentAbsence() {
			return ignoreDocumentAbsence;
		}

		public void setIgnoreDocumentAbsence(Boolean ignoreDocumentAbsence) {
			this.ignoreDocumentAbsence = ignoreDocumentAbsence;
		}
	}

	public static class IsJourneyDocumentModel {
		private JourneyRoot journeyRoot;

		public JourneyRoot getJourneyRoot() {
			return journeyRoot;
		}

		public void setJourneyRoot(JourneyRoot journeyRoot) {
			this.journeyRoot = journeyRoot;
		}
	}

	public static abstract class AbstractDocIdTaskParameterModel {
		private Long docId;

		public Long getDocId() {
			return docId;
		}

		public void setDocId(Long docId) {
			this.docId = docId;
		}
	}

	public static abstract class AbstractDocIdJourneyRootTaskParameterModel {
		private Long docId;
		private JourneyRoot journeyRoot;

		public Long getDocId() {
			return docId;
		}

		public void setDocId(Long docId) {
			this.docId = docId;
		}

		public JourneyRoot getJourneyRoot() {
			return journeyRoot;
		}

		public void setJourneyRoot(JourneyRoot journeyRoot) {
			this.journeyRoot = journeyRoot;
		}
	}
	public static class JourneySectionSizeMapModel extends AbstractDocIdTaskParameterModel {}

	public static class CurrentJourneySectionModel extends AbstractDocIdJourneyRootTaskParameterModel {}

	public static class IsJourneyRootDocumentModel extends AbstractDocIdJourneyRootTaskParameterModel {}
}