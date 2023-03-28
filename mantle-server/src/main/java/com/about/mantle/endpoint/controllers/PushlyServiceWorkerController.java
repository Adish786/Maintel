package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class PushlyServiceWorkerController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;
	private String pushlyDomainKey;

	public PushlyServiceWorkerController(MantleRequestHandlerMethods handlerMethods, String pushlyDomainKey) {
		this.handlerMethods = handlerMethods;
		this.pushlyDomainKey = pushlyDomainKey;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "application/javascript");
		response.setHeader("Cache-Control", "max-age=3600, public");

		if (pushlyDomainKey != null)
			handlerMethods.renderTemplate("pushly-service-worker-template", RequestContext.get(request), request, response);
		else
			render403(request, response);
	}

	@Override
	public String getPath() {
		return "/pushly-sdk-worker.js";
	}

}
