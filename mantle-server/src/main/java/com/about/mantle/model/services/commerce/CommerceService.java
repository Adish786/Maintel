package com.about.mantle.model.services.commerce;

import java.util.List;
import java.util.Map;

import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.model.extended.docv2.CommerceInfoEx;

/**
 * Service for handling commerce methods.
 */
public interface CommerceService {

	/*
	 * Gathers data from commerce apis for each CommerceInfo item and adds the returned 
	 * CommerceModel to each commerceInfo
	 */
	public List<List<CommerceInfoEx>> getCommerceModel(List<List<CommerceInfoEx>> listOfListCommerceInfo,
			Map<String, String> config);
	
	/*
	 * Gathers data from commerce apis for each Retailer item and adds the returned 
	 * CommerceModel to each Retailer (InfoCat)
	 */
	public List<List<Retailer>> getCommerceModelForRetailers(List<List<Retailer>> listOfListRetailers,
			Map<String, String> config);

}
