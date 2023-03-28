package com.about.mantle.endpoint.controllers;

import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Get json representing the models that would be available for template rendering.
 *
 */
public class DebugModelTimingsController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public DebugModelTimingsController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "application/json");
		handlerMethods.debugModelTimings(request, response);
	}

	@Override
	public String getPath() {
		return "/debug/modeltimings.json";
	}

}
