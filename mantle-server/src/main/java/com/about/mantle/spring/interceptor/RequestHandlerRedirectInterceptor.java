package com.about.mantle.spring.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;

/**
 * Decides whether to redirect a request before reaching
 * request handling controllers. Note that {@link GlobalRedirectFilter} is run for all requests.
 * Delegates to whatever instance of {@link RedirectHandler} is available from
 * Spring.
 */
public class RequestHandlerRedirectInterceptor extends AbstractMantleInterceptor {

	private RedirectHandler redirectHandler;

	public RequestHandlerRedirectInterceptor(RedirectHandler redirectHandler) {
		this.redirectHandler = redirectHandler;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		boolean answer = true;
		RedirectHandler.Redirect redirect = redirectHandler.shouldRedirectForNonResourceRequests(request);
		if (redirect != null) {
			response.setHeader(HttpHeader.LOCATION.asString(), redirect.getRedirectUrl());
			response.setStatus(redirect.getHttpStatusCode().getCode());
			answer = false;
		}
		return answer;
	}

	@Override
	final public String getIncludePathPatterns() {
		return "/**";
	}

}
