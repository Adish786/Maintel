package com.about.mantle.web.filter;

import com.about.mantle.http.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * This filter is responsible for providing a wrapper around
 * HttpServletRequest to cache the request body
 */
public class ContentCachingRequestFilter extends AbstractMantleFilter {
    @Override
    protected void doHttpFilter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {

    	//We only want to wrap post messages that don't have the parameter cr set (those are defered)
    	if(httpServletRequest.getMethod() == "POST" && httpServletRequest.getParameter("cr") == null){
			httpServletRequest = new ContentCachingRequestWrapper(httpServletRequest);
    	}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
}
