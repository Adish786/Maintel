package com.about.mantle.model.services.commerce;

import java.util.List;

import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.model.commerce.CommerceConfiguration;
import com.about.mantle.model.commerce.GenericCommerceModel;
import com.about.mantle.model.extended.docv2.CommerceInfoEx;

/**
 * Commerce API for commerce types that do not need api calls, and thus no processing
 *
 */
public class GenericCommerceApi implements CommerceApi {

	private VendorLookupService vendorLookupService;

	public GenericCommerceApi(VendorLookupService vendorLookupService) {
		this.vendorLookupService = vendorLookupService;
	}

	@Override
	public void gatherAndUpdateCommerceInfo(List<CommerceInfoEx> listOfCommerceInfoEx) {
		gatherAndUpdateCommerceInfo(listOfCommerceInfoEx, null);
	}

	@Override
	public void gatherAndUpdateCommerceInfo(List<CommerceInfoEx> listOfCommerceInfoEx, CommerceConfiguration config) {
		listOfCommerceInfoEx.stream().forEach(
				commerceInfoEx -> commerceInfoEx.setCommerceModel(new GenericCommerceModel(commerceInfoEx.getId(),
						vendorLookupService.getRetailerName(commerceInfoEx.getId()))));
	}

	@Override
	public void gatherAndUpdateRetailers(List<Retailer> listOfRetailers) {
		gatherAndUpdateRetailers(listOfRetailers, null);
	}

	@Override
	public void gatherAndUpdateRetailers(List<Retailer> listOfRetailers, CommerceConfiguration config) {
		listOfRetailers.stream().forEach(
				retailer -> retailer.setCommerceModel(new GenericCommerceModel(retailer.getUrl(),
						vendorLookupService.getRetailerName(retailer.getUrl()))));
		
	}
}
