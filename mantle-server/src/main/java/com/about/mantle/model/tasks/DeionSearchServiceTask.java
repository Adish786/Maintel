package com.about.mantle.model.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.about.globe.core.exception.GlobeInvalidTaskParameterException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.TaskParameters;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.model.extended.DeionSearchResultItemEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.extended.docv2.DocumentTaxeneComposite;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.DeionSearchService;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext;
import com.about.mantle.utils.MantleSolrClientUtils;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tasks
public class DeionSearchServiceTask {
	private static final Logger logger = LoggerFactory.getLogger(DeionSearchServiceTask.class);
	protected final DeionSearchService deionSearchService;
	protected final String vertical;
	protected final DocumentTaxeneCompositeTask documentTaxeneCompositeTask;
	protected final AttributionTask attributionTask;

	@Deprecated
	public DeionSearchServiceTask(DeionSearchService deionSearchService) {
		this(deionSearchService, null, null, null);
	}

	public DeionSearchServiceTask(DeionSearchService deionSearchService, String vertical,
								  DocumentTaxeneCompositeTask documentTaxeneCompositeTask,
								  AttributionTask attributionTask) {
		this.deionSearchService = deionSearchService;
		this.vertical = vertical;
		this.documentTaxeneCompositeTask = documentTaxeneCompositeTask;
		this.attributionTask = attributionTask;
	}

	/**
	 * This method is meant to be used for when the user is doing a query.
	 * MantleSolrClientUtils.escapeQueryChars(query) will escape the query to prevent a malicious query.
	 */
	@Task(name = "deionSearch")
	@TimedComponent(category = "task")
	public DeionSearchResultEx escapedDeionSearch(@TaskParameter(name = "query") String query, @TaskParameter(name = "boost") String boost,
			@TaskParameter(name = "filters") List<String> filters, @TaskParameter(name = "facets") List<String> facets,
			@TaskParameter(name = "fields") List<String> fields, @TaskParameter(name = "sort") String sort,
			@TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit,
		    @TaskParameter(name = "traversalRelationships") List<String> traversalRelationships, @TaskParameter(name = "traversalStartDocId") List<Long> traversalStartDocId,
		    @TaskParameter(name = "traversalExcludedDocId") List<Long> traversalExcludedDocId) {
		return deionSearch(MantleSolrClientUtils.escapeQueryChars(query), boost, filters, facets, fields, sort, offset, limit, traversalRelationships, traversalStartDocId, traversalExcludedDocId);
	}

	protected DeionSearchResultEx deionSearch(@TaskParameter(name = "query") String query, @TaskParameter(name = "boost") String boost,
										   @TaskParameter(name = "filters") List<String> filters, @TaskParameter(name = "facets") List<String> facets,
										   @TaskParameter(name = "fields") List<String> fields, @TaskParameter(name = "sort") String sort,
										   @TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit,
									       @TaskParameter(name = "traversalRelationships") List<String> traversalRelationships, @TaskParameter(name = "traversalStartDocId") List<Long> traversalStartDocId,
										   @TaskParameter(name = "traversalExcludedDocId") List<Long> traversalExcludedDocId) {
		DeionSearchRequestContext.Builder builder = new DeionSearchRequestContext.Builder();
		builder.setQuery(query).setBoost(boost).setFilterQueries(filters)
				.setFacetFields(facets).setFields(fields).setSort(sort)
				.setOffset(offset).setLimit(limit)
				.setTraversalRelationships(traversalRelationships)
				.setTraversalStartDocId(traversalStartDocId)
				.setTraversalExcludedDocId(traversalExcludedDocId)
				.setIncludeDocumentSummaries(true).build();
		return deionSearchService.search(builder.build());
	}
	class BuilderCreator {
		private String authorId;
		private String sort;
		private Integer limit;
		private Integer offset;

		public  class Builder {
			private String authorId;
			private String sort;
			private Integer limit;
			private Integer offset;

			public Builder authorId(String autorId) {
				this.authorId = autorId;
				return this;
			}

			public Builder sort(String sort) {
				this.sort = sort;
				return this;
			}

			public Builder limit(Integer limit) {
				this.limit = limit;
				return this;
			}

			public Builder offset(Integer offset) {
				this.offset = offset;
				return this;
			}
			public BuilderCreator build() {
				return new BuilderCreator(this);
			}
		}

