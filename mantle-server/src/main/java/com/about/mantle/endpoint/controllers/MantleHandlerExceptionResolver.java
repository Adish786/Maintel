package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.about.mantle.spring.interceptor.PageGoneHandler;
import com.about.globe.core.exception.GlobeErrorResponseHandlerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.exception.GlobeNotFoundException;
import com.about.mantle.spring.interceptor.PageNotFoundHandler;

import java.io.IOException;

/**
 * This is our custom implementation of {@link HandlerExceptionResolver} where we handle Globe
 * related exception which we want to handle in a special way. e.g. we want to
 * handle {@link GlobeNotFoundException} by forwarding to 404 page. However, we
 * don't want to treat {@link GlobeException} in such special way, thus we let
 * this handlerExceptionResolver throw that exception to Jetty to be handled by its 
 * error handler.
 */

public class MantleHandlerExceptionResolver implements HandlerExceptionResolver {
	private static final Logger logger = LoggerFactory.getLogger(MantleHandlerExceptionResolver.class);

	private PageNotFoundHandler pageNotFoundHandler;
	private PageGoneHandler pageGoneHandler;

	public MantleHandlerExceptionResolver(PageNotFoundHandler pageNotFoundHandler, PageGoneHandler pageGoneHandler) {
		this.pageNotFoundHandler = pageNotFoundHandler;
		this.pageGoneHandler = pageGoneHandler;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {

		ModelAndView answer = null;
		if (ex instanceof GlobeNotFoundException) {
			try {
				if(pageGoneHandler.is410(request)) {
					pageGoneHandler.handle410(request, response);
				}else{
					pageNotFoundHandler.handle404(request, response);
				}
				// make sure you return empty ModelView otherwise, it will throw exception to
				// jetty
				answer = new ModelAndView();
			} catch (Exception e) {
				logger.error("Exception occurred while serving 404 page", e);
			}
		}

        if (ex instanceof GlobeErrorResponseHandlerException) {
            try {
                response.sendError(((GlobeErrorResponseHandlerException) ex).getStatusCode());
            } catch (IOException e) {
                logger.error("Exception occurred while handling 529 response (GlobeTemplateNotFoundException)", e);
            }
            answer = new ModelAndView();
        }
		return answer;
	}

}
