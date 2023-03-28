package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;
import org.apache.http.HttpHeaders;

public class EmbedController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;
	private String primaryDomain;
	
	public EmbedController(MantleRequestHandlerMethods handlerMethods, String primaryDomain) {
		this.handlerMethods = handlerMethods;
		this.primaryDomain = primaryDomain;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "text/html");
		response.setHeader("Cache-Control", "max-age=1800, private");

		response.setHeader(HttpHeaders.VARY, HttpHeaders.REFERER);
		String referer = request.getHeader(HttpHeaders.REFERER);

		if (primaryDomain != null && referer != null && referer.contains(primaryDomain))
			handlerMethods.renderEmbedEndpointTemplate(RequestContext.get(request), request, response);
		else
			render403(request, response);
	}
	
	@Override
	public String getPath() {
		return "/embed";
	}
	
}