		private BuilderCreator(Builder builder) {
			this.authorId = builder.authorId;
			this.sort = builder.sort;
			this.limit = builder.limit;
			this.offset=builder.offset;
		}
	}
	BuilderCreator build = new BuilderCreator.Builder().authorId("").sort("").limit(0).offset(0).build();
	@Task(name = "articlesByAuthorId")
	@TimedComponent(category = "task")
	public List<DocumentTaxeneComposite<BaseDocumentEx>> getArticlesByAuthorId(@TaskParameter(name = "authorId") BuilderCreator builder) {
		if (vertical == null || documentTaxeneCompositeTask == null || attributionTask == null) {
			logger.warn("Do not try using articlesByAuthorId task without updating the VerticalDeionSearchServiceTask constructor");
			return new ArrayList<>();
		}
		ImmutableList<String> attributionIds = attributionTask.generateOrderedAttributionIdsFromAuthorId(builder.authorId);
		if (attributionIds == null) return new ArrayList<>();
		List<BaseDocumentEx> authorArticles = new ArrayList<>();
		for (String attributionId : attributionIds) {
			List<String> filterQueries = new ArrayList<>();
			filterQueries.add("vertical:" + vertical);
			filterQueries.addAll(excludedTemplateTypeQueries());
			filterQueries.add("byline_id:" + attributionId);
			if (authorArticles.size() < builder.limit) {
				DeionSearchResultEx result = deionSearch("*", null, filterQueries, null, null, StringUtils.isEmpty(builder.sort) ? "displayed DESC" : builder.sort, builder.offset, builder.limit, null, null, null);
				if (result != null && result.getItems() != null) {
					authorArticles.addAll(result.getItems().stream()
							.map(item -> item.getDocument())
							.filter(document -> document != null)
							.limit(builder.limit - authorArticles.size())
							.collect(Collectors.toList()));
				}
			}
		}

		return authorArticles.isEmpty() ? new ArrayList<>() : documentTaxeneCompositeTask.getDocumentTaxeneCompositeList(authorArticles);
	}

	@Task(name = "articlesByAuthorId")
	@TimedComponent(category = "task")
	public List<DocumentTaxeneComposite<BaseDocumentEx>> getArticlesByAuthorId(@TaskParameter(name = "authorId") String authorId) {
		return getArticlesByAuthorId(authorId, 16);
	}

	@Task(name = "articlesByAuthorId")
	@TimedComponent(category = "task")
	public List<DocumentTaxeneComposite<BaseDocumentEx>> getArticlesByAuthorId(@TaskParameter(name = "authorId") String authorId,
																			   @TaskParameter(name = "limit") Integer limit) {
		return getArticlesByAuthorId(authorId, limit, "");
	}

	@Task(name = "articlesByAuthorId")
	@TimedComponent(category = "task")
	public List<DocumentTaxeneComposite<BaseDocumentEx>> getArticlesByAuthorId(@TaskParameter(name = "authorId") String authorId,
																			   @TaskParameter(name = "limit") Integer limit,
																			   @TaskParameter(name = "sort") String sort) {
		return getArticlesByAuthorId(authorId, limit, sort, 0);
	}

	@Task(name = "articlesByAuthorId")
	@TimedComponent(category = "task")
	public List<DocumentTaxeneComposite<BaseDocumentEx>> getArticlesByAuthorId(@TaskParameter(name = "authorId") String authorId,
																			   @TaskParameter(name = "limit") Integer limit,
																			   @TaskParameter(name = "offset") Integer offset) {
		return getArticlesByAuthorId(authorId, limit, null, offset);
	}

