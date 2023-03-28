package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class SearchPageController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public SearchPageController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "text/html");
		response.setHeader("Cache-Control", "max-age=1800, private");
		handlerMethods.renderSearchTemplate(RequestContext.get(request), request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		doGet(request, response);
	}

	@Override
	public String getPath() {
		return "/search";
	}

}
