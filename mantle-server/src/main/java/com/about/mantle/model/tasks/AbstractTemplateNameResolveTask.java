package com.about.mantle.model.tasks;

import org.eclipse.jgit.util.StringUtils;

import com.about.globe.core.exception.GlobeNotFoundException;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.hippodrome.url.UrlData;
import com.about.mantle.model.services.DocumentService;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.State;

@Tasks
public abstract class AbstractTemplateNameResolveTask implements TemplateNameResolveTask {
	private final CacheTemplate<String> routingCache;
	protected final DocumentService documentService;


	public AbstractTemplateNameResolveTask(DocumentService documentService, CacheTemplate<String> routingCache) {
		this.routingCache = routingCache;
		this.documentService = documentService;
	}

	// AMP is no longer supported, ignoredAmpTemplateNameCache should be removed when this method is no longer used by any other repos
	@Deprecated
	public AbstractTemplateNameResolveTask(DocumentService documentService, CacheTemplate<String> routingCache, CacheTemplate<String> ignoredAmpTemplateNameCache) {
		this.routingCache = routingCache;
		this.documentService = documentService;
	}

	@Override
	@Task(name = "getDefaultPresentationTemplateName")
	@TimedComponent(category = "task")
	public String getTemplateName(@RequestContextTaskParameter RequestContext requestContext) {
		if (!(requestContext.getUrlData() instanceof VerticalUrlData)) {
			return null;
		}
		VerticalUrlData verticalUrlData = (VerticalUrlData) requestContext.getUrlData();
		
		if (verticalUrlData.getDocId() == null) {
			return null;
		}

		if (requestContext.getRequestUri().equals("/404")) {
			return null;
		}

		if (DocumentTask.getStateFromRequestParameters(requestContext.getParameters()) != State.ACTIVE) {
			// no cache, always refetch
			return getTemplate(verticalUrlData, requestContext);
		}

		return routingCache.get(verticalUrlData.getDocId(), () -> {
			return getTemplate(verticalUrlData, requestContext);
		});
	}

	/**
	 *
	 * @param requestContext
	 * @param isLikelyLegacyDoc
	 * @return
	 */
	@Override
	@Task(name = "getDefaultPresentationTemplateName")
	@TimedComponent(category = "task")
	public String getTemplateName(@RequestContextTaskParameter RequestContext requestContext,
								  @TaskParameter(name = "isLikelyLegacyDoc") Boolean isLikelyLegacyDoc) {

		/*
		 * Note: TL;DR Why throw a GlobeNotFoundException here? We know which url
		 * belongs to legacy url map and we can render document templates accordingly.
		 * But what about url that may look like a legacy url and corresponding document
		 * does not exist in Selene? With previous code (getTemplateName(RequestContext)
		 * method), this will result in 500 error instead of rendering an expected 404
		 * (page not found) template. Solving this problem by forcing a
		 * GlobeNotFoundException here should trigger rendering of 404 template. That's
		 * fine. However, this additional code can also get called if there is an
		 * upstream routine that relies on rendering of a valid template for a certain
		 * request. For example rendering 404 template for "/404" request when
		 * ResourceServelet tries to build request contexts by adding filters for static
		 * resources and those requests are intercepted here and results in
		 * GlobeNotFoundException as a static resource will not have a docId attached to
		 * its urlData. In order to solve this secondary problem, we can introduce a
		 * flag property in "documentTemplate" action task definition and overload
		 * default behavior of "getTemplateName()" method. Doing so will explicitly
		 * invoke this overloaded method for only a certain requests that are mapped in
		 * the aforementioned "documentTemplate" action. Everything else (e.g. upstream
		 * code like ProctorGlobeTestFramework.getInputContext()#89) goes through the
		 * default "getTemplateName()" method and not worry about
		 * GlobeNotFoundException.
		 */
		if (isLikelyLegacyDoc) {
			UrlData urlData = requestContext.getUrlData();
			if (urlData instanceof VerticalUrlData && (((VerticalUrlData) urlData).getDocId() == null)) {
				throw new GlobeNotFoundException("Page not found");
			}
		}

		return this.getTemplateName(requestContext);

	}

	private String getTemplate(VerticalUrlData verticalUrlData, RequestContext requestContext) {
		String key = verticalUrlData.getDocId().toString();
		BaseDocumentEx document = documentService.getDocument(key, requestContext.getParameters());;

		if ((document == null) || !verticalUrlData.getDocId().equals(document.getDocumentId())) {
			return null;
		}

		return getTemplateName(document);
	}

	// AMP is no longer supported
	@Deprecated
	@Override
	public String getAmpTemplateName(BaseDocumentEx document, String templateName) {
		return null;
	}

}
