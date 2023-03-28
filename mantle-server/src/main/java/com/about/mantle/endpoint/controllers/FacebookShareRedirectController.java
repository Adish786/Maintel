package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

/**
 * Load necessary JS to close Facebook share window
 */
public class FacebookShareRedirectController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public FacebookShareRedirectController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "text/html");
		response.setHeader("Cache-Control", "max-age=3600, private");
		handlerMethods.renderFacebookShareRedirectTemplate(RequestContext.get(request), request, response);
	}

	@Override
	public String getPath() {
		return "/facebookshareredirect.htm";
	}

}
