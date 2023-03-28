package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class UgcFeedbackController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public UgcFeedbackController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		handlerMethods.postUgcRatingFeedback(RequestContext.get(request), request, response);
	}

	@Override
	public String getPath() {
		return "/ugc-feedback";
	}

}
