package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class UgcRatingController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public UgcRatingController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		handlerMethods.handleUgcRating(RequestContext.get(request));
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		doGet(request, response);
	}

	@Override
	public String getPath() {
		return "/ugc-rating";
	}

}
