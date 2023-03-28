package com.about.mantle.web.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.logging.Logger404;
import com.about.mantle.spring.interceptor.PageNotFoundHandler;

public class Mantle404Handler implements PageNotFoundHandler {

	private static final String FILE_NOT_FOUND_TEMPLATE = "/404";

	@Override
	public void handle404(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setHeader("Cache-Control", "no-cache");
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		request.getRequestDispatcher(FILE_NOT_FOUND_TEMPLATE).forward(request, response);
		Logger404.log(request, response,
				request.getServerName() + ":" + request.getServerPort() + FILE_NOT_FOUND_TEMPLATE);

	}

	@Override
	public boolean is404(HttpServletRequest request) {
		return FILE_NOT_FOUND_TEMPLATE.equals(request.getRequestURI());
	}

	@Override
	public boolean is404(HttpServletResponse response) {
		return response.getStatus() == HttpServletResponse.SC_NOT_FOUND;
	}

}
