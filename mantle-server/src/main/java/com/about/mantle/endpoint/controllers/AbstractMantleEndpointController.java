package com.about.mantle.endpoint.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.exception.GlobeException;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;

/**
 * 
 * This is class represents an end-point. An abstract class to be extended by all end-point controllers.
 * End-points controllers must override getPath method to provide the end-point path.
 * It contains default implementation for all HHTP methods for an end-point. Also, has some utility methods.
 * Controllers should override methods which they want to expose as an end-point.  
 * This class extends {@link Controller} because, spring boot could use {@link SimpleControllerHandlerAdapter}
 * to call {@link Controller#handleRequest(HttpServletRequest, HttpServletResponse)}
 */
public abstract class AbstractMantleEndpointController implements Controller  {
	public enum Operation {GET, SAVE, DELETE }

	private Map<String, ThrowingBiConsumer<HttpServletRequest, HttpServletResponse>> methodToConsumers = createMap();

	private Map<String, ThrowingBiConsumer<HttpServletRequest, HttpServletResponse>> createMap() {
		Map<String, ThrowingBiConsumer<HttpServletRequest, HttpServletResponse>> map = new HashMap<>();
		map.put("get", this::doGet);
		map.put("post", this::doPost);
		map.put("head", this::doHead);
		map.put("delete", this::doDelete);
		map.put("put", this::doPut);
		map.put("patch", this::doPatch);
		return map;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		render405(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		render405(request, response);
	}

	
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (useGetHandlerForHeadRequests()) {
			doGet(request, response);
		} else {
			render405(request, response);
		}
	}

	protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		render405(request, response);
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws Exception {
		render405(request, response);
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		render405(request, response);
	}

	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String method = request.getMethod().toLowerCase();
		methodToConsumers.get(method).accept(request, response);
	}

    protected void render403(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpStatus.FORBIDDEN_403);
	}

	protected void render405(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpStatus.METHOD_NOT_ALLOWED_405);
	}

	protected void forwardTo404(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getRequestDispatcher("/404").forward(request, response);
	}

	@FunctionalInterface
	protected interface ThrowingBiConsumer<T, U> {
		void accept(T t, U u) throws Exception;
	}

	protected void addHeadersToResponse(HttpServletResponse response, Map<String, String> headers) {
		for (Entry<String, String> entry : headers.entrySet()) {
			response.setHeader(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 
	 * This boolean decides if we should always enable HEAD when there is GET impl
	 * available in the child class. By-default this is true as majority of our
	 * cases have GET and HEAD together. If child controller wants only GET and
	 * disable HEAD then it will have to override this method and return false.
	 */
	protected boolean useGetHandlerForHeadRequests() {
		return true;
	}
	
	public abstract String getPath();
	
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		doService(request, response);
		return null;
	}

	/*
	 * Utility method for controllers that end with /{operation:get|save|delete}.
	 * Get the http method from the request uri so that we can still leverage POST
	 * for automatic csrf protection and still abide by the http method safe list
	 */
	protected static Operation parseRequestOperation(HttpServletRequest request) {
		try {
			String requestUri = request.getRequestURI();
			String operationPath = requestUri.substring(requestUri.lastIndexOf("/") + 1);

			return Enum.valueOf(Operation.class, operationPath.toUpperCase());
		} catch (Exception e) {
			throw new GlobeException("failed to parse request operation", e);
		}
	}
}
