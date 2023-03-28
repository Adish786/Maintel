package com.about.mantle.logging;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.about.globe.core.definition.resource.DSSDeviceCategoryMapping;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.processor.ModelResult;
import com.about.globe.core.web.filter.AccessLogInjector;
import com.about.mantle.model.extended.TaxeneNodeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;

public class JsonFormatAccessLogInjector implements AccessLogInjector {
	private static final Logger logger = LoggerFactory.getLogger(JsonFormatAccessLogInjector.class);

	@Override
	public void clear() {
		MDC.clear();
	}

	@Override
	public void write(RequestContext requestContext, HttpServletResponse response) {
		if (requestContext != null) {
			BaseDocumentEx document = getDocument(requestContext);

			MDC.put("documentId", getDocumentId(document));
			MDC.put("documentType", getDocumentType(document));
			MDC.put("documentTypeId", getDocumentTypeId(document));
			MDC.put("actorId", getActorId(document));
			MDC.put("actorGid", getActorGid(document));
			MDC.put("lastEditingAuthorId", getLastEditingAuthorId(document));
			MDC.put("lastEditingUserId", getLastEditingUserId(document));

			TaxeneNodeEx taxeneNode = getTaxonomyBreadcrumb(requestContext);
			MDC.put("taxonomy", getTaxonomyBreadcrumbObject(taxeneNode));

			MDC.put("renderTemplate", getRenderTemplate(requestContext));
			MDC.put("userAgent", getUserAgent(requestContext));
			MDC.put("deviceCategory", getDeviceCategory(requestContext));
			MDC.put("isPageview", Boolean.toString(getIsPageview(requestContext, response)));

			MDC.put("requestId", getRequestId(requestContext));
			MDC.put("sessionId", getSessionId(requestContext));
			MDC.put("userId", getUserId(requestContext));
			MDC.put("muid", getMuid(requestContext));

			MDC.put("requestTimestamp", getRequestTimestamp(requestContext));

			MDC.put("requestUrlComponents", getUrlObject(requestContext));

			MDC.put("remoteIp", getRemoteIp(requestContext));

			String refererUrl = getReferrerUrl(requestContext);
			MDC.put("refererUrl", escapeAndQuoteString((refererUrl)));
			MDC.put("refererUrlComponents", getUriObject(refererUrl));
		}
	}

	@Override
	public void write(HttpServletRequest request) {
		MDC.put("isPageview", "false");

		MDC.put("requestTimestamp", Long.toString(new Date().getTime()));

		MDC.put("requestUrlComponents", getUrlObject(request));

		MDC.put("remoteIp", quoteString(request.getRemoteAddr()));
	}

	protected BaseDocumentEx getDocument(RequestContext requestContext) {
		ModelResult result = requestContext.getModel("DOCUMENT");
		return getModelValue(result, BaseDocumentEx.class);
	}

	protected String getActorId(BaseDocumentEx document) {
		if ((document != null) && (document.getActorId() != null)) return quoteString(document.getActorId().toString());
		return null;
	}

	protected String getActorGid(BaseDocumentEx document) {
		if ((document != null) && (document.getActorGid() != null)) return quoteString(document.getActorGid());
		return null;
	}

	protected String getLastEditingAuthorId(BaseDocumentEx document) {
		if ((document != null) && (document.getLastEditingAuthorId() != null))
			return quoteString(document.getLastEditingAuthorId());
		return null;
	}

	protected String getLastEditingUserId(BaseDocumentEx document) {
		if ((document != null) && (document.getLastEditingUserId() != null))
			return quoteString(document.getLastEditingUserId());
		return null;
	}

	protected String getDocumentId(BaseDocumentEx document) {
		if ((document != null) && (document.getDocumentId() != null))
			return quoteString(document.getDocumentId().toString());
		return null;
	}

	protected String getDocumentType(BaseDocumentEx document) {
		if ((document != null) && (document.getTemplateType() != null))
			return quoteString(document.getTemplateType().toString());
		return null;
	}

	protected String getDocumentTypeId(BaseDocumentEx document) {
		if ((document != null) && (document.getTemplateType() != null))
			return Integer.toString(document.getTemplateType().getTemplateId());
		return null;
	}

	protected TaxeneNodeEx getTaxonomyBreadcrumb(RequestContext requestContext) {
		return getModelValue(requestContext.getModel("breadcrumb"), TaxeneNodeEx.class);
	}

