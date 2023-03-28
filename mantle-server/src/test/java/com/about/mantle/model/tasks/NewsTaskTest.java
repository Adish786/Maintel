package com.about.mantle.model.tasks;

import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.services.DeionSearchService;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewsTaskTest {
    private static final Long DEFAULT_NEWS_TAXONOMY_DOC_ID = 100000L;
    private static final Integer LIMIT = 3;
    private static final String VERTICAL = "people";
    protected static final String NEWS_SORT = NewsTask.NEWS_SORT_DEFAULT;
    protected static final String CUSTOM_NEWS_SORT = "firstPublished%20asc";
    private static final List<String> NEWS_FILTER_QUERIES =
            ImmutableList.of("-templateType:REDIRECT", "-templateType:BIO", "-templateType:TAXONOMY",
                    "-templateType:TAXONOMYSC", "newsType:[* TO *]", "vertical:" + VERTICAL);
    private static final List<String> CUSTOM_NEWS_FILTER_QUERIES =
            ImmutableList.of("-templateType:REDIRECT", "vertical:" + VERTICAL);

    private NewsTask newsTask;
    private DeionSearchService deionSearchService;

    @Before
    public void setUp() {
        deionSearchService = mock(DeionSearchService.class);
        newsTask = new NewsTask(deionSearchService, VERTICAL, DEFAULT_NEWS_TAXONOMY_DOC_ID);

        DeionSearchResultEx results = new DeionSearchResultEx();
        results.setItems(new ArrayList<>());
        when(deionSearchService.search(any(DeionSearchService.DeionSearchRequestContext.class)))
                .thenReturn(results);
    }

    @Test
    public void testLatestNewsWithDefaultFiltersUsingSingleDocId() {
        NewsTask.LatestNewsWithDefaultFiltersModel parameters = new NewsTask.LatestNewsWithDefaultFiltersModel();
        parameters.setLimit(LIMIT);
        parameters.setNewsTaxonomyDocId(100001L);
        newsTask.getLatestNewsWithDefaultFilters(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(100001L));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testLatestNewsWithDefaultFiltersUsingNoDocId() {
        NewsTask.LatestNewsWithDefaultFiltersModel parameters = new NewsTask.LatestNewsWithDefaultFiltersModel();
        parameters.setLimit(LIMIT);
        newsTask.getLatestNewsWithDefaultFilters(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(DEFAULT_NEWS_TAXONOMY_DOC_ID));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testLatestNewsWithDefaultFiltersUsingSingleDocIdAndMultipleDocIds() {
        NewsTask.LatestNewsWithDefaultFiltersModel parameters = new NewsTask.LatestNewsWithDefaultFiltersModel();
        parameters.setLimit(LIMIT);
        parameters.setNewsTaxonomyDocId(100001L);
        parameters.setNewsTaxonomyDocIds("100002,100003,100004");
        newsTask.getLatestNewsWithDefaultFilters(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(100001L));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testLatestNewsWithDefaultFiltersUsingSingleMultipleDocId() {
        NewsTask.LatestNewsWithDefaultFiltersModel parameters = new NewsTask.LatestNewsWithDefaultFiltersModel();
        parameters.setLimit(LIMIT);
        parameters.setNewsTaxonomyDocIds("100002");
        newsTask.getLatestNewsWithDefaultFilters(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(100002L));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testLatestNewsWithDefaultFiltersUsingMultipleDocIds() {
        NewsTask.LatestNewsWithDefaultFiltersModel parameters = new NewsTask.LatestNewsWithDefaultFiltersModel();
        parameters.setLimit(LIMIT);
        parameters.setNewsTaxonomyDocIds("100002,100003,100004");
        newsTask.getLatestNewsWithDefaultFilters(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(100002L,100003L,100004L));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testLatestNewsWithSpecifiedSortUsingSingleDocId() {
        NewsTask.LatestNewsWithSpecifiedSortModel parameters =
                new NewsTask.LatestNewsWithSpecifiedSortModel.Builder()
                        .setLimit(LIMIT).setNewsTaxonomyDocId(100001L)
                        .setSort(CUSTOM_NEWS_SORT).build();
        newsTask.getLatestNewsWithSpecifiedSort(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(CUSTOM_NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(100001L));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testLatestNewsWithSpecifiedSortUsingNoDocId() {
        NewsTask.LatestNewsWithSpecifiedSortModel parameters =
                new NewsTask.LatestNewsWithSpecifiedSortModel.Builder()
                        .setLimit(LIMIT)
                        .setSort(CUSTOM_NEWS_SORT).build();
        newsTask.getLatestNewsWithSpecifiedSort(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(CUSTOM_NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(DEFAULT_NEWS_TAXONOMY_DOC_ID));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testLatestNewsWithSpecifiedSortUsingSingleDocIdAndMultipleDocIds() {
        NewsTask.LatestNewsWithSpecifiedSortModel parameters =
                new NewsTask.LatestNewsWithSpecifiedSortModel.Builder()
                        .setLimit(LIMIT).setNewsTaxonomyDocId(100001L)
                        .setNewsTaxonomyDocIds("100002,100003,100004")
                        .setSort(CUSTOM_NEWS_SORT).build();
        newsTask.getLatestNewsWithSpecifiedSort(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(CUSTOM_NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(100001L));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testLatestNewsWithSpecifiedSortUsingSingleMultipleDocId() {
        NewsTask.LatestNewsWithSpecifiedSortModel parameters =
                new NewsTask.LatestNewsWithSpecifiedSortModel.Builder()
                        .setLimit(LIMIT).setNewsTaxonomyDocIds("100002")
                        .setSort(CUSTOM_NEWS_SORT).build();
        newsTask.getLatestNewsWithSpecifiedSort(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(CUSTOM_NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(100002L));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testLatestNewsWithSpecifiedSortUsingMultipleDocIds() {
        NewsTask.LatestNewsWithSpecifiedSortModel parameters =
                new NewsTask.LatestNewsWithSpecifiedSortModel.Builder()
                        .setLimit(LIMIT).setNewsTaxonomyDocIds("100002,100003,100004")
                        .setSort(CUSTOM_NEWS_SORT).build();
        newsTask.getLatestNewsWithSpecifiedSort(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(CUSTOM_NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(100002L,100003L,100004L));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testGetLatestNewsWithSpecifiedFiltersUsingNoDocId() {
        NewsTask.GetLatestNewsWithSpecifiedFiltersModel parameters =
                new NewsTask.GetLatestNewsWithSpecifiedFiltersModel.Builder()
                        .setLimit(LIMIT)
                        .setSort(CUSTOM_NEWS_SORT)
                        .setFilters(CUSTOM_NEWS_FILTER_QUERIES).build();
        newsTask.getLatestNewsWithSpecifiedFilters(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(CUSTOM_NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(CUSTOM_NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(DEFAULT_NEWS_TAXONOMY_DOC_ID));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testGetLatestNewsWithSpecifiedFiltersUsingSingleDocId() {
        NewsTask.GetLatestNewsWithSpecifiedFiltersModel parameters =
                new NewsTask.GetLatestNewsWithSpecifiedFiltersModel.Builder()
                        .setLimit(LIMIT).setNewsTaxonomyDocId(100001L)
                        .setSort(CUSTOM_NEWS_SORT)
                        .setFilters(CUSTOM_NEWS_FILTER_QUERIES).build();
        newsTask.getLatestNewsWithSpecifiedFilters(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(CUSTOM_NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(CUSTOM_NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(100001L));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testGetLatestNewsWithSpecifiedFiltersUsingSingleDocIdAndMultipleDocIds() {
        NewsTask.GetLatestNewsWithSpecifiedFiltersModel parameters =
                new NewsTask.GetLatestNewsWithSpecifiedFiltersModel.Builder()
                        .setLimit(LIMIT).setNewsTaxonomyDocId(100001L)
                        .setNewsTaxonomyDocIds("100002,100003,100004")
                        .setSort(CUSTOM_NEWS_SORT)
                        .setFilters(CUSTOM_NEWS_FILTER_QUERIES).build();
        newsTask.getLatestNewsWithSpecifiedFilters(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(CUSTOM_NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(CUSTOM_NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(100001L));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testGetLatestNewsWithSpecifiedFiltersUsingSingleMultipleDocId() {
        NewsTask.GetLatestNewsWithSpecifiedFiltersModel parameters =
                new NewsTask.GetLatestNewsWithSpecifiedFiltersModel.Builder()
                        .setLimit(LIMIT)
                        .setNewsTaxonomyDocIds("100002")
                        .setSort(CUSTOM_NEWS_SORT)
                        .setFilters(CUSTOM_NEWS_FILTER_QUERIES).build();
        newsTask.getLatestNewsWithSpecifiedFilters(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(CUSTOM_NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(CUSTOM_NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(100002L));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }

    @Test
    public void testGetLatestNewsWithSpecifiedFiltersUsingMultipleDocIds() {
        NewsTask.GetLatestNewsWithSpecifiedFiltersModel parameters =
                new NewsTask.GetLatestNewsWithSpecifiedFiltersModel.Builder()
                        .setLimit(LIMIT)
                        .setNewsTaxonomyDocIds("100002,100003,100004")
                        .setSort(CUSTOM_NEWS_SORT)
                        .setFilters(CUSTOM_NEWS_FILTER_QUERIES).build();
        newsTask.getLatestNewsWithSpecifiedFilters(parameters);

        DeionSearchService.DeionSearchRequestContext.Builder builder =
                new DeionSearchService.DeionSearchRequestContext.Builder();
        builder.setQuery("*")
                .setSort(CUSTOM_NEWS_SORT)
                .setLimit(LIMIT)
                .setFilterQueries(CUSTOM_NEWS_FILTER_QUERIES)
                .setIncludeDocumentSummaries(true)
                .setTraversalRelationships(Collections.singletonList("primaryParent"))
                .setTraversalStartDocId(List.of(100002L,100003L,100004L));
        Mockito.verify(deionSearchService, Mockito.times(1)).search(builder.build());
    }
}
