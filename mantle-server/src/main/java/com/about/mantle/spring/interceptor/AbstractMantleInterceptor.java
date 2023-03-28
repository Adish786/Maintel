package com.about.mantle.spring.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Abstract class which forces subclassed interceptors to provide
 * paths to exclude and include from them.    
 *
 */
public abstract class AbstractMantleInterceptor extends HandlerInterceptorAdapter {

	public abstract String getIncludePathPatterns();

	public String getExcludePathPatterns() {
		return "/static/**,/favicon.ico,/apple-touch-icon*.png";
	}
}
