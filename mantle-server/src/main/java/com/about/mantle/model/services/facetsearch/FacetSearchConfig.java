package com.about.mantle.model.services.facetsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.about.mantle.model.tasks.DeionFacetSearchTask;
import org.apache.commons.lang3.StringUtils;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.model.extended.DeionSearchResultEx.DeionFacet;
import com.about.mantle.model.extended.DeionSearchResultEx.DeionFacetCount;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext.FacetRangeQuery;

/**
 * FacetSearchConfig
 * 
 * Used to manage the list of items associated with a FacetSearchConfiguration to avoid passing around series of maps.
 * {@link DeionFacetSearchTask}
 */
public class FacetSearchConfig {

	private final List<DeionFacet> deionFacetList;
	private final List<String> facetsSentToDeion;
	private final List<String> filters;
	private final List<FacetRangeQuery> listOfFacetRangeQuery; 

	/**
	 * FacetSearchConfig
	 * 
	 * Used to configure a search type. This constructor is for a search type that relies on the default processors,
	 * fields, and filters
	 * 
	 * @param deionFacetList
	 *            - list of deionFacets that you want to guarantee come back to the app
	 */
	public FacetSearchConfig(List<DeionFacet> deionFacetList) {
		this(deionFacetList, null);
	}

	/**
	 * FacetSearchConfig
	 * 
	 * Generates a facet list via config string passed in instead of getting the facet list passed in directly Facets
	 * are separated by semicolons (;) The name in selene can be followed by an optional period (.) with text after that
	 * for display purposes A comma (,) after the facetName is used to space out the FacetCounts. Like the Facet, the
	 * FacetCount can follow the format seleneName.displayName
	 * 
	 * @param deionFacetListInStringForm
	 *    format: NameInSeleneOfFacet.OptionalDisplayName,optionalCountNameOfPreviousFacet.OptionalDisplayName;NameOfFacet2,CountFacet1,CountFacet2.displayNameOfCountFacet2;NameOfFacet3
	 */
	public FacetSearchConfig(String deionFacetListInStringForm) {
		this(deionFacetListInStringForm, null);
	}

	/**
	 * FacetSearchConfig
	 * 
	 * Generates a facet list via config string passed in instead of getting the facet list passed in directly Facets
	 * are separated by semicolons (;) The name in selene can be followed by an optional period (.) with text after that
	 * for display purposes A comma (,) after the facetName is used to space out the FacetCounts. Like the Facet, the
	 * FacetCount can follow the format seleneName.displayName
	 * 
	 * @param deionFacetListInStringForm
	 *     format: NameInSeleneOfFacet.OptionalDisplayName,optionalCountNameOfPreviousFacet.OptionalDisplayName;NameOfFacet2,CountFacet1,CountFacet2.displayNameOfCountFacet2;NameOfFacet3
	 * @param facetSearchPostProcessorService
	 *            - The post processor that handles merging the results from deion with with deionFacetList
	 * @param filters
	 *            - list of filters that you wish to use (such as excluding certain template types, including certain
	 *            verticals, ect)
	 */
	public FacetSearchConfig(String deionFacetListInStringForm, List<String> filters) {
		this(deionFacetListInStringForm, filters, null);
	}

	/**
	 * FacetSearchConfig
	 * 
	 * Used to configure a search type.
	 * 
	 * @param deionFacetList
	 *            - list of deionFacets that you want to guarantee come back to the app
	 * @param facetSearchPostProcessorService
	 *            - The post processor that handles merging the results from deion with with deionFacetList
	 * @param filters
	 *            - list of filters that you wish to use (such as excluding certain template types, including certain
	 *            verticals, ect)
	 */
	public FacetSearchConfig(List<DeionFacet> deionFacetList, List<String> filters) {
		this(deionFacetList, filters, null);
	}
	
	
	/**
	 * FacetSearchConfig
	 * 
	 * Generates a facet list via config string passed in instead of getting the facet list passed in directly Facets
	 * are separated by semicolons (;) The name in selene can be followed by an optional period (.) with text after that
	 * for display purposes A comma (,) after the facetName is used to space out the FacetCounts. Like the Facet, the
	 * FacetCount can follow the format seleneName.displayName
	 * 
	 * @param deionFacetListInStringForm
	 *     format: NameInSeleneOfFacet.OptionalDisplayName,optionalCountNameOfPreviousFacet.OptionalDisplayName;NameOfFacet2,CountFacet1,CountFacet2.displayNameOfCountFacet2;NameOfFacet3
	 * @param facetSearchPostProcessorService
	 *            - The post processor that handles merging the results from deion with with deionFacetList
	 * @param filters
	 *            - list of filters that you wish to use (such as excluding certain template types, including certain
	 *            verticals, ect)
	 * @param listOfFacetRangeQuery
	 *            - list of range queries that are used to allow Deion to take advantage of solr's abilities to create range facets    
	 */
	public FacetSearchConfig(String deionFacetListInStringForm, List<String> filters, List<FacetRangeQuery> listOfFacetRangeQuery) {
		this(buildFacetList(deionFacetListInStringForm), filters, listOfFacetRangeQuery);
	}

