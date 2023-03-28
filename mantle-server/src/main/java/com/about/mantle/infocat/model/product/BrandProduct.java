package com.about.mantle.infocat.model.product;

import java.util.Objects;

public class BrandProduct extends Product {

	private static final long serialVersionUID = 1L;
	
	@Override
	public String getProductName() {
		return null;
	}
	
	@Override
	public String getShortTitle() {
		return Objects.toString(getPropertyValue("brand").getPrimaryValue(), null);
	}

	@Override
	public String getLongTitle() {
		return Objects.toString(getPropertyValue("brand").getPrimaryValue(), null);
	}

}
