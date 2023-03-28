package com.about.mantle.handlers.methods;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.startsWithAny;

import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.nio.ByteBuffer;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.exception.GlobeNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpContent;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.CachedContentFactory;
import org.eclipse.jetty.util.BufferUtil;

import com.about.globe.core.app.GlobeExternalConfigKeys;
import com.about.globe.core.definition.resource.aggregate.AggregateKeyGenerator;
import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.module.Module;
import com.about.globe.core.module.ModuleEntry;
import com.about.hippodrome.config.CommonPropertyFactory;
import com.about.hippodrome.util.projectinfo.ProjectInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import com.netflix.archaius.api.PropertyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO poor open/closed principle here.  What are we expecting verts to override?
public class MantleResourceHandlerMethods {
	private static final Logger logger = LoggerFactory.getLogger(MantleResourceHandlerMethods.class);

	// TODO: Should this be injected via spring ?
	private static final PropertyFactory PROPERTY_FACTORY = CommonPropertyFactory.INSTANCE.get();
	private static final String MAX_AGE_ONE_YEAR = "max-age=31556926";
	private static final String MAX_AGE_ONE_DAY = "max-age=86400";

	private final AggregateKeyGenerator keyGenerator;
	private final ProjectInfo projectInfo;
	private final Module module;
	private final CachedContentFactory cacheContentFactory;
	private final Boolean cacheEnabled;
	private final int MAX_CACHE_FILE_SIZE = 128 * 1024 * 1024;

	private static final Map<String, String> CONTENT_TYPES = new ImmutableMap.Builder<String, String>()
			.put("css", "text/css").put("eot", "application/vnd.ms-fontobject").put("gif", "image/gif")
			.put("jpg", "image/jpeg").put("js", "application/javascript").put("otf", "application/font-sfnt")
			.put("pdf", "application/pdf").put("png", "image/png").put("svg", "image/svg+xml")
			.put("ttf", "application/font-sfnt").put("woff", "application/font-woff")
			.put("woff2", "application/font-woff2").put("ico", "image/x-icon").build();

	public MantleResourceHandlerMethods(AggregateKeyGenerator keyGenerator, ProjectInfo projectInfo, Module module,
			CachedContentFactory cacheContentFactory) {
		this.keyGenerator = keyGenerator;
		this.cacheEnabled = PROPERTY_FACTORY.getProperty(GlobeExternalConfigKeys.RESOURCES_CACHE_ENABLED)
				.asBoolean(null).get();
		this.projectInfo = projectInfo;
		this.module = module;
		this.cacheContentFactory = cacheContentFactory;
	}

	public void handleResourceRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setCharacterEncoding("UTF-8");

		String requestUri = request.getRequestURI();
		String extension = requestUri.substring(requestUri.lastIndexOf(".") + 1);

		if (CONTENT_TYPES.containsKey(extension)) {
			response.setContentType(CONTENT_TYPES.get(extension));
		}

		boolean isConcatedResource = isConcatenatedResourceRequest(request);
		String resourcePath = getResourcePath(requestUri);

		// If request URI version is later than the server version we want to set it to no-cache. For deployments, the load balancer will
		// replace the nodes one by one. New requests can hit an old node with a request for newer version of a resource
		// which the old node wouldn't have, so we don't want to tell the browser to cache it
		if (isLaterProdVersion(requestUri, resourcePath, isConcatedResource)) {
			response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
			// Otherwise, cache for successful and initial responses
		} else if (HttpStatus.isSuccess(response.getStatus()) || 0 == response.getStatus()) {
			// Favicon should not be given a cache expiry of 1 year
			boolean isFavicon = requestUri != null && requestUri.endsWith("/favicon.ico");
			response.setHeader(HttpHeaders.CACHE_CONTROL,
					cacheEnabled ? (isFavicon ? MAX_AGE_ONE_DAY : MAX_AGE_ONE_YEAR) : "no-cache");
		} else {
			response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
		}

