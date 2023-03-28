package com.about.mantle.model.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.TaskParameters;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.DeionSearchService;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;

@Tasks
public class NewsTask {

	protected final DeionSearchService deionSearchService;
	protected final Long defaultNewsTaxonomyDocId;

	protected final List<String> NEWS_FILTER_QUERIES;
	protected static final String NEWS_SORT_DEFAULT = "firstPublished%20desc";
	private static final String DYNAMIC_NEWS_DEFAULT_TRAVERSAL_RELATIONSHIP = "curatedList";

	public NewsTask(DeionSearchService deionSearchService, String vertical, Long defaultNewsTaxonomyDocId) {

		NEWS_FILTER_QUERIES = ImmutableList.<String> of("-templateType:REDIRECT",
				"-templateType:BIO", "-templateType:TAXONOMY", "-templateType:TAXONOMYSC", "newsType:[* TO *]", "vertical:"+vertical);
		
		this.deionSearchService = deionSearchService;
		this.defaultNewsTaxonomyDocId = defaultNewsTaxonomyDocId;
	}

	/**
	 * getLatestNews
	 *
	 * Used to get the latest news articles, with optional filters and sorting.
	 * If both a newsTaxonomyDocId and newsTaxonomyDocIds are provided, only the newsTaxonomyDocId will be used.
	 *
	 * @param model contains limit (required, number of news articles requested back), sort (used to sort the results of the
	 *              search. ex "firstPublished desc"), filters (filters to add to the search), newsTaxonomyDocId
	 *              (the traversal start doc ID to use), newsTaxonomyDocIds (a comma separated
	 *              string of Doc IDs of traversal start doc IDs to use, ex: "5655001,9635104,7785025").
	 * @return List<BaseDocumentEx> of news articles
	 */
	@Task(name = "latestNews")
	public List<BaseDocumentEx> getLatestNews(@TaskParameters LatestNewsModel model) {

		DeionSearchRequestContext.Builder builder = new DeionSearchRequestContext.Builder();
		builder.setQuery("*")
				.setSort(model.getSort() != null
						? model.getSort()
						: NEWS_SORT_DEFAULT)
				.setLimit(model.getLimit())
				.setFilterQueries(model.getFilters() != null
						? model.getFilters()
						: NEWS_FILTER_QUERIES)
				.setIncludeDocumentSummaries(true);

		List<Long> traversalStartDocIds = model.getNewsTaxonomyDocId() != null
				? Collections.singletonList(model.getNewsTaxonomyDocId())
				: model.getNewsTaxonomyDocIds() != null
					? model.getNewsTaxonomyDocIdsList()
					: Collections.singletonList(defaultNewsTaxonomyDocId);

		builder.setTraversalStartDocId(traversalStartDocIds);
		builder.setTraversalRelationships(Collections.singletonList("primaryParent"));

		return getNewsUsingBuilder(builder);
	}

	/**
	 * latestNewsWithDefaultFilters
	 *
	 * Used to get the latest news from the news ordered by firstPublished for optionally provided newsTaxonomyDocId(s).
	 * If both a newsTaxonomyDocId and newsTaxonomyDocIds are provided, only the newsTaxonomyDocId will be used.
	 *
	 * @param model containing limit (number of news articles requested back),
	 *              newsTaxonomyDocId (optional, the traversal start doc ID to use), newsTaxonomyDocIds (optional, a
	 *              comma separated string of Doc IDs of traversal start doc IDs to use, ex: "5655001,9635104,7785025").
	 * @return
	 */
	@Deprecated
	@Task(name = "latestNewsWithDefaultFilters")
	public List<BaseDocumentEx> getLatestNewsWithDefaultFilters(@TaskParameters LatestNewsWithDefaultFiltersModel model){
		LatestNewsWithSpecifiedSortModel.Builder specifiedSortBuilder = new LatestNewsWithSpecifiedSortModel.Builder()
				.setLimit(model.getLimit())
				.setSort(NEWS_SORT_DEFAULT);
		if (model.getNewsTaxonomyDocId() != null) {
			specifiedSortBuilder.setNewsTaxonomyDocId(model.getNewsTaxonomyDocId());
		} else {
			specifiedSortBuilder.setNewsTaxonomyDocIds(model.getNewsTaxonomyDocIds());
		}
		return getLatestNewsWithSpecifiedSort(specifiedSortBuilder.build());
	}

