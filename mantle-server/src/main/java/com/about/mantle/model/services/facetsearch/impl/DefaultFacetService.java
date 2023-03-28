package com.about.mantle.model.services.facetsearch.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.extended.DeionSearchResultEx.DeionFacet;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext.FacetRangeQuery;
import com.about.mantle.model.services.facetsearch.FacetSearchConfig;
import com.about.mantle.model.services.facetsearch.FacetSearchPostProcessorService;
import com.about.mantle.model.services.facetsearch.FacetService;

/**
 * DefaultFacetService
 *
 * Service to handle the injection of application specified configuration for the faceted search functionality. Designed
 * to handle most cases to allow an application to configure multiple faceted searches and handle a majority of use
 * cases. Meant to be used by the com.about.mantle.model.tasks.DeionSearchSeviceTask.
 */
public class DefaultFacetService implements FacetService {
	private Map<String, FacetSearchConfig> mapOfFacetSearchConfigurations;

	private final List<String> DEFAULT_FILTERS;
	private final FacetSearchPostProcessorService globalFacetSearchPostProcessorService;
	private final Map<String, FacetSearchPostProcessorService> facetSearchPostProcessorServiceMap;
	protected final String QUOTATION_MARK = "\"";

	/**
	 * DefaultFacetService
	 * 
	 * In the case that the user doesn't wish to specify additional or reduced number of fields for the documents
	 * returned by facet search will use the default for all.
	 * 
	 * @param mapOfFacetSearchConfigurations
	 *            - lists of facets for the UI
	 */
	public DefaultFacetService(Map<String, FacetSearchConfig> mapOfFacetSearchConfigurations) {
		this(mapOfFacetSearchConfigurations, null, null, null);
	}

	/**
	 * DefaultFacetService
	 * 
	 * In the case that the user doesn't wish to specify additional or reduced number of fields for the documents
	 * returned by facet search will use the default for all.
	 * 
	 * @param facetLists
	 *            - lists of facets for the UI
	 * @param facetSearchPostProcessorServiceMap
	 *            - map of injectable facetSearchPostProcessorServices that may contain specialized post processors for
	 *            different searches.
	 */
	public DefaultFacetService(Map<String, FacetSearchConfig> mapOfFacetSearchConfigurations,
			Map<String, FacetSearchPostProcessorService> facetSearchPostProcessorServiceMap) {
		this(mapOfFacetSearchConfigurations, null, null, facetSearchPostProcessorServiceMap);
	}

	/**
	 * DefaultFacetService
	 * 
	 * In the case that the user doesn't wish to specify additional or reduced number of fields for the documents
	 * returned by facet search will use the default for all. Allows for customizing the default fields in the case that
	 * many searches will use the same field. Additionally takes a map of Facet Search Key to fields for fine grain
	 * control of the document results returned for different faceted searches.
	 * 
	 * @param facetLists
	 *            - lists of facets for the UI
	 * @param globalFacetSearchPostProcessorService
	 *            - A default processor that will be ran for any facetSearchKey that does not have a default specified
	 * @param defaultFilters
	 *            - list of filters that you wish to use (such as excluding certain template types, including certain
	 *            verticals, ect)
	 * @param facetSearchPostProcessorServiceMap
	 *            - map of injectable facetSearchPostProcessorServices that may contain specialized post processors for
	 *            different searches.
	 */
	public DefaultFacetService(Map<String, FacetSearchConfig> mapOfFacetSearchConfigurations,
			FacetSearchPostProcessorService globalFacetSearchPostProcessorService, List<String> defaultFilters,
			Map<String, FacetSearchPostProcessorService> facetSearchPostProcessorServiceMap) {
		this.mapOfFacetSearchConfigurations = mapOfFacetSearchConfigurations;
		this.globalFacetSearchPostProcessorService = globalFacetSearchPostProcessorService;
		DEFAULT_FILTERS = defaultFilters;
		this.facetSearchPostProcessorServiceMap = facetSearchPostProcessorServiceMap;
	}
	
	/**
	 * getRangeList
	 * 
	 * Returns the range query list setup in the facetSearchConfig for using Deion to give SOLR a range query to group numbers together into buckets. 
	 * See SOLR range documentation for details. 
	 * 
	 * @param facetSearchKey
	 * @return range query list if present, null otherwise
	 */
	@Override
	public List<FacetRangeQuery> getRangeList(String facetSearchKey){
		verifyFacetSearchKey(facetSearchKey);
		return mapOfFacetSearchConfigurations.get(facetSearchKey).getListOfFacetRangeQuery();
	}

