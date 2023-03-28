package com.about.mantle.infocat.model;

import org.apache.commons.lang3.tuple.Pair;

import com.about.mantle.infocat.model.product.Product;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx.StructuredContentProductRecordDataEx;

/**
 * This class was introduced because Jackson couldn't deserialize {@link Pair} 
 */
public class InfoCatRecordPair {

	private StructuredContentProductRecordDataEx structuredContentProductRecordDataEx;
	private Product product;

	// for jackson
	public InfoCatRecordPair() {
	}

	public InfoCatRecordPair(StructuredContentProductRecordDataEx structuredContentProductRecordDataEx,
			Product product) {
		this.structuredContentProductRecordDataEx = structuredContentProductRecordDataEx;
		this.product = product;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public StructuredContentProductRecordDataEx getStructuredContentProductRecordDataEx() {
		return structuredContentProductRecordDataEx;
	}

	public void setStructuredContentProductRecordDataEx(
			StructuredContentProductRecordDataEx structuredContentProductRecordDataEx) {
		this.structuredContentProductRecordDataEx = structuredContentProductRecordDataEx;
	}

	@Override
	public String toString() {
		return "InfoCatRecordPair [structuredContentProductRecordDataEx=" + structuredContentProductRecordDataEx
				+ ", product=" + product + "]";
	}

}
