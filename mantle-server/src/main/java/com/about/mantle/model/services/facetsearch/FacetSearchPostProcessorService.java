package com.about.mantle.model.services.facetsearch;

import java.util.List;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.extended.DeionSearchResultEx.DeionFacet;

/**
 * FacetSearchPostProcessorService
 * 
 * This Service takes a deion search result and provides post processing to go to the frontend.
 */
public interface FacetSearchPostProcessorService {

	public DeionSearchResultEx postProcessDeionResults (List<DeionFacet> listOfFacets, DeionSearchResultEx deionSearchResult);
	
	public DeionSearchResultEx postProcessDeionResults (List<DeionFacet> listOfFacets, DeionSearchResultEx deionSearchResult, 
			Integer maxHeight, Integer maxWidth, Boolean forceSize, String cropSetting, RequestContext requestContext );
}
