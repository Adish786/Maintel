package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;


/**
 * Gets the tasks and their required parameters
 * 
 */
public class AppTasksController extends AbstractMantleEndpointController {
	private final MantleRequestHandlerMethods handlerMethods;

	public AppTasksController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public String getPath() {
		return "/debug/tasks";
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		handlerMethods.getTaskConfiguration(RequestContext.get(request), request, response);
	}

}