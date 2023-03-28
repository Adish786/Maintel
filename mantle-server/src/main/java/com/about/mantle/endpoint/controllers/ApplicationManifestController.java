package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class ApplicationManifestController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public ApplicationManifestController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	// For progressive web app
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "application/json");
		response.setHeader("Cache-Control", "max-age=3600, private");
		handlerMethods.renderTemplate("mntl-application-manifest", RequestContext.get(request), request, response);
	}

	@Override
	public String getPath() {
		return "/manifest.json";
	}

}
