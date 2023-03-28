package com.about.mantle.infocat.model.product;

import java.util.Objects;

public class BookProduct extends Product {

	private static final long serialVersionUID = 1L;

	@Override
	public String getProductName() {
		return Objects.toString(getPropertyValue("fullTitle").getPrimaryValue(), null);
	}
	
	@Override
	public String getShortTitle() {
		return Objects.toString(getPropertyValue("shortTitle").getPrimaryValue(), null);
	}

	@Override
	public String getLongTitle() {
		return Objects.toString(getPropertyValue("fullTitle").getPrimaryValue(), null);
	}
}
