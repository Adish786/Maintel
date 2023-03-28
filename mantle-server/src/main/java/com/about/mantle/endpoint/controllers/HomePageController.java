package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.hippodrome.url.UrlData;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class HomePageController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public HomePageController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Cache-Control", "max-age=1800, private");
		response.setHeader("Content-Type", "text/html");
		handlerMethods.renderHomePageTemplate(RequestContext.get(request), request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		RequestContext requestContext = RequestContext.get(request);
		if (isDocIdInVerticalUrlData(requestContext)) {// deferred requests
			handlerMethods.renderDocumentTemplate(requestContext, request, response);
		} else {
			render403(request, response);
		}
	}

	private boolean isDocIdInVerticalUrlData(RequestContext requestContext) {
		UrlData urlData = requestContext.getUrlData();
 		return (urlData instanceof VerticalUrlData && ((VerticalUrlData) urlData).getDocId() != null);
	}

	@Override
	public String getPath() {
		return "/";
	}

}
