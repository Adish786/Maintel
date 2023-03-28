package com.about.mantle.infocat.services;

import java.util.List;
import java.util.Map;

import com.about.mantle.infocat.model.product.Product;

/**
 * Service for retrieving Product records from InfoCat
 */
public interface ProductService {
	/**
	 * Returns the {@link Product} with the given id.
	 */
	Product getProduct(String id);

	/**
	 * Returns the associated {@link Product} for each given id, indexed by id.
	 */
	Map<String, Product> getProducts(List<String> ids);
}
