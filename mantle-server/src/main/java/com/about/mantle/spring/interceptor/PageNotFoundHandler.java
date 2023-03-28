package com.about.mantle.spring.interceptor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PageNotFoundHandler {
	public boolean is404(HttpServletRequest request);
	public boolean is404(HttpServletResponse response);
	public void handle404(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException;
}