	/**
	 * This task is used on bio templates to populate the recirc section at the bottom.
	 * First we grab the documents they have authored and then we look through to find the docs they have reviewed
	 */
	@Task(name = "articlesByAuthorId")
	@TimedComponent(category = "task")
	public List<DocumentTaxeneComposite<BaseDocumentEx>> getArticlesByAuthorId(@TaskParameter(name = "authorId") String authorId,
																			   @TaskParameter(name = "limit") Integer limit,
																			   @TaskParameter(name = "sort") String sort,
																			   @TaskParameter(name = "offset") Integer offset) {

		if (vertical == null || documentTaxeneCompositeTask == null || attributionTask == null) {
			logger.warn("Do not try using articlesByAuthorId task without updating the VerticalDeionSearchServiceTask constructor");
			return new ArrayList<>();
		}

		ImmutableList<String> attributionIds = attributionTask.generateOrderedAttributionIdsFromAuthorId(authorId);
		if (attributionIds == null) return new ArrayList<>();

		List<BaseDocumentEx> authorArticles = new ArrayList<>();
		for (String attributionId : attributionIds) {

			List<String> filterQueries = new ArrayList<>();
			filterQueries.add("vertical:" + vertical);
			filterQueries.addAll(excludedTemplateTypeQueries());
			filterQueries.add("byline_id:" + attributionId);

			if (authorArticles.size() < limit) {
				DeionSearchResultEx result = deionSearch("*", null, filterQueries, null, null, StringUtils.isEmpty(sort) ? "displayed DESC" : sort, offset, limit, null, null, null);
				if (result != null && result.getItems() != null) {
					authorArticles.addAll(result.getItems().stream()
							.map(item -> item.getDocument())
							.filter(document -> document != null)
							.limit(limit - authorArticles.size())
							.collect(Collectors.toList()));
				}
			}
		}

		return authorArticles.isEmpty() ? new ArrayList<>() : documentTaxeneCompositeTask.getDocumentTaxeneCompositeList(authorArticles);
	}

	@Task(name = "deionSearchForTaxonomyLists")
	@TimedComponent(category = "task")
	public List<BaseDocumentEx> deionSearchForTaxonomyLists(@TaskParameter(name = "docId") Long docId,
															@TaskParameter(name = "limit") Integer limit,
															@TaskParameter(name = "relationships") String relationships) {

		List<String> filterQueries = new ArrayList<>();
		filterQueries.add("vertical:" + vertical);
		filterQueries.addAll(excludedTemplateTypeQueries());

		DeionSearchResultEx results = deionSearch("*", null, filterQueries, null, null,
				"lastMaterialPublished DESC", 0, limit, Collections.singletonList(relationships),
				Collections.singletonList(docId), null);

		if (results != null && results.getItems() != null) {
			return results.getItems().stream().filter(Objects::nonNull).map(item -> item.getDocument()).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
		}
		return null;
	}

	/**
	 * Retrieves latest 'news and deals' content. If the current doc should not be included (ie the default behavior),
	 * the task will fetch 1 more document than requested and will either filter out the current document if it's
	 * in the results or filter out the extra requested document if the current document is not in the results.
	 * @param requestContext Used to get the doc ID to filter out if one is not provided
	 * @param model Accepts an Integer limit (default: 3), a BaseDocumentEx documentToFilterOut (default: the
	 *      requestContext's docId value), and a Boolean includeCurrentDoc (default: false).
	 * @return The latest 'news and deals' documents up to a given limit, excluding the current document if
	 * 		includeCurrentDoc is false
	 */
	@Task(name = "latestNewsAndDeals")
	@TimedComponent(category = "task")
	public List<BaseDocumentEx> getLatestNewsAndDeals(@RequestContextTaskParameter RequestContext requestContext,
													  @TaskParameters LatestNewsAndDealsModel model) {
		int limit = model.getLimit() != null ? model.getLimit() : 3;
		Long docIdToFilterOut = model.getDocumentToFilterOut() != null && model.getDocumentToFilterOut().getDocumentId() != null
				? model.getDocumentToFilterOut().getDocumentId()
				: requestContext.getUrlData() instanceof VerticalUrlData
					? ((VerticalUrlData) requestContext.getUrlData()).getDocId()
					: null;
		boolean includeCurrentDoc = Boolean.TRUE.equals(model.getIncludeCurrentDoc()) || docIdToFilterOut == null;
		String queryVertical = model.getVertical() != null ? model.getVertical() : vertical;

		List<BaseDocumentEx> newsAndDealsArticles = new ArrayList<>();

		List<String> filterQueries = new ArrayList<>();
		filterQueries.add("vertical:" + queryVertical);
		filterQueries.addAll(excludedTemplateTypeQueries());
		filterQueries.add("revenueGroup:COMMERCENEWSDEALS");

		DeionSearchResultEx result = deionSearch("*", null, filterQueries, null, null,
				"lastPublished DESC", 0, includeCurrentDoc ? limit : limit + 1, null, null, null);
		if (result != null && result.getItems() != null) {
			newsAndDealsArticles.addAll(result.getItems().stream()
					.map(DeionSearchResultItemEx::getDocument)
					.filter(Objects::nonNull)
					.filter(document -> includeCurrentDoc || !Objects.equals(document.getDocumentId(), docIdToFilterOut))
					.limit(limit)
					.collect(Collectors.toList()));
		}
		return newsAndDealsArticles;
	}

