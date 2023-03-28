package com.about.mantle.exception;

import com.about.globe.core.exception.GlobeException;

/**
 * Exception thrown by spring configuration when a service (ex: Selene) can't be found during
 * service discovery
 */
public class MissingServiceException extends GlobeException {

	private static final long serialVersionUID = 1L;

	private String serviceName;

	public MissingServiceException(String message) {
		super(message);
	}

	public MissingServiceException(String message, String serviceName) {
		super(message);
		this.serviceName = serviceName;
	}

	public MissingServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
