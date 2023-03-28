package com.about.mantle.endpoint.controllers;

import com.about.globe.core.render.CoreRenderUtils;
import com.about.mantle.handlers.methods.MantleResourceHandlerMethods;
import com.about.mantle.render.MantleRenderUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles the path for any apple favicons and wraps request to use resource static path for handling by
 * resource handler methods.
 */
final public class AppleFaviconHandlerController extends AbstractMantleEndpointController {

	private MantleResourceHandlerMethods handlerMethods;
	private MantleRenderUtils renderUtils;

	public AppleFaviconHandlerController(CoreRenderUtils renderUtils, MantleResourceHandlerMethods handlerMethods) {
		if (renderUtils instanceof MantleRenderUtils) {
			this.renderUtils = (MantleRenderUtils) renderUtils;
		}
		this.handlerMethods = handlerMethods;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String path = getAppleFaviconFileName(request.getServletPath());

		HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
			@Override
			public String getRequestURI() {

				return (renderUtils != null ? (renderUtils.getStaticPath() + "/icons/favicons") : "") + path;
			}
		};
		handlerMethods.handleResourceRequest(wrapper, response);
	}

	/**
	 * Apple favicons can request many different paths for our favicons. Instead of hosting many different files in many different places
	 * this method concatenates a string for a path to a file that we do have. Example paths we can receive and what they will become:
	 *  - /apple-touch-icon-60x60-precomposed.png -> static path to /apple-touch-icon-60x60.png
	 *  - /apple-touch-icon-60x60.png -> static path to /apple-touch-icon-60x60.png
	 *  - /apple-touch-icon-precomposed.png -> static path to /apple-touch-icon-120x120.png
	 *  - /apple-touch-icon.png -> static path to /apple-touch-icon-120x120.png
	 *
	 *  If they are requesting the relative path we will return the path to the static file.
	 *  If they are requesting the precomposed version (one that we don't want additional styles added to)
	 *      we will return the path to that static file without the precomposed string in the name
	 *  If they are not specifying a size we will default to 120x120
	 * @param originalPath - the original request path from the URL
	 * @return - path to the correct file we would like to show
	 */
	private String getAppleFaviconFileName(String originalPath) {
		if (StringUtils.indexOf(originalPath, "x") != -1) { // if there is an x in the name it must contain a size
			return StringUtils.replace(originalPath, "-precomposed", "");
		}

		return "/apple-touch-icon-120x120.png";
	}

	@Override
	public String getPath() { return "/apple-touch-icon*.png"; }

}
