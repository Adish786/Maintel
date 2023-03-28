package com.about.mantle.model.tasks;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.extended.DeionSearchResultEx.DeionFacet;
import com.about.mantle.model.services.DeionSearchService;
import com.about.mantle.model.services.DeionSearchService.DeionSearchRequestContext;
import com.about.mantle.model.services.facetsearch.FacetSearchRequest;
import com.about.mantle.model.services.facetsearch.FacetService;
import com.about.mantle.utils.MantleSolrClientUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DeionFacetSearchTask
 *
 * Taske used to perform facet searches with the deion service. 
 * 
 */
@Tasks
public class DeionFacetSearchTask {
	protected final DeionSearchService deionSearchService;
	protected final FacetService facetService;
	private final ObjectMapper mapper = new StandardObjectMapperProvider().getContext(null);
	
	/**
	 * DeionFacetSearchTask 
	 * 
	 * Takes a deionSearchService and facetService. deionSearchService to perform the calls to selene
	 * while the facetService handles the facet search configuration details.
	 * 
	 * @param deionSearchService
	 * @param facetService
	 */
	public DeionFacetSearchTask(DeionSearchService deionSearchService, FacetService facetService) {
		this.deionSearchService = deionSearchService;
		this.facetService = facetService;
	}
	
	/**
	 * getFacets
	 *
	 * Returns a list of facets for a particular facetSearch
	 * @param facetSearchKey
	 */
	@Task(name = "getFacets")
	public List<DeionFacet>  getFacetsForAfacetSearchKey(@TaskParameter(name = "facetSearchKey") String facetSearchKey) {
		
		return facetService.getExpectedFacets(facetSearchKey);
	}
	
	/**
	 * facetedSearchWithoutFiltering
	 *
	 * Performs a standard deion search with facets - but does not take in pivots.  
	 *
	 * @param query - term being searched
	 * @param offset - how many documents from zero to start at returning (used in pagination)
	 * @param limit - number of documents returned
	 * @param facetSearchKey - facet search being used
	 */
	@Task(name = "facetedSearch")
	public DeionSearchResultEx facetedSearchWithoutFilteringOnFacets(@TaskParameter(name = "query") String query, 
			@TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit, @TaskParameter(name = "facetSearchKey") String facetSearchKey) {
		
		return facetedSearch(query, facetService.getFilters(facetSearchKey), null, offset, limit,facetSearchKey, null);
	}
	

	/**
	 * facetedSearchWithoutFiltering
	 *
	 * Performs a standard deion search with facets - but does not take in pivots.  
	 * 
	 * @param query - term being searched
	 * @param sort - the selene parameter for sorting
	 * @param offset - how many documents from zero to start at returning (used in pagination)
	 * @param limit - number of documents returned
	 * @param facetSearchKey - facet search being used
	 */
	@Task(name = "facetedSearch")
	public DeionSearchResultEx facetedSearchWithoutFilteringOnFacets(@TaskParameter(name = "query") String query, @TaskParameter(name = "sort") String sort,
			@TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit, @TaskParameter(name = "facetSearchKey") String facetSearchKey) {
		
		return facetedSearch(query, facetService.getFilters(facetSearchKey), sort, offset, limit,facetSearchKey, null);
	}
	

	/**
	 * facetedSearch
	 *
	 * Receives a request from a post method (needs to be wired in an instantiating vertical's action.xml file to work)
	 */
	@Task(name = "facetedSearchPost")
	public void facetedSearch (HttpServletRequest request, HttpServletResponse response, @RequestContextTaskParameter RequestContext requestContext) throws IOException, URISyntaxException {
		
		FacetSearchRequest facetSearchRequest = mapper.readValue(request.getReader(), FacetSearchRequest.class);
		response.setContentType(MediaType.APPLICATION_JSON);
		List<String> filters = facetService.getFiltersWithFacets(facetSearchRequest.getFacetSearchKey(), facetSearchRequest.getPivots());
		Set<String> filterTags = facetSearchRequest.getPivots() != null? facetSearchRequest.getPivots().keySet() : null;
				
		DeionSearchResultEx searchResult = facetedSearch(facetSearchRequest.getQuery(), filters, facetSearchRequest.getSort(),facetSearchRequest.getOffset(),
				facetSearchRequest.getLimit(),facetSearchRequest.getFacetSearchKey(), facetSearchRequest.getMaxHeight(), facetSearchRequest.getMaxWidth(),
				facetSearchRequest.getForceSize(), facetSearchRequest.getCropSetting(), filterTags, requestContext);
		mapper.writeValue(response.getWriter(), searchResult); 
	}
	
	
	/**
	 * facetedSearch
	 * 
	 * Performs faceted search
	 * 
	 * @param query - term being searched
	 * @param sort - the selene parameter for sorting
	 * @param pivots - Map of a list, where the key is the facet being searched, and the list associated
	 * with the key are the counts being selected. 
	 * @param offset - how many documents from zero to start at returning (used in pagination)
	 * @param limit - number of documents returned
	 * @param facetSearchKey - facet search being used
	 */
	@Task(name = "facetedSearch")
	public DeionSearchResultEx facetedSearch(@TaskParameter(name = "query") String query,
			@TaskParameter(name = "pivots") Map<String, List<String>> mapOfPivots, @TaskParameter(name = "sort") String sort,
			@TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit, @TaskParameter(name = "facetSearchKey") String facetSearchKey) {
		
		Set<String> filterTags = mapOfPivots != null ? mapOfPivots.keySet() : null;
		List<String> filters = facetService.getFiltersWithFacets(facetSearchKey, mapOfPivots);
		return facetedSearch(query, filters, sort, offset, limit,facetSearchKey, filterTags);
	
	}
	