	/**
	 * latestNewsWithSpecifiedSort
	 *
	 * Used to get the latest news from the news ordered by firstPublished for optionally provided newsTaxonomyDocId(s).
	 * If both a newsTaxonomyDocId and newsTaxonomyDocIds are provided, only the newsTaxonomyDocId will be used.
	 *
	 * @param model containing limit (number of news articles requested back), sort (used to sort the results of the
	 *              search. ex "displayed desc"), newsTaxonomyDocId (optional, the traversal start doc ID to use),
	 *              newsTaxonomyDocIds (optional, a comma separated string of Doc IDs of traversal start doc IDs to
	 *              use, ex: "5655001,9635104,7785025").
	 * @return
	 */
	@Deprecated
	@Task(name = "latestNewsWithSpecifiedSort")
	public List<BaseDocumentEx> getLatestNewsWithSpecifiedSort(@TaskParameters LatestNewsWithSpecifiedSortModel model){
		GetLatestNewsWithSpecifiedFiltersModel.Builder newsWithSpecifiedFiltersBuilder =
				new GetLatestNewsWithSpecifiedFiltersModel.Builder()
						.setLimit(model.getLimit())
						.setSort(model.getSort())
						.setFilters(NEWS_FILTER_QUERIES);
		if (model.getNewsTaxonomyDocId() != null) {
			newsWithSpecifiedFiltersBuilder.setNewsTaxonomyDocId(model.getNewsTaxonomyDocId());
		} else {
			newsWithSpecifiedFiltersBuilder.setNewsTaxonomyDocIds(model.getNewsTaxonomyDocIds());
		}
		return getLatestNewsWithSpecifiedFilters(newsWithSpecifiedFiltersBuilder.build());
	}
	
	/**
	 * getLatestNewsWithSpecifiedFilters
	 * 
	 * Used to get the latest news from the news ordered by a vertical selected sort
	 * and filters for optionally provided newsTaxonomyDocId(s).
	 * If both a newsTaxonomyDocId and newsTaxonomyDocIds are provided, only the newsTaxonomyDocId will be used.
	 * 
	 * @param model contains limit (number of news articles requested back), sort (used to sort the results of the
	 *              search. ex "firstPublished desc"), filters (filters to add to the search), newsTaxonomyDocId
	 *              (optional, the traversal start doc ID to use), newsTaxonomyDocIds (optional, a comma separated
	 *              string of Doc IDs of traversal start doc IDs to use, ex: "5655001,9635104,7785025").
	 * @return
	 */
	@Deprecated
	@Task(name = "getLatestNewsWithSpecifiedFilters")
	public List<BaseDocumentEx> getLatestNewsWithSpecifiedFilters(@TaskParameters GetLatestNewsWithSpecifiedFiltersModel model){
		List<Long> traversalStartDocIds = model.getNewsTaxonomyDocId() != null
				? Collections.singletonList(model.getNewsTaxonomyDocId())
				: model.getNewsTaxonomyDocIds() != null
					? model.getNewsTaxonomyDocIdsList()
					: Collections.singletonList(defaultNewsTaxonomyDocId);

		DeionSearchRequestContext.Builder builder = new DeionSearchRequestContext.Builder();
		builder.setQuery("*")
				.setSort(model.getSort())
				.setLimit(model.getLimit())
				.setFilterQueries(model.getFilters())
				.setIncludeDocumentSummaries(true);

		if (traversalStartDocIds != null && !traversalStartDocIds.isEmpty()) {
			builder.setTraversalStartDocId(traversalStartDocIds);
			builder.setTraversalRelationships(Collections.singletonList("primaryParent"));
		}

		return getNewsUsingBuilder(builder);
	}

	/**
	 * Used to get a dynamic latest news feed to surface the latest content using default news filters
	 *
	 * @param limit - max number of news articles requested back
	 * @param programmedSummaryDocId - a programmed summary doc ID generated by selene/greenhouse
	 */
	@Task(name = "dynamicNewsFeedWithDefaultFilters")
	public List<BaseDocumentEx> getDynamicNewsFeed(@TaskParameter(name = "limit") Integer limit,
												   @TaskParameter(name = "programmedSummaryDocId") Long programmedSummaryDocId) {
		return getDynamicNewsFeed(limit, programmedSummaryDocId, NEWS_FILTER_QUERIES);
	}

