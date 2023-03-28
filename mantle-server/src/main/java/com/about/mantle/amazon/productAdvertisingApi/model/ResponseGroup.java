package com.about.mantle.amazon.productAdvertisingApi.model;

/**
 * Enum for ResponseGroup request parameter of Amazon Product Advertising API
 * Documentation of response groups: http://docs.aws.amazon.com/AWSECommerceService/latest/DG/CHAP_ResponseGroupsList.html
 *  
 */
public enum ResponseGroup {

	ACCESSORIES("Accessories"),
	ALTERNATEVERSIONS("AlternateVersions"),
	BROWSENODEINFO("BrowseNodeInfo"),
	BROWSENODES("BrowseNodes"),
	CART("Cart"),
	CARTNEWRELEASES("CartNewReleases"),
	CARTTOPSELLERS("CartTopSellers"),
	CARTSIMILARITIES("CartSimilarities"),
	EDITORIALREVIEW("EditorialReview"),
	IMAGES("Images"),
	ITEMATTRIBUTES("ItemAttributes"),
	ITEMIDS("ItemIds"),
	LARGE("Large"),
	MEDIUM("Medium"),
	MOSTGIFTED("MostGifted"),
	MOSTWISHEDFOR("MostWishedFor"),
	NEWRELEASES("NewReleases"),
	OFFERFULL("OfferFull"),
	OFFERLISTINGS("OfferListings"),
	OFFERS("Offers"),
	OFFERSUMMARY("OfferSummary"),
	PROMOTIONSUMMARY("PromotionSummary"),
	RELATEDITEMS("RelatedItems"),
	REQUEST("Request"),
	REVIEWS("Reviews"),
	SALESRANK("SalesRank"),
	SEARCHBINS("SearchBins"),
	SIMILARITIES("Similarities"),
	SMALL("Small"),
	TOPSELLERS("TopSellers"),
	TRACKS("Tracks"),
	VARIATIONS("Variations"),
	VARIATIONIMAGES("VariationImages"),
	VARIATIONMATRIX("VariationMatrix"),
	VARIATIONOFFERS("VariationOffers"),
	VARIATIONSUMMARY("VariationSummary"),
    ;
	
	private String responseGroup;
	
	ResponseGroup(String responseGroup){
		this.responseGroup = responseGroup;
	}

	public String getResponseGroup() {
		return responseGroup;
	}

}
