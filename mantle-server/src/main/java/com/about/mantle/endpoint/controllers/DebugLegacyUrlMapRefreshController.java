package com.about.mantle.endpoint.controllers;

import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DebugLegacyUrlMapRefreshController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public DebugLegacyUrlMapRefreshController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "text/plain");
		handlerMethods.debugLegacyUrlMapRefresh(request, response);
	}

	@Override
	public String getPath() {
		return "/debug/legacy-url-map/refresh";
	}

}