	/**
	 * Used to get a dynamic latest news feed to surface the latest content by limit, programmed summary doc ID,
	 * and filters
	 * NOTE: Be aware that the results of this query can get shared/cached with other tasks so as a result no
	 * projections were done for this query as those other tasks are using certain fields from the results.
	 * See AXIS-3789 for more details.
	 *
	 * @param limit - max number of news articles requested back
	 * @param programmedSummaryDocId - a programmed summary doc ID generated by selene/greenhouse
	 * @param filters - filters to add to the search
	 */
	@Task(name = "dynamicNewsFeed")
	public List<BaseDocumentEx> getDynamicNewsFeed(@TaskParameter(name = "limit") Integer limit,
												   @TaskParameter(name = "programmedSummaryDocId") Long programmedSummaryDocId,
												   @TaskParameter(name = "filters") List<String> filters) {
		DeionSearchRequestContext.Builder builder = new DeionSearchRequestContext.Builder();
		builder.setQuery("*")
				.setSort(NEWS_SORT_DEFAULT)
				.setLimit(limit)
				.setFilterQueries(filters)
				.setNoCache(true)
				.setIncludeDocumentSummaries(true);

		builder.setTraversalStartDocId(Collections.singletonList(programmedSummaryDocId));
		builder.setTraversalRelationships(Collections.singletonList(DYNAMIC_NEWS_DEFAULT_TRAVERSAL_RELATIONSHIP));

		return getNewsUsingBuilder(builder);
	}

	private List<BaseDocumentEx> getNewsUsingBuilder(DeionSearchRequestContext.Builder builder) {
		DeionSearchResultEx results = deionSearchService.search(builder.build());

		return results.getItems().stream().map(item -> item.getDocument()).collect(Collectors.toCollection(ArrayList::new));
	}

	public static class LatestNewsModel {
		private Integer limit;
		private Long newsTaxonomyDocId;
		// array of comma separated Doc IDs (ex: "5655001,9635104,7785025")
		private String newsTaxonomyDocIds;
		private String sort;
		private List<String> filters;

		public Integer getLimit() {
			return limit;
		}

		public void setLimit(Integer limit) {
			this.limit = limit;
		}

		public Long getNewsTaxonomyDocId() {
			return newsTaxonomyDocId;
		}

		public void setNewsTaxonomyDocId(Long newsTaxonomyDocId) {
			this.newsTaxonomyDocId = newsTaxonomyDocId;
		}

		public String getNewsTaxonomyDocIds() {
			return newsTaxonomyDocIds;
		}

		public List<Long> getNewsTaxonomyDocIdsList() {
			if (StringUtils.isBlank(newsTaxonomyDocIds)) {
				return new ArrayList<>();
			} else {
				try {
					return Stream.of(newsTaxonomyDocIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
				} catch (NumberFormatException e) {
					throw new GlobeException("The newsTaxonomyDocIds parameter value " + newsTaxonomyDocIds +
							" passed to the NewsTask could not be parsed. Make sure you're passing in a comma" +
							" separated string of Doc IDs, ex: \"5655001,9635104,7785025\".");
				}

			}
		}

		public void setNewsTaxonomyDocIds(String newsTaxonomyDocIds) {
			this.newsTaxonomyDocIds = newsTaxonomyDocIds;
		}

		public String getSort() {
			return sort;
		}

		public void setSort(String sort) {
			this.sort = sort;
		}

		public List<String> getFilters() {
			return filters;
		}

		public void setFilters(List<String> filters) {
			this.filters = filters;
		}
	}

	@Deprecated
	public static abstract class LatestNewsBaseModel {
		private Integer limit;
		private Long newsTaxonomyDocId;
		// array of comma separated Doc IDs (ex: "5655001,9635104,7785025")
		private String newsTaxonomyDocIds;

		public static abstract class Builder<T extends Builder<T>> {
			protected abstract T self();

			protected Integer limit;
			protected Long newsTaxonomyDocId;
			// array of comma separated Doc IDs (ex: "5655001,9635104,7785025")
			protected String newsTaxonomyDocIds;
			public T setLimit(Integer limit) {
				this.limit = limit;
				return self();
			}

