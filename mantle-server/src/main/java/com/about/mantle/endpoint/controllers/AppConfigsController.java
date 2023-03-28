package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.hippodrome.config.CommonPropertyFactory;
import com.about.hippodrome.config.HippodromePropertyFactory;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;


/**
 * 
 * Displays configs loaded by the app.
 * Also shows services and environment name.
 * Output is in json format
 *
 */
public class AppConfigsController extends AbstractMantleEndpointController {
	private final HippodromePropertyFactory propertyFactory = CommonPropertyFactory.INSTANCE.get();
	private final MantleRequestHandlerMethods handlerMethods;

	public AppConfigsController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public String getPath() {
		return "/debug/configs";
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		handlerMethods.showLoadedConfigs(RequestContext.get(request), request, response, propertyFactory.getUsageMap());
	}

}
