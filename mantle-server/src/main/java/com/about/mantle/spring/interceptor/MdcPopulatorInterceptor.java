package com.about.mantle.spring.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.web.filter.MdcPopulator;

/**
 * Populates {@link org.slf4j.MDC for logging}. Must be _after_
 * {@link com.about.globe.core.web.filter.RequestContextFilter} in the filter
 * chain. Delegates to whatever instance os {@link MdcPopulator} is available
 * from Spring
 */
public class MdcPopulatorInterceptor extends AbstractMantleInterceptor {

	private MdcPopulator mdcPopulator;

	public MdcPopulatorInterceptor(MdcPopulator mdcPopulator) {
		this.mdcPopulator = mdcPopulator;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		RequestContext reqCtx = RequestContext.get(request);
		mdcPopulator.populatePreRequestMdc(request, reqCtx);
		return true;

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		RequestContext reqCtx = RequestContext.get(request);
		mdcPopulator.populatePostRequestMdc(request, reqCtx);
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
