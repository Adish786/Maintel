package com.about.mantle.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.web.filter.AbstractCoreFilter;

/**
 * Hand overs incoming request to proxy servlet.
 * 
 * See implementation of {@link ExternalServiceProxyHandler} for actual handling
 * of the request. In case of error, continues with next filters in the chain.
 */
public class ExternalServiceProxyFilter extends AbstractCoreFilter {
	private static final Logger logger = LoggerFactory.getLogger(ExternalServiceProxyFilter.class);

	private ExternalServiceProxyHandler proxyHandler;

	public ExternalServiceProxyFilter(ExternalServiceProxyHandler proxyHandler) {
		this.proxyHandler = proxyHandler;
	}

	
	@Override
	protected void doHttpFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (proxyHandler.handle(request, response)) {
			chain.doFilter(request, response);
		}
	}
}