	/**
	 * FacetSearchConfig
	 * 
	 * Used to configure a search type.
	 * 
	 * @param deionFacetList
	 *            - list of deionFacets that you want to guarantee come back to the app
	 * @param facetSearchPostProcessorService
	 *            - The post processor that handles merging the results from deion with with deionFacetList
	 * @param filters
	 *            - list of filters that you wish to use (such as excluding certain template types, including certain
	 *            verticals, ect)
	 * @param listOfFacetRangeQuery
	 *            - list of range queries that are used to allow Deion to take advantage of solr's abilities to create range facets       
	 */
	public FacetSearchConfig(List<DeionFacet> deionFacetList, List<String> filters, List<FacetRangeQuery> listOfFacetRangeQuery) {
		this.deionFacetList = deionFacetList;
		this.facetsSentToDeion = buildFacetListForDeionSearch(deionFacetList);
		this.filters = filters;
		this.listOfFacetRangeQuery = listOfFacetRangeQuery;
	}

	/**
	 * buildFacetListForDeionSearch
	 * 
	 * Builds the list of facets for a search type to send to the DeionSearch service.
	 * 
	 * @param facetLists
	 */
	private static List<String> buildFacetListForDeionSearch(List<DeionFacet> facetList) {
		List<String> listForDeionSearch = facetList.stream().map(s -> s.getField()).collect(Collectors.toList());
		return listForDeionSearch;
	}

	/**
	 * All the facets expected to be returned to the application
	 */
	public List<DeionFacet> getDeionFacetList() {
		return deionFacetList;
	}

	/**
	 * Facets groups are sent to deion in a list format
	 */
	public List<String> getFacetsSentToDeion() {
		return facetsSentToDeion;
	}

	public List<String> getFilters() {
		return filters;
	}

	private static List<DeionFacet> buildFacetList(String buildString) {
		String[] strings = StringUtils.split(buildString, ";");
		List<DeionFacet> listOfFacets = new ArrayList<>();

		for (String s : strings) {
			listOfFacets.add(buildFacet(s));
		}

		return listOfFacets;
	}

	private static DeionFacet buildFacet(String buildString) {
		String[] strings = StringUtils.split(buildString, ",");
		DeionFacet deionFacet = new DeionFacet();
		List<DeionFacetCount> deionFacetCountList = new ArrayList<>();

		if (strings.length > 0) {
			String[] facet = StringUtils.split(strings[0], ".");

			if (facet.length > 0) deionFacet.setField(facet[0]);

			if (facet.length > 1) deionFacet.setDisplayName(facet[1]);

			if (facet.length == 0) throw new GlobeException("Misconfigured Facet Configuration: " + buildString);

			for (int i = 1; i < strings.length; i++) {
				String[] facetCountParts = StringUtils.split(strings[i], ".");
				DeionFacetCount facetCount = new DeionFacetCount();

				if (facetCountParts.length > 1) facetCount.setDisplayName(facetCountParts[1]);
				
				if (facetCountParts.length > 0){
					facetCount.setValue(facetCountParts[0]);
					deionFacetCountList.add(facetCount);
				}
			}
		} else {
			throw new GlobeException("Misconfigured Facet Configuration: " + buildString);
		}

		deionFacet.setCounts(deionFacetCountList);
		return deionFacet;
	}

	public List<FacetRangeQuery> getListOfFacetRangeQuery() {
		return listOfFacetRangeQuery;
	}
}