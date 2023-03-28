package com.about.mantle.infocat.model.product;

import java.util.Objects;

public class DefaultProduct extends Product {

	private static final long serialVersionUID = 1L;

	@Override
	public String getProductName() {
		return Objects.toString(getPropertyValue("technicalName").getPrimaryValue(), null);
	}
	
	@Override
	public String getShortTitle() {
		return Objects.toString(getPropertyValue("brand").getPrimaryValue(), "")
				+ ' ' + Objects.toString(getPropertyValue("shortName").getPrimaryValue(), "");
	}
	
	@Override
	public String getLongTitle() {
		return Objects.toString(getPropertyValue("brand").getPrimaryValue(), "")
				+ ' ' + Objects.toString(getPropertyValue("technicalName").getPrimaryValue(), "");
	}
}
