package com.about.mantle.model.services.facetsearch.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.extended.DeionRangeFacet;
import com.about.mantle.model.extended.DeionSearchResultEx;
import com.about.mantle.model.extended.DeionSearchResultEx.DeionFacet;
import com.about.mantle.model.extended.DeionSearchResultEx.DeionFacetCount;
import com.about.mantle.model.extended.DeionSearchResultItemEx;
import com.about.mantle.model.extended.docv2.ImageEx;
import com.about.mantle.model.extended.docv2.TaggedImage.UsageFlag;
import com.about.mantle.model.services.facetsearch.FacetSearchPostProcessorService;
import com.about.mantle.model.tasks.DeionSearchServiceTask;
import com.about.mantle.render.MantleRenderUtils;
import com.about.mantle.render.image.filter.ImageFilterBuilder;

/**
 * DefaultFacetSearchPostProcessorService
 *
 * Service to handle any post processing of the deion search before sending it along to the application
 * 
 * Meant to be used by the com.about.mantle.model.tasks.DeionFacetSearchTask. {@link DeionSearchServiceTask}
 */
public class DefaultFacetSearchPostProcessorService implements FacetSearchPostProcessorService {

	private final MantleRenderUtils mntlRenderer;
	private final String[] filters;

	public DefaultFacetSearchPostProcessorService(MantleRenderUtils mntlRenderer) {
		this.mntlRenderer = mntlRenderer;
		filters = new String[] { ImageFilterBuilder.noUpscale() };
	}

	@Override
	public DeionSearchResultEx postProcessDeionResults(List<DeionFacet> deionFacets,
			DeionSearchResultEx deionSearchResult) {

		// Build a map of facets for creating a merged FacetList
		Map<String, DeionFacet> mapOfFacets = new HashMap<>();
		deionSearchResult.getFacets().stream().forEach(facet -> mapOfFacets.put(facet.getField(), facet));

		// Creates the merged facet list. This will ensure that order for the app, all facets will be returned
		// consistently
		List<DeionFacet> mergedDeionFacets = deionFacets.stream().map(deionFacet -> {
			if (!mapOfFacets.containsKey(deionFacet.getField())) {
				return createFacet(deionFacet);
			} else {
				return createMergedFacet(deionFacet, mapOfFacets.get(deionFacet.getField()));
			}
		}).collect(Collectors.toList());

		// Build a new search result so that old one doesn't get modified
		DeionSearchResultEx clonedSearchResult = createCloneDeionSearchResult(deionSearchResult);
		clonedSearchResult.setFacets(mergedDeionFacets);
		
		if(deionSearchResult.getRangeFacets() != null){
			List<DeionRangeFacet> clonedRangeFacets = new ArrayList<>();
			for(DeionRangeFacet rangeFacet : deionSearchResult.getRangeFacets()){
				DeionRangeFacet newRangeFacet = new DeionRangeFacet();
				newRangeFacet.setName(rangeFacet.getName());
				
				for(DeionRangeFacet.Count count : rangeFacet.getCounts()){
					newRangeFacet.addCount(count.getValue(), count.getCount());
				}
				
				clonedRangeFacets.add(newRangeFacet);
			}
			
			clonedSearchResult.setRangeFacets(clonedRangeFacets);
		}

		return clonedSearchResult;
	}

	@Override
	public DeionSearchResultEx postProcessDeionResults(List<DeionFacet> deionFacets,
			DeionSearchResultEx deionSearchResult, Integer maxHeight, Integer maxWidth, Boolean forceSize,
			String cropSetting, RequestContext requestContext) {

		DeionSearchResultEx clonedSearchResult = postProcessDeionResults(deionFacets, deionSearchResult);
		attachImageUrlToDocuments(clonedSearchResult, maxHeight, maxWidth, forceSize, cropSetting, requestContext);
		return clonedSearchResult;
	}

	// Used to break by reference nature of lists for the expected facet in the areas being modified
	protected DeionFacet createFacet(DeionFacet expected) {
		DeionFacet deionFacet = new DeionFacet();
		deionFacet.setField(expected.getField());
		if (expected.getDisplayName() != null) {
			deionFacet.setDisplayName(expected.getDisplayName());
		} else {
			// Done so that the app only needs to look for display name instead of doing a null check
			deionFacet.setDisplayName(expected.getField());
		}

		return deionFacet;
	}

	// Used to break by reference nature of lists for both the app expected facet and the results
	protected DeionFacet createMergedFacet(DeionFacet expected, DeionFacet result) {

		// build map of counts
		Map<String, DeionFacetCount> counts = new HashMap<>();
		expected.getCounts().stream().forEach(facetCount -> counts.put(facetCount.getValue(), facetCount));
		
		return createFacet(result, expected.getDisplayName(), counts);
	}

	// Used to break by reference nature of lists for both the app expected facet in the areas being modified
	protected DeionFacet createFacet(DeionFacet result, String displayName,
			Map<String, DeionFacetCount> expectedCounts) {
		DeionFacet deionFacet = new DeionFacet();
		deionFacet.setField(result.getField());
		deionFacet.setDisplayName(displayName);

		List<DeionFacetCount> clonedAndMergedCounts = new ArrayList<>();
		for (DeionFacetCount resultCount : result.getCounts()) {
			DeionFacetCount newCount = new DeionFacetCount();
			newCount.setCount(resultCount.getCount());
			newCount.setValue(resultCount.getValue());
			if (expectedCounts != null && expectedCounts.containsKey(resultCount.getValue())) {
				newCount.setDisplayName(expectedCounts.get(resultCount.getValue()).getDisplayName());
			} else {
				// Done so that the app only needs to look for display name instead of doing a null check
				newCount.setDisplayName(resultCount.getValue());
			}
			clonedAndMergedCounts.add(newCount);
		}
		deionFacet.setCounts(clonedAndMergedCounts);
		
		return deionFacet;
	}

	// Used to break by reference DeionSearchResultEx to make sure it is cache safe in the areas being modified
	protected DeionSearchResultEx createCloneDeionSearchResult(DeionSearchResultEx deionSearchResult) {
		DeionSearchResultEx deionSearchResultClone = new DeionSearchResultEx(deionSearchResult);
		deionSearchResultClone.setNumFound(deionSearchResult.getNumFound());
		deionSearchResultClone.setItems(deionSearchResult.getItems());
		return deionSearchResultClone;
	}

	protected void attachImageUrlToDocuments(DeionSearchResultEx deionSearchResult, Integer maxHeight, Integer maxWidth,
			Boolean forceSize, String cropSetting, RequestContext requestContext) {

		for (DeionSearchResultItemEx item : deionSearchResult.getItems()) {

			if (item.getDocument() != null && item.getDocument().getImageForUsage(UsageFlag.PRIMARY) != null
					&& item.getDocument().getImageForUsage(UsageFlag.PRIMARY).getObjectId() != null) {

				String thumborUrl = createImageUrl(item.getDocument().getImageForUsage(UsageFlag.PRIMARY),
						maxWidth, maxHeight, forceSize,	cropSetting, requestContext);

				item.getDocument().getImageForUsage(UsageFlag.PRIMARY).setUrl(thumborUrl);
			}
		}

	}

	protected String createImageUrl(ImageEx image, int maxWidth, int maxHeight, Boolean forceSize,
									String cropSetting, RequestContext requestContext) {

		if (mntlRenderer == null)
			throw new GlobeException("Mantle Renderer not set in DefaultFacetSearchPostProcessor");

		return mntlRenderer.getThumborUrl(image, maxWidth, maxHeight, "", forceSize, requestContext,
				cropSetting, filters);
	}
}
