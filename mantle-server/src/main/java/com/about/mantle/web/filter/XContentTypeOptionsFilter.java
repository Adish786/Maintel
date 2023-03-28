package com.about.mantle.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * GLBE-5022 Prevent MIME-sniffing attacks.
 * @author jkrayer 6/20/2017
 *
 */
public final class XContentTypeOptionsFilter extends AbstractHttpFilter {

	public static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
	public static final String NO_SNIFF = "nosniff";

	@Override
	protected void doHttpFilter(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {
		res.setHeader(X_CONTENT_TYPE_OPTIONS, NO_SNIFF);
		filterChain.doFilter(req, res);
	}

}
