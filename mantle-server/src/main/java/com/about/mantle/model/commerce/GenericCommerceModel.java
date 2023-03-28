package com.about.mantle.model.commerce;

/**
 * Commerce Model for Skimlinks and Other and should be leveraged for any commerce types that require no processing.
 */
public class GenericCommerceModel extends CommerceModel {

	public GenericCommerceModel(String url, String retailerName) {
		super(url, retailerName);
	}

	public GenericCommerceModel(String url, String retailerName, String imageUrl) {
		super(url, retailerName, imageUrl);
	}

	@Override
	public String getBestPrice() {
		return null;
	}
}