	@Task(name = "articlesByEntityReferenceTags")
	@TimedComponent(category = "task")
	public List<BaseDocumentEx> getArticlesByEntityReferenceTags(@TaskParameters EntityReferenceDocumentsModel entityReferenceDocumentsModel) {
		if(entityReferenceDocumentsModel.getEntityReferenceTags() == null) {
			throw new GlobeInvalidTaskParameterException("entityReferenceTags task parameter can not be null");
		}

		final int limit = entityReferenceDocumentsModel.getLimit() != null ? entityReferenceDocumentsModel.getLimit() : 16;
		final String queryVertical = entityReferenceDocumentsModel.getVertical() != null ? entityReferenceDocumentsModel.getVertical() : vertical;
		String entityReferenceTagsFilterQuery = entityReferenceDocumentsModel.getEntityReferenceTagsFilterQuery();

		List<String> filterQueries = new ArrayList<>();
		filterQueries.add("vertical:" + queryVertical);
		filterQueries.addAll(excludedTemplateTypeQueries());
		filterQueries.add("entityReferenceTags:" + entityReferenceTagsFilterQuery);

		DeionSearchResultEx result = deionSearch("*", null, filterQueries, null, null,
				"lastPublished DESC", 0, limit, null, null, null);

		List<BaseDocumentEx> entityReferenceDocuments = new ArrayList<>();
		if(result != null && result.getItems() != null) {
			entityReferenceDocuments.addAll(result.getItems().stream()
					.map(DeionSearchResultItemEx::getDocument)
					.filter(Objects::nonNull)
					.collect(Collectors.toList()));
		}
		return entityReferenceDocuments;
	}

	/**
	 * Creates a list of template types to exclude when getting {@link #getArticlesByAuthorId(String, Integer, String, Integer)}
	 * Meant to be overridden. Defaults to filtering out everything in {@link DeionSearchRequestContext#getDefaultTemplateTypesFilterOutQuery()}
	 * as well as BIO + TAXONOMY pages
	 */
	public static List<String> excludedTemplateTypeQueries() {
		return ImmutableList.of(DeionSearchRequestContext.getDefaultTemplateTypesFilterOutQuery(),
				"-templateType:BIO", "-templateType:TAXONOMY");
	}

	public static class LatestNewsAndDealsModel {

		private Integer limit;
		private BaseDocumentEx documentToFilterOut;
		private Boolean includeCurrentDoc;
		private String vertical;

		public Integer getLimit() {
			return limit;
		}

		public void setLimit(Integer limit) {
			this.limit = limit;
		}

		public BaseDocumentEx getDocumentToFilterOut() {
			return documentToFilterOut;
		}

		public void setDocumentToFilterOut(BaseDocumentEx documentToFilterOut) {
			this.documentToFilterOut = documentToFilterOut;
		}

		public Boolean getIncludeCurrentDoc() {
			return includeCurrentDoc;
		}

		public void setIncludeCurrentDoc(Boolean includeCurrentDoc) {
			this.includeCurrentDoc = includeCurrentDoc;
		}

		public String getVertical() {
			return vertical;
		}

		public void setVertical(String vertical) {
			this.vertical = vertical;
		}
	}

	private static class EntityReferenceDocumentsModel {
		private Integer limit;

		private SliceableListEx<String> entityReferenceTags;

		private String vertical;

		public Integer getLimit() {
			return limit;
		}

		public void setLimit(Integer limit) {
			this.limit = limit;
		}

		public SliceableListEx<String> getEntityReferenceTags() {
			return entityReferenceTags;
		}

		public void setEntityReferenceTags(SliceableListEx<String> entityReferenceTags) {
			this.entityReferenceTags = entityReferenceTags;
		}

		private String getEntityReferenceTagsFilterQuery() {
			StringBuilder entityReferenceTagsFilterQuery = new StringBuilder();

			if(entityReferenceTags == null || entityReferenceTags.isEmpty()) return StringUtils.EMPTY;

			entityReferenceTagsFilterQuery.append("(");

			entityReferenceTagsFilterQuery.append(String.join(" OR ", entityReferenceTags.getList()));

			entityReferenceTagsFilterQuery.append(")");

			return entityReferenceTagsFilterQuery.toString();
		}

		public String getVertical() {
			return vertical;
		}

		public void setVertical(String vertical) {
			this.vertical = vertical;
		}

	}
}
