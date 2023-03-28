package com.about.mantle.endpoint.controllers;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GoogleNewsSitemapController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public GoogleNewsSitemapController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "text/xml");
		response.setHeader("Cache-Control", "max-age=86400");
		handlerMethods.renderTemplate("mntl-google-news-sitemap", RequestContext.get(request), request, response);
	}

	@Override
	public String getPath() {
		return "/google-news-sitemap.xml";
	}

}
