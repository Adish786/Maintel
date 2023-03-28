package com.about.mantle.endpoint.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class DoSomethingMagicalController extends AbstractMantleEndpointController {

	private MantleRequestHandlerMethods handlerMethods;

	public DoSomethingMagicalController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws GlobeException, ServletException, IOException {
		handlerMethods.doSomethingMagical(RequestContext.get(request), request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		doGet(request, response);
	}

	@Override
	public void doPatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		doGet(request, response);
	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws Exception {
		doGet(request, response);
	}

	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		doGet(request, response);
	}

	@Override
	public String getPath() {
		return "/dosomethingmagical.*";
	}

}
