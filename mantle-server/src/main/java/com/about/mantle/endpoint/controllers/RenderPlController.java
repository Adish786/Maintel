package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class RenderPlController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public RenderPlController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "text/html");
		response.setHeader("Cache-Control", "max-age=1800, private");
		handlerMethods.renderPlTemplate(RequestContext.get(request), request, response);;
	}

	@Override
	public String getPath() {
		return "/pattern-library/**";
	}

}
