package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;
/**
 * 
 * Get xml representing the effective template that would be rendered.
 *
 */

public class DebugEffectiveTemplateController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public DebugEffectiveTemplateController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "application/xml");
		handlerMethods.debugEffectiveTemplate(request, response);
	}
	
	@Override
	public String getPath() {
		return "/debug/effectivetemplate.xml";
	}
	
}
