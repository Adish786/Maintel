package com.about.mantle.endpoint.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class TestLoggerController extends AbstractMantleEndpointController {
	private MantleRequestHandlerMethods handlerMethods;

	public TestLoggerController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws GlobeException, ServletException, IOException {
		handlerMethods.testLogger(RequestContext.get(request), request, response);
	}

	// disabling HEAD
	@Override
	protected boolean useGetHandlerForHeadRequests() {
		return false;
	}

	@Override
	public String getPath() {
		return "/testlogger.*";
	}

}
