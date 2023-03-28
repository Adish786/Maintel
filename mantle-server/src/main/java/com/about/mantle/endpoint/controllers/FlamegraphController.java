package com.about.mantle.endpoint.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

/**
 * 
 * Render flamegraph if enabled
 *
 */
public class FlamegraphController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public FlamegraphController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws GlobeException, ServletException, IOException {
		response.setHeader("Content-Type", "text/html");
		handlerMethods.renderTemplate("flamegraph-template", RequestContext.get(request), request, response);
	}

	@Override
	public String getPath() {
		return "/debug/flamegraph.htm";
	}

}