	protected String getTaxonomyBreadcrumbObject(TaxeneNodeEx taxeneNode) {
		if (taxeneNode != null) {
			List<String> elements = new ArrayList<>();
			getTaxonomyBreadcrumbObject(taxeneNode, elements);
			return "{\"path1\":[" + StringUtils.join(elements, ',') + "]}";
		}
		return null;
	}

	protected void getTaxonomyBreadcrumbObject(TaxeneNodeEx taxeneNode, List<String> elements) {
		if (taxeneNode.isTaxonomyDocument()) {
			String docId = (taxeneNode.getDocId() != null) ? taxeneNode.getDocId().toString() : null;
			String text = null;
			if (taxeneNode.getDocument() != null) {
				text = taxeneNode.getDocument().getShortHeading();
			}
			elements.add(0, "{\"docId\":" + quoteString(docId) + ",\"text\":" + escapeAndQuoteString(text) + "}");
		}
		if (taxeneNode.getPrimaryParent() != null) getTaxonomyBreadcrumbObject(taxeneNode.getPrimaryParent(), elements);
	}

	protected String getRenderTemplate(RequestContext requestContext) {
		return quoteString(requestContext.getTemplateName());
	}

	protected String getUserAgent(RequestContext requestContext) {
		return escapeAndQuoteString(requestContext.getHeaders().getUserAgent());
	}

	protected String getDeviceCategory(RequestContext requestContext) {
		if (requestContext.getUserAgent() != null) {
			return quoteString(DSSDeviceCategoryMapping.get(requestContext.getUserAgent().getDeviceCategory()));
		}
		return null;
	}

	protected boolean getIsPageview(RequestContext requestContext, HttpServletResponse response) {
		return response.getStatus() != 200 ? false : requestContext.isPageview();
	}

	protected String getRequestId(RequestContext requestContext) {
		return escapeAndQuoteString(requestContext.getRequestId());
	}

	protected String getSessionId(RequestContext requestContext) {
		return escapeAndQuoteString(requestContext.getSessionId());
	}

	protected String getUserId(RequestContext requestContext) {
		return escapeAndQuoteString(requestContext.getUserId());
	}

	protected String getMuid(RequestContext requestContext) {
		return escapeAndQuoteString(requestContext.getMuid());
	}

	protected String getRequestTimestamp(RequestContext requestContext) {
		return Long.toString(requestContext.getRequestTimestamp());
	}

	protected String getUrlObject(RequestContext requestContext) {
		return toUriObject(requestContext.getUrlData().getScheme(), requestContext.getUrlData().getHost(),
				requestContext.getUrlData().getPath(), requestContext.getUrlData().getQuery(),
				requestContext.getUrlData().getFragment());
	}

	protected String getUrlObject(HttpServletRequest request) {
		String url = request.getRequestURL().toString()
				+ (request.getQueryString() == null ? "" : "?" + request.getQueryString());
		return getUriObject(url);
	}

	protected String getReferrerUrl(RequestContext requestContext) {
		return requestContext.getHeaders().getReferer();
	}

	protected String getRemoteIp(RequestContext requestContext) {
		if (requestContext.getHeaders() != null) {
			return quoteString(requestContext.getHeaders().getRemoteIp());
		}
		return null;
	}

	protected String getUriObject(String url) {
		URI uri = null;
		if (StringUtils.isNotEmpty(url)) {
			try {
				uri = new URI(url);
			} catch (URISyntaxException e) {
				logger.warn("Error parsing referer url {}", url);
			}
		}
		return toUriObject(uri);
	}

	protected String toUriObject(URI uri) {
		return toUriObject((uri != null) ? uri.getScheme() : null, (uri != null) ? uri.getHost() : null,
				(uri != null) ? uri.getPath() : null, (uri != null) ? uri.getQuery() : null,
				(uri != null) ? uri.getFragment() : null);
	}

	protected String toUriObject(String scheme, String host, String path, String query, String fragment) {
		return "{\"domain\":" + quoteString(host) + ",\"frag\":" + escapeAndQuoteString(fragment) + ",\"path\":"
				+ escapeAndQuoteString(path) + ",\"protocol\":" + quoteString(scheme) + ", \"qString\":"
				+ escapeAndQuoteString(query) + "}";
	}

	protected String quoteString(String value) {
		if (value == null) return null;
		return "\"" + value + "\"";
	}

	protected String escapeAndQuoteString(String value) {
		return quoteString(StringEscapeUtils.escapeJson(value));
	}

	protected <T> T getModelValue(ModelResult result, Class<T> bindToTarget) {
		if ((result != null) && (result.getValue() != null) && (bindToTarget.isInstance(result.getValue()))) {
			return bindToTarget.cast(result.getValue());
		}
		return null;
	}
}
