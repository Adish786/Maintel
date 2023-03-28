package com.about.mantle.model.services.commerce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.model.commerce.CommerceConfiguration;
import com.about.mantle.model.commerce.GenericCommerceModel;
import com.about.mantle.model.commerce.SkimlinksCommerceModel;
import com.about.mantle.model.extended.docv2.CommerceInfoEx;
import com.about.mantle.skimlinks.pricingApi.services.SkimlinksPricingApiFacade;

public class SkimlinksCommerceApi implements CommerceApi {

	private final SkimlinksPricingApiFacade skimlinksService;
	private VendorLookupService vendorLookupService;

	public SkimlinksCommerceApi(SkimlinksPricingApiFacade skimlinksService, VendorLookupService vendorLookupService) {
		this.skimlinksService = skimlinksService;
		this.vendorLookupService = vendorLookupService;
	}

	@Override
	public void gatherAndUpdateCommerceInfo(List<CommerceInfoEx> listOfCommerceInfoEx, CommerceConfiguration config) {

		Map<String, List<CommerceInfoEx>> mapOfUrlsToCommerceInfoEx = new HashMap<>();

		for (CommerceInfoEx commerce : listOfCommerceInfoEx) {

			String url = commerce.getId();
			if (!mapOfUrlsToCommerceInfoEx.containsKey(url)) {
				mapOfUrlsToCommerceInfoEx.put(url, new ArrayList<>());
			}

			mapOfUrlsToCommerceInfoEx.get(url).add(commerce);
		}

		Map<String, SkimlinksCommerceModel> skimlinksResults = 
				mapOfUrlsToCommerceInfoEx.isEmpty() || skimlinksService == null ? Collections.emptyMap() : skimlinksService
				.lookupItems(mapOfUrlsToCommerceInfoEx.keySet());

		for (String url : mapOfUrlsToCommerceInfoEx.keySet()) {
			
			List<CommerceInfoEx> commerceInfoList = mapOfUrlsToCommerceInfoEx.get(url);
			String retailerName = vendorLookupService.getRetailerName(url);

			commerceInfoList.forEach(commerceInfo -> {
				SkimlinksCommerceModel returnedModel = skimlinksResults.get(url);
				if (returnedModel != null && "success".equals(returnedModel.getStatus())) { //only return result if response is success, not in case of failure/pending
					returnedModel.setRetailerName(retailerName);
					commerceInfo.setCommerceModel(skimlinksResults.get(url));
				} else { // handles the case where the api doesn't find it
					commerceInfo.setCommerceModel(new GenericCommerceModel(commerceInfo.getId(), retailerName));
				}
			});
		}
	}

	@Override
	public void gatherAndUpdateCommerceInfo(List<CommerceInfoEx> listOfCommerceInfoEx) {
		gatherAndUpdateCommerceInfo(listOfCommerceInfoEx, null);

	}

	@Override
	public void gatherAndUpdateRetailers(List<Retailer> listOfRetailers) {
		gatherAndUpdateRetailers(listOfRetailers, null);
		
	}

	@Override
	public void gatherAndUpdateRetailers(List<Retailer> listOfRetailers, CommerceConfiguration config) {
		Map<String, List<Retailer>> mapOfUrlsToRetailers = new HashMap<>();

		for (Retailer retailer : listOfRetailers) {

			String url = retailer.getUrl();
			if (!mapOfUrlsToRetailers.containsKey(url)) {
				mapOfUrlsToRetailers.put(url, new ArrayList<>());
			}

			mapOfUrlsToRetailers.get(url).add(retailer);
		}

		Map<String, SkimlinksCommerceModel> skimlinksResults = 
				mapOfUrlsToRetailers.isEmpty() || skimlinksService == null ? Collections.emptyMap() : skimlinksService
				.lookupItems(mapOfUrlsToRetailers.keySet());

		for (String url : mapOfUrlsToRetailers.keySet()) {
			
			List<Retailer> retailerList = mapOfUrlsToRetailers.get(url);
			String retailerName = vendorLookupService.getRetailerName(url);

			retailerList.forEach(retailer -> {
				SkimlinksCommerceModel returnedModel = skimlinksResults.get(url);
				if (returnedModel != null && "success".equals(returnedModel.getStatus())) { //only return result if response is success, not in case of failure/pending
					returnedModel.setRetailerName(retailerName);
					retailer.setCommerceModel(skimlinksResults.get(url));
				} else { // handles the case where the api doesn't find it
					retailer.setCommerceModel(new GenericCommerceModel(retailer.getUrl(), retailerName));
				}
			});
		}
		
	}

}
