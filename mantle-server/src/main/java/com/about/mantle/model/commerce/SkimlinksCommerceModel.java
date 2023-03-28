package com.about.mantle.model.commerce;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;

import com.about.mantle.skimlinks.pricingApi.model.SkimlinksItem;

public class SkimlinksCommerceModel extends CommerceModel {

	private static final long serialVersionUID = 1L;
	
	private String salePrice;
	/*
	 * String returned by Skimlinks representing the result of the scrape ("success", "pending", "failure", "domain-not-supported")
	 */
	private String status;
	/*
	 * The Skimlinks Link wrapped url for the product page url
	 * See https://developers.skimlinks.com/link.html
	 */
	private String deeplink;
	
	private static final Currency usdCurrency = Currency.getInstance("USD");
	
	/*
	 * Needed for Redis deserialization
	 */
	public SkimlinksCommerceModel() {
		super();
	}
	
	public SkimlinksCommerceModel(SkimlinksItem item, String url) {
		this(item, url, null);
    }
	
	public SkimlinksCommerceModel(SkimlinksItem item, String url, String retailerName) {
		super(url, retailerName);
        this.salePrice = formatAndRoundUpPrice(item.getSalePrice());
        this.status = item.getStatus();
        this.deeplink = item.getDeeplink();
    }
	
	@Override
	public String getBestPrice() {
		return getSalePrice();
	}

	public String getSalePrice() {
		return salePrice;
	}
	
	public void setSalePrice(String salePrice) {
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
	
	@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SkimlinksCommerceModel{");
        sb.append("status=").append(status).append(", ");
        sb.append("salePrice=").append(salePrice).append(", ");
        sb.append("deeplink=").append(deeplink).append(", ");
        sb.append("bestPrice=").append(getBestPrice());
        sb.append('}');
        return sb.toString();
    }
	
    private static String formatAndRoundUpPrice(Double price) {
    	if(price == null) return null;
    	
        BigDecimal bd = new BigDecimal(price);
        bd = bd.setScale(0, RoundingMode.UP);
        BigInteger rounded = bd.toBigIntegerExact();

        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        formatter.setCurrency(usdCurrency);
        formatter.setMaximumFractionDigits(0);

        return formatter.format(rounded);
    }

}
