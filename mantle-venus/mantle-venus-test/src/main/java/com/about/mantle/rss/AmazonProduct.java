package com.about.mantle.rss;

import javax.xml.bind.annotation.XmlElement;

public class AmazonProduct {

	String amazonProductURL;
	String amazonProductSummary;
	String amazonProductHeadline;
	String amazonAward;

	public String getAmazonProductSummary() { return amazonProductSummary; }

	public String getAmazonProductHeadline() { return amazonProductHeadline; }

	public String getAmazonProductURL() {
		return amazonProductURL;
	}

	public String getAmazonAward() {
		return amazonAward;
	}

	@XmlElement(name = "productURL", namespace = "https://amazon.com/ospublishing/1.0/")
	public void setAmazonProductURL(String amazonProductURL) {
		this.amazonProductURL = amazonProductURL;
	}

	@XmlElement(name = "productSummary", namespace = "https://amazon.com/ospublishing/1.0/")
	public void setAmazonProductSummary(String amazonProductSummary) {
		this.amazonProductSummary = amazonProductSummary;
	}

	@XmlElement(name = "productHeadline", namespace = "https://amazon.com/ospublishing/1.0/")
	public void setAmazonProductHeadline(String amazonProductHeadline) { this.amazonProductHeadline = amazonProductHeadline; }

	@XmlElement(name = "award", namespace = "https://amazon.com/ospublishing/1.0/")
	public void setAmazonAward(String amazonAward) {
		this.amazonAward = amazonAward;
	}
}