			public T setNewsTaxonomyDocId(Long newsTaxonomyDocId) {
				this.newsTaxonomyDocId = newsTaxonomyDocId;
				return self();
			}

			public T setNewsTaxonomyDocIds(String newsTaxonomyDocIds) {
				this.newsTaxonomyDocIds = newsTaxonomyDocIds;
				return self();
			}
		}

		public Integer getLimit() {
			return limit;
		}

		public void setLimit(Integer limit) {
			this.limit = limit;
		}

		public Long getNewsTaxonomyDocId() {
			return newsTaxonomyDocId;
		}

		public void setNewsTaxonomyDocId(Long newsTaxonomyDocId) {
			this.newsTaxonomyDocId = newsTaxonomyDocId;
		}

		public String getNewsTaxonomyDocIds() {
			return newsTaxonomyDocIds;
		}

		public List<Long> getNewsTaxonomyDocIdsList() {
			if (StringUtils.isBlank(newsTaxonomyDocIds)) {
				return new ArrayList<>();
			} else {
				try {
					return Stream.of(newsTaxonomyDocIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
				} catch (NumberFormatException e) {
					throw new GlobeException("The newsTaxonomyDocIds parameter value " + newsTaxonomyDocIds +
							" passed to the NewsTask could not be parsed. Make sure you're passing in a comma" +
							" separated string of Doc IDs, ex: \"5655001,9635104,7785025\".");
				}

			}
		}

		public void setNewsTaxonomyDocIds(String newsTaxonomyDocIds) {
			this.newsTaxonomyDocIds = newsTaxonomyDocIds;
		}
	}

	@Deprecated
	public static class LatestNewsWithDefaultFiltersModel extends LatestNewsBaseModel {}

	@Deprecated
	public static class LatestNewsWithSpecifiedSortModel extends LatestNewsBaseModel {
		private String sort;

		public LatestNewsWithSpecifiedSortModel() {}
		public LatestNewsWithSpecifiedSortModel(Builder builder) {
			this.setLimit(builder.limit);
			this.setNewsTaxonomyDocId(builder.newsTaxonomyDocId);
			this.setNewsTaxonomyDocIds(builder.newsTaxonomyDocIds);
			this.setSort(builder.sort);
		}

		public static class Builder extends LatestNewsBaseModel.Builder<Builder> {
			private String sort;

			@Override
			protected Builder self() {
				return this;
			}

			public Builder setSort(String sort) {
				this.sort = sort;
				return this;
			}

			public LatestNewsWithSpecifiedSortModel build() {
				return new LatestNewsWithSpecifiedSortModel(this);
			}
		}

		public String getSort() {
			return sort;
		}

		public void setSort(String sort) {
			this.sort = sort;
		}
	}

	@Deprecated
	public static class GetLatestNewsWithSpecifiedFiltersModel extends LatestNewsBaseModel {
		private String sort;
		private List<String> filters;

		public GetLatestNewsWithSpecifiedFiltersModel() {}
		public GetLatestNewsWithSpecifiedFiltersModel(GetLatestNewsWithSpecifiedFiltersModel.Builder builder) {
			this.setLimit(builder.limit);
			this.setNewsTaxonomyDocId(builder.newsTaxonomyDocId);
			this.setNewsTaxonomyDocIds(builder.newsTaxonomyDocIds);
			this.setSort(builder.sort);
			this.setFilters(builder.filters);
		}

		public static class Builder extends LatestNewsBaseModel.Builder<Builder> {
			private String sort;
			private List<String> filters;

			@Override
			protected Builder self() {
				return this;
			}

			public Builder setSort(String sort) {
				this.sort = sort;
				return this;
			}

			public Builder setFilters(List<String> filters) {
				this.filters = filters;
				return this;
			}

			public GetLatestNewsWithSpecifiedFiltersModel build() {
				return new GetLatestNewsWithSpecifiedFiltersModel(this);
			}
		}

		public String getSort() {
			return sort;
		}

		public void setSort(String sort) {
			this.sort = sort;
		}

		public List<String> getFilters() {
			return filters;
		}

		public void setFilters(List<String> filters) {
			this.filters = filters;
		}
	}
}
