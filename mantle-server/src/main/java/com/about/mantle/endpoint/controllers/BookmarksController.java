package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.mantle.model.extended.bookmarks.BookmarksRequestContext;
import com.about.mantle.model.services.bookmarks.BookmarksService;

/**
 * Globe's endpoints for integrating with Bookmarks service API.
 * Intentionally limited to HTTP POST method to piggyback off CSRF token verification
 * and to limit visibility of sensitive request parameters like the access token to the
 * bookmarks API.
 */
public class BookmarksController extends AbstractMantleEndpointController {

	private static Logger logger = LoggerFactory.getLogger(BookmarksController.class);

	private final BookmarksService bookmarksService;

	public BookmarksController(BookmarksService bookmarksService) {
		this.bookmarksService = bookmarksService;
	}

	@Override
	public String getPath() {
		return "/bookmarks/{operation:get|save|delete}";
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RequestContext requestContext = RequestContext.get(request);
		Operation operation = null;
		BookmarksRequestContext bookmarksReqCtx = null;

		try {
			operation = parseRequestOperation(request);

			bookmarksReqCtx = new BookmarksRequestContext.Builder()
					.accessToken(requestContext.getParameterSingle("accessToken"))
					.docId(requestContext.getParameterSingle("docId"))
					.hashId(requestContext.getHashId())
					.build();
		} catch (GlobeException e) {
			logger.debug("bad request", e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		if (bookmarksReqCtx != null && operation != null) {
			handleRequest(bookmarksReqCtx, operation, response);
		}

		response.flushBuffer();
	}

	private void handleRequest(BookmarksRequestContext reqCtx, Operation operation, HttpServletResponse response) {
		if (bookmarksService == null) {
			throw new GlobeException("Bookmarks service is not configured for the application.");
		}
		switch (operation) {
			case GET:
				response.setStatus(bookmarksService.isBookmarked(reqCtx)
						? HttpServletResponse.SC_OK
						: HttpServletResponse.SC_NOT_FOUND);
				break;
			case SAVE:
				response.setStatus(bookmarksService.saveBookmark(reqCtx)
						? HttpServletResponse.SC_OK
						: HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				break;
			case DELETE:
				response.setStatus(bookmarksService.deleteBookmark(reqCtx)
						? HttpServletResponse.SC_OK
						: HttpServletResponse.SC_NOT_FOUND);
				break;
		}
	}

}
