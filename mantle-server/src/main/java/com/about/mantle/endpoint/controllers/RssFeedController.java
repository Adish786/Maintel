package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class RssFeedController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public RssFeedController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "text/xml");
		handlerMethods.renderTemplate("mntl-rss-curated", RequestContext.get(request), request, response);
	}

	@Override
	public String getPath() {
		return "/feeds/*.rss";
	}

}