	/**
	 * facetedSearch
	 * 
	 * Performs faceted search
	 * 
	 * @param query - term being searched
	 * @param filters - filters passed from the app instead of utilizing the facet service for them.
	 * @param sort - the selene parameter for sorting
	 * @param pivots - Map of a list, where the key is the facet being searched, and the list associated
	 * with the key are the counts being selected. 
	 * @param offset - how many documents from zero to start at returning (used in pagination)
	 * @param limit - number of documents returned
	 * @param facetSearchKey - facet search being used
	 * @param tagList - List of tags for excluding filters from facets (https://blog.griddynamics.com/multi-select-faceting-for-nested-documents-in-solr/)
	 */
	@Task(name = "facetedSearch")
	public DeionSearchResultEx facetedSearch(@TaskParameter(name = "query") String query,
			@TaskParameter(name = "filters") List<String> filters, @TaskParameter(name = "sort") String sort,
			@TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit, @TaskParameter(name = "facetSearchKey") String facetSearchKey, @TaskParameter(name = "tagList") Set<String> tagList) {
		
		DeionSearchResultEx deionSearch = performDeionSearch (query, filters, sort, offset, limit, facetSearchKey, tagList);
		
		//Performs Post Facet Search processing 
		return facetService.postProcessResults(facetSearchKey, deionSearch);
	}
	
	/**
	 * facetedSearch
	 * 
	 * Performs faceted search
	 * 
	 * @param query - term being searched
	 * @param filters - filters passed from the app instead of utilizing the facet service for them.
	 * @param sort - the selene parameter for sorting
	 * @param pivots - Map of a list, where the key is the facet being searched, and the list associated
	 * with the key are the counts being selected. 
	 * @param offset - how many documents from zero to start at returning (used in pagination)
	 * @param limit - number of documents returned
	 * @param facetSearchKey - facet search being used
	 * @param tagList - List of tags for excluding filters from facets (https://blog.griddynamics.com/multi-select-faceting-for-nested-documents-in-solr/)
	 */
	@Task(name = "facetedSearch")
	public DeionSearchResultEx facetedSearch(@TaskParameter(name = "query") String query,
			@TaskParameter(name = "filters") List<String> filters, @TaskParameter(name = "sort") String sort,
			@TaskParameter(name = "offset") Integer offset, @TaskParameter(name = "limit") Integer limit, @TaskParameter(name = "facetSearchKey") String facetSearchKey, 
			@TaskParameter(name = "maxHeight") Integer maxHeight, @TaskParameter(name = "maxWidth") Integer maxWidth, @TaskParameter(name = "forceSize") Boolean forceSize,
			@TaskParameter(name = "cropSetting") String cropSetting, @TaskParameter(name = "tagList") Set<String> tagList, @RequestContextTaskParameter RequestContext requestContext) {
		
		DeionSearchResultEx deionSearch = performDeionSearch (query, filters, sort, offset, limit, facetSearchKey, tagList);
		
		//Performs Post Facet Search processing  
		return facetService.postProcessResultsWithThumborURLHandling(facetSearchKey, deionSearch, maxHeight, maxWidth, forceSize, cropSetting, requestContext);
	}
	
	protected DeionSearchResultEx performDeionSearch (String query, List<String> filters, String sort, Integer offset, Integer limit, String facetSearchKey, Set<String> tagList) {
		
		//Builds and gets Deion Search
		DeionSearchRequestContext.Builder builder = new DeionSearchRequestContext.Builder();
		builder.setQuery(MantleSolrClientUtils.escapeQueryChars(query)).setFilterQueries(filters)
				.setFacetFields(facetService.getSearchFacets(facetSearchKey, tagList)).setSort(sort)
				.setOffset(offset).setLimit(limit).setIncludeDocumentSummaries(true)
				.setFacetRange(facetService.getRangeList(facetSearchKey)).build();
		DeionSearchResultEx deionSearch = deionSearchService.search(builder.build());
				
		return deionSearch;
	}
}