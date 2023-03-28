package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class SitemapIndexController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public SitemapIndexController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "text/xml");
		response.setHeader("Cache-Control", "max-age=86400");
		handlerMethods.renderTemplate("mntl-sitemap-index", RequestContext.get(request), request, response);
	}

	@Override
	public String getPath() {
		return "/sitemap.xml";
	}

}
