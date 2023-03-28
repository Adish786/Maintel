package com.about.mantle.spring.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.http.HttpStatus;


/**
 * Consolidates all redirect handling.
 */
public interface RedirectHandler {
	public static class Redirect {
		private final String redirectUrl;
		private final HttpStatus.Code httpStatusCode;

		public Redirect(String redirectUrl, HttpStatus.Code httpStatusCode) {
			this.redirectUrl = redirectUrl;
			this.httpStatusCode = httpStatusCode;
		}

		public String getRedirectUrl() {
			return redirectUrl;
		}

		public HttpStatus.Code getHttpStatusCode() {
			return httpStatusCode;
		}
	}
	
	/**
	 * Used by {@link GlobalRedirectFilter} to determine whether a request should be redirected.
	 * This filter is applied to _all_ requests including resources.
	 *
	 * NOTE: In an ideal world this would be a single shouldRedirect(request) that gets called at the global
	 *       filter level. Unfortunately we have some rules that should only apply to certain requests. This
	 *       forces us to split the logic such that rules that can apply to all requests get handled in this
	 *       method and rules that get applied to a subset of requests get handled in the following method.
	 *
	 * @param httpServletRequest
	 * @return Redirect object if the request should be redirected; otherwise null.
	 */
	Redirect shouldRedirectForAllRequests(HttpServletRequest httpServletRequest);

	/**
	 * Used by {@link RequestHandlerRedirectInterceptor} to determine whether a request should be redirected.
	 * This filter is applied to non-resource requests only, e.g. template rendering.
	 *
	 * @param httpServletRequest
	 * @return Redirect object if the request should be redirected; otherwise null.
	 */
	Redirect shouldRedirectForNonResourceRequests(HttpServletRequest httpServletRequest);
}
