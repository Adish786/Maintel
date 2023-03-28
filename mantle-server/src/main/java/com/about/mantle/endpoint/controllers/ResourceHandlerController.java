package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.mantle.handlers.methods.MantleResourceHandlerMethods;

final public class ResourceHandlerController extends AbstractMantleEndpointController {

	private MantleResourceHandlerMethods handlerMethods;

	public ResourceHandlerController(MantleResourceHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		handlerMethods.handleResourceRequest(request, response);
	}

	@Override
	public String getPath() {
		return "/static/**";
	}

}
