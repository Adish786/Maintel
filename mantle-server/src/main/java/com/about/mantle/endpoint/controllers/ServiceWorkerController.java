package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class ServiceWorkerController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public ServiceWorkerController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "application/javascript");
		response.setHeader("Cache-Control", "max-age=3600, private");
		handlerMethods.serveResource("/web/stub-sw.js", RequestContext.get(request), request, response);
	}

	@Override
	public String getPath() {
		return "/homescreen-sw.js";
	}

}
