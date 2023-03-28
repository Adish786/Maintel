package com.about.mantle.exception;

import com.about.globe.core.exception.GlobeException;

/**
 * Exception thrown by the CommerceApiMapper when the provided type on the CommerceInfoEx isn't a valid commerce
 * mapping.
 *
 */
public class CommerceTypeException extends GlobeException {

	private static final long serialVersionUID = 1L;

	public CommerceTypeException(String message) {
		super(message);
	}

	public CommerceTypeException(String message, Throwable cause) {
		super(message, cause);
	}

}
