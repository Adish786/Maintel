package com.about.mantle.model.commerce;

import java.io.Serializable;

/**
 * The base data expected to be returned from the commerce apis
 */
public abstract class CommerceModel implements Serializable {

	// Inc this number for any non-backwards-compatible (ie non-additive) interface changes.
	private static final long serialVersionUID = 1L;

	protected String imageUrl;
	protected String url;
	protected String retailerName; // name displayed for business

	public CommerceModel(String url, String retailerName) {
		this(url, retailerName, null);
	}

	public CommerceModel(String url, String retailerName, String imageUrl) {
		this.url = url;
		this.retailerName = retailerName;
		this.imageUrl = imageUrl;
	}

	public CommerceModel() {
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRetailerName() {
		return retailerName;
	}

	public void setRetailerName(String retailerName) {
		this.retailerName = retailerName;
	}

	/**
	 * The 'best price' for this item, because generally the verticals show only one price.  Should return null
	 * if the subclass doesn't have prices available.
	 * @return
	 */
	public abstract String getBestPrice();

	/**
	 * Does nothing!  Why is this here?  Because `getBestPrice()` is a derived field and so ideally we wouldn't want it
	 * stored in Redis.  But if we @JsonIgnore it so that it doesn't go to redis, it will _also_ be ignored when
	 * we send this object to the browser and prices would be missing.  The solution was to not JsonIgnore
	 * `getBestPrice` (which would store it in redis) but add this no-op setter so that when the Redis code tries to
	 * deserialize it will not fail.
	 * @param price
	 */
	public void setBestPrice(String price) {}

	
	@Override
	public String toString() {
		return "CommerceModel [imageUrl=" + imageUrl + ", url=" + url + ", retailerName=" + retailerName + "]";
	}
	
}
