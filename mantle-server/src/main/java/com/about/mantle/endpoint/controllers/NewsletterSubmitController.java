package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

/**
 * 
 * Submit an email signup for the current site, returns success/fail
 *
 */
public class NewsletterSubmitController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public NewsletterSubmitController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		handlerMethods.handleNewsletterSubmit(RequestContext.get(request), request, response);
	}

	@Override
	public String getPath() {
		return "/newsletter/signup";
	}

}
