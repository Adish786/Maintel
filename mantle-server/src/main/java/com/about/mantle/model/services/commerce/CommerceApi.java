package com.about.mantle.model.services.commerce;

import java.util.List;

import com.about.mantle.infocat.model.Retailer;
import com.about.mantle.model.commerce.CommerceConfiguration;
import com.about.mantle.model.extended.docv2.CommerceInfoEx;

public interface CommerceApi {

	/**
	 * Takes a list of CommerceInfo and calls the subsequent api to gather and update the commerce info this method
	 * takes advantage of by reference to update the items in the list.
	 * 
	 * @param listOfCommerceInfoEx
	 */
	public void gatherAndUpdateCommerceInfo(List<CommerceInfoEx> listOfCommerceInfoEx);

	/**
	 * Takes a list of CommerceInfo and calls the subsequent api to gather and update the commerce info this method
	 * takes advantage of by reference to update the items in the list. A map of configs can be passed in by the app to
	 * open the services for ab testing.
	 * 
	 * @param listOfCommerceInfoEx
	 * @param config
	 */
	public void gatherAndUpdateCommerceInfo(List<CommerceInfoEx> listOfCommerceInfoEx, CommerceConfiguration config);
	
	/**
	 * Takes a list of Retailer and calls the subsequent api to gather and update the commerce info this method
	 * takes advantage of by reference to update the items in the list.
	 * 
	 * @param listOfCommerceInfoEx
	 */
	public void gatherAndUpdateRetailers(List<Retailer> listOfRetailers);
	
	/**
	 * Takes a list of Retailers and calls the subsequent api to gather and update the commerce info this method
	 * takes advantage of by reference to update the items in the list. A map of configs can be passed in by the app to
	 * open the services for ab testing.
	 * 
	 * @param listOfRetailers
	 * @param config
	 */
	public void gatherAndUpdateRetailers(List<Retailer> listOfRetailers, CommerceConfiguration config);

}
