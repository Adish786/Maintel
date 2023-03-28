package com.about.mantle.spring;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Manages consul configuration for Cors Filter found in MantleFilterSpringConfiguration
 * Provides defaults as well as the ability to override
 */
public class MantleCorsConfigs {
	
	//DEFAULTS
	public static final List<String> DEFAULT_HTTP_METHODS = Arrays.asList("GET", "POST", "HEAD");
	public static final List<String> DEFAULT_HEADERS = Arrays.asList("X-Requested-With", "Content-Type", "Accept","Origin");
	public static final boolean DEFAULT_ALLOW_CREDENTIALS = true;
	public static final Long DEFAULT_PREFLIGHT_MAX_AGE_IN_SECONDS = 1800L;
			
	private final String primaryDomain;
	private final Set<String> allowedOrigins;
	private final Boolean allowCredentials;
	private final Long preFlightMaxAgeInSeconds;
	private final List<String> headers;
	private final List<String> httpMethods;
	
	/**
	 * Takes in a domain name for cors filtering and provides defaults for the rest of the configuration
	 * 
	 * @param primaryDomain - The Domain Name of the application
	 */
	public MantleCorsConfigs(String primaryDomain) {
		this(primaryDomain, Sets.newLinkedHashSet());
	}

	public MantleCorsConfigs(String primaryDomain, Set<String> allowedOrigins) {
		this(primaryDomain, allowedOrigins, null, null, DEFAULT_ALLOW_CREDENTIALS, null);
	}

	/**
	 * Allows for overriding of all default cors configuration variables, where null is passed in
	 * defaults will be used. 
	 * 
	 * @param allowedOrigins - The Domain Name of the application
	 * @param httpMethods - REST methods. GET, POST, HEAD ect. 
	 * @param headers - Allowed headers
	 * @param allowCredentials - Allows the browser to expose the response to frontend java script
	 * @param preFlightMaxAgeInSeconds - How long the preflight can be cached
	 */
	public MantleCorsConfigs(String primaryDomain, Set<String> allowedOrigins, List<String>httpMethods, List<String>headers, boolean allowCredentials, Long preFlightMaxAgeInSeconds) {
		this.primaryDomain = primaryDomain;
		// just in case if incoming set is immutable, create a new set
		this.allowedOrigins = Sets.newLinkedHashSet(allowedOrigins);
		this.allowedOrigins.add(primaryDomain);
		this.headers = headers != null ? headers : DEFAULT_HEADERS;
		this.httpMethods = httpMethods != null ? httpMethods : DEFAULT_HTTP_METHODS;
		this.allowCredentials = allowCredentials;
		this.preFlightMaxAgeInSeconds = preFlightMaxAgeInSeconds != null ? preFlightMaxAgeInSeconds : DEFAULT_PREFLIGHT_MAX_AGE_IN_SECONDS;
	}
	
	public List<String> getHeaders() {
		return headers;
	}

	public Set<String> getAllowedOrigins() {
		return allowedOrigins;
	}

	public Boolean getAllowCredentials() {
		return allowCredentials;
	}

	public Long getPreFlightMaxAgeInSeconds() {
		return preFlightMaxAgeInSeconds;
	}
	
	public List<String> getHttpMethods() {
		return httpMethods;
	}

	public String getPrimaryDomain() {
		return primaryDomain;
	}
}