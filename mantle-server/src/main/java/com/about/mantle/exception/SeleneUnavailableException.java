package com.about.mantle.exception;

import com.about.globe.core.exception.GlobeException;

/**
 * Exception thrown by spring configuration when a call to Selene (ex: to authenticate on startup) fails
 */
public class SeleneUnavailableException extends GlobeException {

	private static final long serialVersionUID = 1L;

	public SeleneUnavailableException(Throwable cause) {
		this(null, cause);
	}

	public SeleneUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}
}
