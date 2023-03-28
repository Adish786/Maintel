package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

/**
 * Get json representing the models that would be available for template rendering.
 *
 */
public class DebugModelsController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public DebugModelsController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "application/json");
		handlerMethods.debugModels(request, response);
	}

	@Override
	public String getPath() {
		return "/debug/models.json";
	}

}
