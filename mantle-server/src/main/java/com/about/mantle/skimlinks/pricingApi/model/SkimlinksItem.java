package com.about.mantle.skimlinks.pricingApi.model;

public class SkimlinksItem {

	private static final long serialVersionUID = 1L;
	
	private Double salePrice;
	private String status;
	private String deeplink;
	
	public Double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDeeplink() {
		return deeplink;
	}

	public void setDeeplink(String deeplink) {
		this.deeplink = deeplink;
	}
}
