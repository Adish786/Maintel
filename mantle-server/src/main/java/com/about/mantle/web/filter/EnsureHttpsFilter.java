package com.about.mantle.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;

import com.about.globe.core.web.filter.AbstractCoreFilter;
import com.about.mantle.http.util.RequestSourceUtils;

/**
 * This filter is responsible for force-redirecting all requests except
 * /serverStatus to https (if they are not secured requests)
 */
public class EnsureHttpsFilter extends AbstractCoreFilter {

	@Override
	protected void doHttpFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (isNotSecure(request) && !request.getRequestURI().endsWith("/serverStatus")) {
			String redirectUrl = StringUtils.replaceOnceIgnoreCase(getFullRequestUrl(request), "http://", "https://");
			response.setHeader(HttpHeader.LOCATION.asString(), redirectUrl);
			response.setStatus(HttpStatus.Code.MOVED_PERMANENTLY.getCode());
			return;
		}
		chain.doFilter(request, response);
	}

	private boolean isNotSecure(HttpServletRequest request) {
		return !(request.getScheme().equalsIgnoreCase("https") || RequestSourceUtils.isRemoteSecure(request));
	}

	private String getFullRequestUrl(HttpServletRequest request) {
		String answer = "";
		StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
		String queryString = request.getQueryString();

		if (StringUtils.isBlank(queryString)) {
			answer = requestURL.toString();
		} else {
			answer = requestURL.append('?').append(queryString).toString();
		}

		return answer;
	}

}