		if (isConcatedResource) {
			serveConcatenatedResource(response, request);
		} else {
			serveResource(response, resourcePath, new ResourceNotFoundHandler() {
				@Override
				public void handle(HttpServletResponse response) {
					serveResourceNotFound(response);
				}
			});
		}

	}

	private void serveConcatenatedResource(HttpServletResponse response, HttpServletRequest request)
			throws ServletException, IOException {

		final String requestUri = request.getRequestURI();
		int lastSlash = requestUri.lastIndexOf('/');

		String key = null;
		String extension = null;
		try {
			key = requestUri.substring(lastSlash + 1, requestUri.indexOf('.', lastSlash));
			final String type = requestUri.substring(requestUri.lastIndexOf('.') + 1);
			extension = requestUri.endsWith(".min." + type) ? ".min." + type : "." + type;
		} catch (StringIndexOutOfBoundsException e) {
			logger.error("Unable to parse resource URI " + requestUri, e);
		}

		Iterable<String> paths = keyGenerator.paths(key == null ? "" : key);
		if (paths == null) {
			serveResourceNotFound(response);
			return;
		}

		for (final String path : paths) {
			serveResource(response, path + extension, ResourceNotFoundHandler.NO_OP);
			// Future proofing for possible source maps in the future or that we miss them when
			// importing 3rd party code
			response.getOutputStream().write('\n');
		}
	}

	protected boolean isConcatenatedResourceRequest(HttpServletRequest request) {
		return request.getRequestURI().contains("/cache/");
	}

	private void serveResourceNotFound(HttpServletResponse response) {
		response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
		throw new GlobeNotFoundException("Could not find requested resource");
	}

	private boolean isLaterProdVersion(String requestUri, String resourcePath, boolean isConcatedResource) {
		String[] pathStems = split(requestUri, "/", 3);
		if (pathStems.length < 3)
			return false;

		ProjectInfo resolvedProjectInfo = getResolvedProjectInfo(isConcatedResource, resourcePath);

		if (resolvedProjectInfo != null) {
			String reqVersionStr = pathStems[1];
			String resolvedProjectVersion = resolvedProjectInfo.getVersion();

			try {
				if (!"DEVELOPMENT".equals(resolvedProjectVersion) && !resolvedProjectVersion.equals(reqVersionStr))
					return ModuleDescriptor.Version.parse(reqVersionStr).compareTo(ModuleDescriptor.Version.parse(resolvedProjectVersion)) > 0;
			} catch (IllegalArgumentException e) {
				logger.warn("Unable to parse version {}", e.getMessage());
			}
		}

		return false;
	}

	private ProjectInfo getResolvedProjectInfo(boolean isConcatedResource, String resourcePath) {
		ProjectInfo resolvedProjectInfo = null;
		if (isConcatedResource) {
			resolvedProjectInfo = this.projectInfo;
		} else {
			ModuleEntry entry = module.resolveEntry(resourcePath);
			if (entry != null) {
				resolvedProjectInfo = entry.getModule().getProjectInfo();
			}
		}

		return resolvedProjectInfo;
	}

	private void serveResource(HttpServletResponse response, String resourcePath, ResourceNotFoundHandler handler)
			throws ServletException, IOException {

		HttpContent httpContent = null;

		try {
			httpContent = lookupHttpContent(resourcePath);
			if (httpContent == null) {
				handler.handle(response);
				return;
			}

			ServletOutputStream outputStream = response.getOutputStream();
			// httpOutput.sendContent is changed in jetty 9.4 and commits response on first
			// call
			// which leads subsequent resource failed to serve. Removing
			// httpOutput.sendContent and
			// serving it via following (else condition earlier) works without any problem.
			// TODO: will have to see any other side effect in regression and PE testing
			ByteBuffer indirectBuffer = httpContent.getIndirectBuffer();
			if (indirectBuffer == null) {
				// not sure what causes this but this error is better than an NPE.
				// Perhaps it should call `handler.handle(response)` instead?
				throw new GlobeException("Could not serve `" + resourcePath + "`.  HTTP Content's buffer " + "is null");
			}
			BufferUtil.writeTo(indirectBuffer, outputStream);
		} finally {
			if (httpContent != null)
				httpContent.release();
		}
	}

	protected HttpContent lookupHttpContent(RequestContext requestContext) throws ServletException {
		return lookupHttpContent(requestContext.getRequestUri());
	}

	protected HttpContent lookupHttpContent(String resourcePath) throws ServletException {
		if (isBlank(resourcePath))
			return null;

		try {
			return this.cacheContentFactory.getContent(resourcePath, MAX_CACHE_FILE_SIZE);
		} catch (IOException e) {
			throw new ServletException("Could not lookup resource " + resourcePath, e);
		}
	}

	protected String getResourcePath(String requestUri) {

		String[] pathParts = StringUtils.split(requestUri, "/", 3);
		if (pathParts.length != 3)
			return null;

		String resourcePath = pathParts[2];

		if (startsWithAny(resourcePath, "static", "static/")) {
			// ex. /icons/2.15.0-SNAPSHOT/icons/money/mstile-144x144.png ->
			// /icons/money/mstile-144x144.png
			// ex. /js/2.15.2/static/js/default/ie.min.js -> /static/js/default/ie.min.js
			return "/" + resourcePath;
		}

		// ex. /static/2.15.0-SNAPSHOT/icons/money/mstile-144x144.png ->
		// /static/icons/money/mstile-144x144.png
		return "/static/" + resourcePath;
	}

	private static interface ResourceNotFoundHandler {

		static final ResourceNotFoundHandler NO_OP = new ResourceNotFoundHandler() {
			@Override
			public void handle(HttpServletResponse response) {
				// Do nothing
			}
		};

		void handle(HttpServletResponse response) throws ServletException, IOException;
	}

}
