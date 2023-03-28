package com.about.mantle.model.services.facetsearch;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.extended.DeionSearchResultEx.DeionFacet;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext.FacetRangeQuery;

/**
 * Service designed to manage predetermined facets for types of searches. This is meant to be used in conjunction with DeionSearch. 
 * Provides the facet list for starting a deion search and the facet structure to maintain a consistent 
 * UI. 
 */
public interface FacetService {
	
	/**
	 * getSearchFacets 
	 * 
	 * Used for getting the list of facets for a DeionSearch 
	 * @param facetSearchKey - used to select a particular type of faceted search so an application can have many 
	 * @param tagList - List of tags for excluding filters from facets (https://blog.griddynamics.com/multi-select-faceting-for-nested-documents-in-solr/)
	 */
	public List<String> getSearchFacets (String facetSearchKey, Set<String>tagList);
	
	/**
	 * postProcessDeionSearch
	 * 
	 * Used to provide post processing of the DeionSearch for the application
	 * * @param facetSearchKey - used to select a particular type of faceted search so an application can have many
	 * * @param originalResult - original result returned 
	 */
	public DeionSearchResultEx postProcessResults(String facetSearchKey, DeionSearchResultEx originalResult);
	
	/** 
	 * postProcessResultsWithThumborURLHandling
	 * 
	 * Used to provide post processing of the DeionSearch for the application
	 * @param facetSearchKey - used to select a particular type of faceted search so an application can have many
	 * @param originalResult - original result returned 
	 * @param maxHeight - max height of image card
	 * @param maxHeight - max width of image card
	 * @param forceSize - if the size is being forced
	 * @param cropSetting - setting for cropping
	 * @param requestContext 
	 */
	public DeionSearchResultEx postProcessResultsWithThumborURLHandling(String facetSearchKey, DeionSearchResultEx originalResult, Integer maxHeight, Integer maxWidth, Boolean forceSize, String cropSetting, RequestContext requestContext);

	/**
	 * getFilters
	 * 
	 * Gets the necessary filters that are not related to the facets. These would be for example might be what template types to get/exclude, 
	 * what verticals to select, and other items that would be ran everytime for that search. 
	 */
	public List<String> getFilters(String facetSearchKey);
	
	/**
	 * getFiltersWithFacets
	 * 
	 * Gets the necessary filters and merge them with the facets to get the full filter query. 
	 */
	public List<String> getFiltersWithFacets(String facetSearchKey, Map<String, List<String>> searchfacets);
	
	/**
	 * getExpectedFacets
	 * 
	 * @param facetSearchKey
	 * @return returns the expected facets in facet for for a search type
	 */
	public List<DeionFacet> getExpectedFacets(String facetSearchKey);

	/**
	 * getRangeList
	 * 
	 * Returns the range query list setup in the facetSearchConfig for using Deion to give SOLR a range query to group numbers together into buckets. 
	 * See SOLR range documentation for details. 
	 * 
	 * @param facetSearchKey
	 * @return range query list if present, null otherwise
	 */
	List<FacetRangeQuery> getRangeList(String facetSearchKey);
}
