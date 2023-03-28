package com.about.mantle.http.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class RequestSourceUtils {
	// These headers are used to retrieve the client IP from the request
	// headers, the request is usually incoming
	// through a CDN and load-balancer which use a HTTP header to relay the
	// original IP
	public static String getRemoteIp(HttpServletRequest request) {
		String remoteIp = StringUtils.defaultIfBlank(request.getHeader("Fastly-Client-IP"),
				StringUtils.defaultIfBlank(request.getHeader("CIPA"),
						StringUtils.defaultIfBlank(request.getHeader("X-Forwarded-For"), request.getRemoteAddr())));
		return StringUtils.split(StringUtils.deleteWhitespace(remoteIp), ",")[0];
	}

	// These headers are used to retrieve the client protocol from the request
	// headers, the request is usually incoming
	// through a load-balancer which typically terminates the HTTPS protocol in
	// order to route traffic, relaying the traffic
	// to the server over HTTP with a header specifying the original protocol of
	// the client
	public static boolean isRemoteSecure(HttpServletRequest request) {
		return StringUtils.defaultIfBlank(request.getHeader("HTTPS"), "off").equalsIgnoreCase("on")
				|| StringUtils.defaultIfBlank(request.getHeader("X-Forwarded-Proto"), "http").equalsIgnoreCase("https")
				|| request.isSecure();
	}

	/**
	 * Checks for the presence of a header set at the Fastly Edge to indicate the request originates
	 * from a trusted Google crawler. See https://dotdash.atlassian.net/browse/CLDFD-1365
	 * @param request
	 * @return
	 */
	public static boolean isGoogleBot(HttpServletRequest request) {
		return Boolean.parseBoolean(request.getHeader("isgooglebot"));
	}
}
