package com.about.mantle.spring.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;

/**
 * Decides up-front whether a request should be redirected before continuing
 * further down the filter chain. Runs on all requests (as opposed to
 * {@link RequestHandlerRedirectInterceptor} Delegates to whatever instance of
 * {@link RedirectHandler} is available from Spring.
 */
public class GlobalRedirectInterceptor extends AbstractMantleInterceptor {

	private RedirectHandler redirectHandler;
	private PageNotFoundHandler pageNotFoundHandler;

	public GlobalRedirectInterceptor(RedirectHandler redirectHandler, PageNotFoundHandler pageNotFoundHandler) {
		this.redirectHandler = redirectHandler;
		this.pageNotFoundHandler = pageNotFoundHandler;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		boolean answer = true;
		if (!pageNotFoundHandler.is404(request) && !pageNotFoundHandler.is404(response)) {
			// We have to be careful to only run the redirect handling logic if the request
			// is *not* the 404 page because
			// otherwise it will try to fetch the document with docId 404 from Selene which
			// happens to be an actual document.
			RedirectHandler.Redirect redirect = redirectHandler.shouldRedirectForAllRequests(request);
			if (redirect != null) {
				response.setHeader(HttpHeader.LOCATION.asString(), redirect.getRedirectUrl());
				response.setStatus(redirect.getHttpStatusCode().getCode());
				answer = false;
			}
		}

		return answer;	
	}

	@Override
	final public String getIncludePathPatterns() {
		return "/**";
	}

}
