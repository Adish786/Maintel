package com.about.mantle.exception;

import com.about.globe.core.exception.GlobeException;

/**
 * Exception thrown by the AmazonCommerceApi when a provided ASIN isn't found.
 *
 */
public class CommerceAsinNotFoundException extends GlobeException {

	private static final long serialVersionUID = 1L;

	public CommerceAsinNotFoundException(String message) {
		super(message);
	}

	public CommerceAsinNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
