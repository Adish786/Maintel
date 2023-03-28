package com.about.mantle.endpoint.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.exception.GlobeNotFoundException;
import com.about.globe.core.http.RequestContext;
import com.about.hippodrome.url.UrlData;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;
import org.springframework.web.cors.CorsConfiguration;

/**
 * 
 * This controller acts as a catch-all controller. It handles all requests which are not handled by other controllers.
 * This does not mean, it is guaranteed that request will be handled in a sane way, if request can't be handled then
 * it will be 40xed. Currently, it handles requests which adhere to our slug-docId- these requests differ in method types and query params.
 * Please note that, it also handles valid legacy urls.
 */
public class DocumentController extends AbstractMantleEndpointController {
	private final Pattern slugDocIdRegExPattern = Pattern.compile("/[^/]+\\-[0-9]+");
	private MantleRequestHandlerMethods handlerMethods;
	private final String blank = "";

	public DocumentController(MantleRequestHandlerMethods handlerMethods) {
		this.handlerMethods = handlerMethods;
	}

	/**
	 * Apple news and facebook instant articles are matching against slug-docId regEx
	 * and further they use query params to finalize routing. This could have been
	 * done in a better way but we don't want to remove this now because these urls
	 * could have been already bookmarked in outside world. Ideally we should not
	 * have any other cases like these in future but lets avoid putting one more
	 * condition below before giving detailed thought.
	 * https://dotdash.slack.com/archives/C2MLDF9NX/p1560201039028100
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RequestContext requestContext = RequestContext.get(request);
		String queryString = request.getQueryString();

		boolean isCanonicalPath = slugDocIdRegExPattern.matcher(request.getRequestURI()).matches();
		boolean isFacebookInstantInQueryParams = isCanonicalPath
				&& blank.equals(request.getParameter("facebookInstant"));
		boolean isAppleNewsInQueryParams = isCanonicalPath && !isFacebookInstantInQueryParams && queryString != null
				&& queryString.contains("appleNews");

		if (isAppleNewsInQueryParams) {
			response.setHeader("Content-Type", "text/json");
			response.setHeader("Cache-Control", "max-age=1800, private");
			handlerMethods.renderAppleNewsTemplate(requestContext, request, response);
		} else if (isFacebookInstantInQueryParams) {
			response.setHeader("Content-Type", "text/html");
			response.setHeader("Cache-Control", "max-age=1800, private");
			handlerMethods.renderFacebookInstantArticleTemplate(requestContext, request, response);
		} else if (isDocIdInVerticalUrlData(requestContext)) {
			response.setHeader("Content-Type", "text/html");
			response.setHeader("Cache-Control", "max-age=1800, private");
			addHeadersToResponse(response, responseHeadersForGet());
			handlerMethods.renderDocumentTemplate(requestContext, request, response);
		} else {
			throw new GlobeNotFoundException("Not found in DocumentContoller.doGet");
		}
	}

	// print template and deferred requests
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RequestContext requestContext = RequestContext.get(request);
		if (blank.equals(request.getParameter("print"))) {
			addHeadersToResponse(response, responseHeadersForPost());
			handlerMethods.renderPrintTemplate(requestContext, request, response);
		} else if (isDocIdInVerticalUrlData(requestContext)) {// deferred requests
			/* ContentType is set on the response later, but when the response writer is opened,
			 * the character encoding is locked and can not be changed. An incorrect encoding
			 * results in some characters appearing as question marks. Setting the content type
			 * here allows the proper encoding to be used.
			 */
			response.setHeader("Content-Type", "application/json");
			handlerMethods.renderDocumentTemplate(requestContext, request, response);
		} else {
			render403(request, response);
		}
	}

	private boolean isDocIdInVerticalUrlData(RequestContext requestContext) {
		UrlData urlData = requestContext.getUrlData();
		return (urlData instanceof VerticalUrlData && ((VerticalUrlData) urlData).getDocId() != null);
	}

	protected Map<String, String> responseHeadersForGet() {
		return responseHeaders();
	}

	protected Map<String, String> responseHeadersForPost() {
		return responseHeaders();
	}

	private Map<String, String> responseHeaders() {
		Map<String, String> headers = new HashMap<>();
		headers.put("Cache-Control", "max-age=1800, private");
		headers.put("Content-Type", "text/html");
		return headers;
	}

	@Override
	public String getPath() {
		return "/*";
	}
}
