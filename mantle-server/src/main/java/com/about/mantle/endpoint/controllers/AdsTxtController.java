package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class AdsTxtController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;
	private final String adsTxtBovdPath;

	public AdsTxtController(MantleRequestHandlerMethods handlerMethods, String adsTxtBovdPath) {
		this.handlerMethods = handlerMethods;
		this.adsTxtBovdPath = adsTxtBovdPath;
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "text/plain");
		response.setHeader("Cache-Control", "max-age=3600, private");
		handlerMethods.renderAdsTxt(RequestContext.get(request), request, response, adsTxtBovdPath);
	}

	@Override
	public String getPath() {
		return "/ads.txt";
	}

}
