package com.about.mantle.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.HttpStatus;

public class MalformedURLFilter extends AbstractMantleFilter {

	private UrlValidator urlValidator;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String[] schemes = {"http","https"};
		// re: the 2 slashes, the canonicalization filter that happens downstream will redirect to a url w/out xtra slashes
		urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_2_SLASHES);
	}

	@Override
	protected void doHttpFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (urlValidator.isValid(request.getRequestURL().toString())) {
			chain.doFilter(request, response);
		}
		else {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.getWriter().write("The request could not be understood by the server due to malformed syntax. The client should not repeat the request without modifications.");
		}
	}
	
	@Override
	public void destroy() {
	}
	
}
