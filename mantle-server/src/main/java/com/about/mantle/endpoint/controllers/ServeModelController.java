package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

/**
 * 
 * Get json representing the models that would be available for template
 * rendering.
 *
 */
public class ServeModelController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public ServeModelController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "application/json");
		handlerMethods.serveModel(RequestContext.get(request), request, response);
	}

	@Override
	public String getPath() {
		return "/servemodel/model.json";
	}

}
