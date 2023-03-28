package com.about.mantle.endpoint.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class FlamegraphSvgController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public FlamegraphSvgController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws GlobeException, IOException, InterruptedException {
		response.setHeader("Content-Type", "image/svg+xml");
		handlerMethods.renderFlamegraph(request, response);
	}

	@Override
	public String getPath() {
		return "/debug/flamegraph.svg";
	}

}