	@Override
	public List<String> getSearchFacets(String facetSearchKey, Set<String> tagList) {
		verifyFacetSearchKey(facetSearchKey);

		List<String> facets = new ArrayList<>();
		for (String s : mapOfFacetSearchConfigurations.get(facetSearchKey).getFacetsSentToDeion()) {
			StringBuilder sb;

			// Used to handle excluding filtering of facetgroup from faceted results
			// https://blog.griddynamics.com/multi-select-faceting-for-nested-documents-in-solr/
			if (tagList != null && tagList.contains(s)) {
				sb = new StringBuilder("%7B!ex=");
				sb.append(s).append("%7D").append(s);
			} else {
				sb = new StringBuilder(s);
			}
			facets.add(sb.toString());
		}
		return facets;
	}

	@Override
	public List<String> getFilters(String facetSearchKey) {
		verifyFacetSearchKey(facetSearchKey);
		List<String> filters = mapOfFacetSearchConfigurations.get(facetSearchKey).getFilters();
		if (filters == null) filters = DEFAULT_FILTERS;
		return filters;
	}

	@Override
	public List<String> getFiltersWithFacets(String facetSearchKey, Map<String, List<String>> searchfacets) {
		List<String> filters = getFilters(facetSearchKey);
		List<String> listOfFacets = flattenedFacetMap(searchfacets);
		listOfFacets.addAll(filters);
		return listOfFacets;
	}

	@Override
	public DeionSearchResultEx postProcessResults(String facetSearchKey, DeionSearchResultEx originalResult) {

		FacetSearchPostProcessorService facetSearchPostProcessorService = getPostProcessor(facetSearchKey);

		// If no processors are set assume no post processing and return the original result
		if (facetSearchPostProcessorService == null) return originalResult;

		return facetSearchPostProcessorService.postProcessDeionResults(
				mapOfFacetSearchConfigurations.get(facetSearchKey).getDeionFacetList(), originalResult);
	}

	@Override
	public DeionSearchResultEx postProcessResultsWithThumborURLHandling(String facetSearchKey,
			DeionSearchResultEx originalResult, Integer maxHeight, Integer maxWidth, Boolean forceSize,
			String cropSetting, RequestContext requestContext) {
		FacetSearchPostProcessorService facetSearchPostProcessorService = getPostProcessor(facetSearchKey);

		// If no processors are set assume no post processing and return the original result
		if (facetSearchPostProcessorService == null) return originalResult;

		return facetSearchPostProcessorService.postProcessDeionResults(
				mapOfFacetSearchConfigurations.get(facetSearchKey).getDeionFacetList(), originalResult, maxHeight,
				maxWidth, forceSize, cropSetting, requestContext);
	}

	private FacetSearchPostProcessorService getPostProcessor(String facetSearchKey) {
		verifyFacetSearchKey(facetSearchKey);

		FacetSearchPostProcessorService facetSearchPostProcessorService = facetSearchPostProcessorServiceMap != null
				&& facetSearchPostProcessorServiceMap.containsKey(facetSearchKey)
						? facetSearchPostProcessorServiceMap.get(facetSearchKey) : null;

		// Check to see if there's a specialized post processor
		if (facetSearchPostProcessorService == null)
			facetSearchPostProcessorService = globalFacetSearchPostProcessorService;

		return facetSearchPostProcessorService;
	}

	@Override
	public List<DeionFacet> getExpectedFacets(String facetSearchKey) {
		verifyFacetSearchKey(facetSearchKey);

		return mapOfFacetSearchConfigurations.get(facetSearchKey).getDeionFacetList();
	}

	private void verifyFacetSearchKey(String facetSearchKey) {
		if (mapOfFacetSearchConfigurations == null || !mapOfFacetSearchConfigurations.containsKey(facetSearchKey))
			throw new GlobeException("Facet Search Key: " + facetSearchKey + " not found");
	}

	protected List<String> flattenedFacetMap(Map<String, List<String>> searchFacets) {

		if (searchFacets == null) {
			return new ArrayList<String>();
		}

		return searchFacets.entrySet().stream().map(facet -> flattenedFacetList(facet.getKey(), facet.getValue()))
				.collect(Collectors.toList());
	}

	protected String flattenedFacetList(String facetField, List<String> choicesWithinFacetField) {

		// Tagging the filter query so we can later exclude it in the facet queries
		// https://blog.griddynamics.com/multi-select-faceting-for-nested-documents-in-solr/
		StringBuilder sb = new StringBuilder("%7B!tag=");
		sb.append(facetField);
		sb.append("%7D"); // End building the tag

		sb.append(facetField);
		sb.append(":(");
		for (int i = 0; i < choicesWithinFacetField.size(); i++) {
			if (i != 0) {
				sb.append(" OR ");
			}

			// Used to handle ranges in the list of facets being filtered.
			// IE starRating_score:([1 TO 2] OR [2 TO 4]) is not wrapped in quotes
			boolean notARange = !choicesWithinFacetField.get(i).contains("[");
			if (notARange) sb.append(QUOTATION_MARK);

			sb.append(choicesWithinFacetField.get(i));

			if (notARange) sb.append(QUOTATION_MARK);
		}
		sb.append(")");

		return sb.toString();
	}

}
