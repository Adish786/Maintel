package com.about.mantle.spring.interceptor;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.BadMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import com.about.globe.core.exception.GlobeNotFoundException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.RequestContextSource;
import com.about.globe.core.web.filter.AccessLogInjector;
import com.google.common.collect.Sets;

public class RequestContextContributorInterceptor extends AbstractMantleInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(RequestContextContributorInterceptor.class);
	private final RequestContextSource requestContextSource;
	private final AccessLogInjector accessLogInjector;
	// TODO: Siteok.htm may be obsolete.
	private static final Set<String> IGNORE_PATH_SET = Sets.newHashSet("/siteok.htm", "/global_error_page");

	public RequestContextContributorInterceptor(RequestContextSource requestContextSource,
			AccessLogInjector accessLogInjector) {
		this.requestContextSource = requestContextSource;
		this.accessLogInjector = accessLogInjector;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean answer = false;
		// Each thread will hold on to its values so we need to clear previous request
		// values for this thread
		accessLogInjector.clear();

		if (!IGNORE_PATH_SET.contains(request.getRequestURI())) {
			try {
				RequestContext.set(request, requestContextSource.get(request));
				answer = true;
			} catch (GlobeNotFoundException e) {
				throw e; // allow this exception to bubble up to {@link PageNotFoundFilter}
			} catch (BadMessageException e) {
				response.setStatus(400);
				request.getRequestDispatcher("/global_error_page").forward(request, response);
			} catch (Exception e) {
				logger.error("Error in globe for request " + request.getRequestURL() + " and query params: "
						+ request.getQueryString(), e);
				response.setStatus(500);
				request.getRequestDispatcher("/global_error_page").forward(request, response);
			}
		}

		return answer;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (IGNORE_PATH_SET.contains(request.getRequestURI())) {
			accessLogInjector.write(request);
		} else {
			accessLogInjector.write(RequestContext.get(request), response);
		}
	}

	@Override
	final public String getIncludePathPatterns() {
		return "/**";
	}

	@Override
	public String getExcludePathPatterns() {
		return "";
	}

}
