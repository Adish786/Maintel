package com.about.mantle.endpoint.controllers;

import com.about.globe.core.render.CoreRenderUtils;
import com.about.mantle.handlers.methods.MantleResourceHandlerMethods;
import com.about.mantle.render.MantleRenderUtils;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles the path for /favicon.ico and wraps request to use resource static path for handling by
 * resource handler methods.
 */
final public class FaviconHandlerController extends AbstractMantleEndpointController {

	private MantleResourceHandlerMethods handlerMethods;
	private MantleRenderUtils renderUtils;

	public FaviconHandlerController(CoreRenderUtils renderUtils, MantleResourceHandlerMethods handlerMethods) {
		if (renderUtils instanceof MantleRenderUtils) {
			this.renderUtils = (MantleRenderUtils) renderUtils;
		}
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
			@Override
			public String getRequestURI() {
				return (renderUtils != null ? (renderUtils.getStaticPath() + "/icons/favicons") : "") + "/favicon.ico";
			}
		};
		handlerMethods.handleResourceRequest(wrapper, response);
	}

	@Override
	public String getPath() {
		return "/favicon.ico";
	}

}
