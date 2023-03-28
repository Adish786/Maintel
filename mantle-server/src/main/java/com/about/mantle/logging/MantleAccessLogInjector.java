package com.about.mantle.logging;

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.about.globe.core.definition.resource.DSSDeviceCategoryMapping;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.web.filter.AccessLogInjector;
import com.about.mantle.spring.interceptor.CookieInterceptor;

/**
 * For nginx logging to add special values for logging purposes
 * 
 * TODO: Add test id
 */
public class MantleAccessLogInjector implements AccessLogInjector {

	private static final Logger logger = LoggerFactory.getLogger(MantleAccessLogInjector.class);

	private static final String DEVICE_TYPE = "deviceType";
	private static final String REQUEST_URI = "PATH";

	protected String getRequestUriKey() {
		return REQUEST_URI;
	}

	protected String getDeviceTypeKey() {
		return DEVICE_TYPE;
	}

	protected String getIdStampKey() {
		return CookieInterceptor.IDSTAMP;
	}

	@Override
	public void clear() {
		MDC.clear();
	}

	protected void writeRequestUri(RequestContext requestContext) {

		// TODO: Figure out what this fragment logic is
		Map<String, List<String>> queryParams = requestContext.getUrlData().getQueryParams();
		String fragment = null;
		if (queryParams != null && queryParams.get("fragment") != null && queryParams.get("fragment").size() > 0) {
			fragment = queryParams.get("fragment").get(0);
		}

		if (fragment != null) {
			try {
				fragment = URLDecoder.decode(fragment, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("Error decoding " + fragment + " in url: " + requestContext.getUrlData().getUrl());
			}
		}

		MDC.put(getRequestUriKey(), requestContext.getUrlData().getPath() + defaultString(fragment, ""));
	}

	protected void writeDeviceCategory(RequestContext rc) {

		MDC.put(getDeviceTypeKey(), DSSDeviceCategoryMapping.get(rc.getUserAgent().getDeviceCategory()));

	}

	protected void writeRequestId(RequestContext rc) {

		MDC.put(getIdStampKey(), rc.getRequestId());

	}

	@Override
	public void write(RequestContext requestContext, HttpServletResponse response) {
		writeRequestUri(requestContext);
		writeDeviceCategory(requestContext);
		writeRequestId(requestContext);
	}

	@Override
	public void write(HttpServletRequest request) {
	}
}
