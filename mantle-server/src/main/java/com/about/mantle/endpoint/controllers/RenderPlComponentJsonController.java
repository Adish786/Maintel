package com.about.mantle.endpoint.controllers;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RenderPlComponentJsonController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public RenderPlComponentJsonController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "application/json");
		response.setHeader("Cache-Control", "max-age=1800, private");
		handlerMethods.renderTemplate("mntl-pl-json", RequestContext.get(request), request, response);
	}

	@Override
	public String getPath() {
		return "/pattern-library.json";
	}

}
