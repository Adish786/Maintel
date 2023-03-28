package com.about.mantle.handlers.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.about.globe.core.Globe;
import com.about.globe.core.componentinclusion.ComponentNode;
import com.about.globe.core.definition.action.DebugHttpServletRequest;
import com.about.globe.core.definition.common.Property;
import com.about.globe.core.definition.common.TaskModel;
import com.about.globe.core.definition.component.ResourceHandle;
import com.about.globe.core.definition.resource.ResolvedResource;
import com.about.globe.core.definition.resource.ResourceAggregator;
import com.about.globe.core.definition.resource.ResourceResolver;
import com.about.globe.core.definition.template.RenderManifest;
import com.about.globe.core.definition.template.TemplateComponent;
import com.about.globe.core.definition.template.TemplateComponent.Builder;
import com.about.globe.core.definition.template.TemplateComponentResolver;
import com.about.globe.core.exception.GlobeDependencyUnsatisfiedException;
import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.exception.GlobeInvalidConfigurationException;
import com.about.globe.core.exception.GlobeInvalidDestinationException;
import com.about.globe.core.exception.GlobeRenderException;
import com.about.globe.core.exception.GlobeTaskHaltException;
import com.about.globe.core.exception.GlobeTemplateNotFoundException;
import com.about.globe.core.http.AccountInfo;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.RequestContextSource;
import com.about.globe.core.logging.ContextBasedTracer;
import com.about.globe.core.logging.GlobeContextBasedTracerHolder;
import com.about.globe.core.model.EnvironmentConfig;
import com.about.globe.core.render.RenderingEngine;
import com.about.globe.core.task.TaskGroup;
import com.about.globe.core.task.TaskMethod;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.processor.DependencyRequestManager;
import com.about.globe.core.task.registry.TasksRegistry;
import com.about.globe.core.util.MethodParameterUtil;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.hippodrome.jaxrs.providers.StandardObjectMapperProvider;
import com.about.hippodrome.url.UrlData;
import com.about.hippodrome.url.UrlDataFactory;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.app.LegacyUrlMap;
import com.about.mantle.cache.clearance.CacheClearanceCandidateRepo;
import com.about.mantle.cache.clearance.CacheClearanceRequest;
import com.about.mantle.cache.clearance.CacheClearanceThreadLocalUtils;
import com.about.mantle.definition.action.DebugActionTasks;
import com.about.mantle.definition.action.NewsletterSignupService;
import com.about.mantle.endpoint.controllers.AbstractMantleEndpointController;
import com.about.mantle.model.debug.TaskOutput;
import com.about.mantle.model.debug.TaskOutput.TaskParameterOutput;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.resound.ResoundReactResponse;
import com.about.mantle.model.extended.resound.MantleResoundReviewStatusWrapper;
import com.about.mantle.model.extended.resound.MantleResoundReviewWriteWrapper;
import com.about.mantle.model.services.DocumentService;
import com.about.mantle.model.services.auth.DDMAccountAuthService;
import com.about.mantle.model.services.resound.ResoundService;
import com.about.mantle.model.tasks.BusinessOwnedVerticalDataTask;
import com.about.mantle.model.tasks.ComplianceTask;
import com.about.mantle.model.tasks.PrintReadyTemplateNameResolveTask;
import com.about.mantle.model.tasks.TemplateNameResolveTask;
import com.about.mantle.model.tasks.UGCFeedbackTask;
import com.about.mantle.model.tasks.UGCRatingsTask;
import com.about.mantle.render.MantleRenderUtils;
import com.about.mantle.spring.interceptor.PageNotFoundHandler;
import com.about.mantle.testing.SomethingMagicalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.MediaType;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

// TODO this class was a stopgap for the Spring Boot migration.  Revisit how Controllers talk to services, ensuring
// clean separation of concerns
// Why final?  Because this is really just a passthru between the controllers and the services.  We don't want
// verts to override it.  That said, this might go away as part of the discussion above.
public final class MantleRequestHandlerMethods {

	public static final Logger logger = LoggerFactory.getLogger(MantleRequestHandlerMethods.class);
	private static final ContextBasedTracer tracer = GlobeContextBasedTracerHolder.getDefault();

	private static final Pattern BACKMATCH_PATTERN = Pattern.compile("\\\\([0-9]+)");

	private final EnvironmentConfig envConfig;
	private final UrlDataFactory urlDataFactory;
	private final ComplianceTask complianceTask;
	private final DependencyRequestManager dependencyRequestManager;
	private final MantleRenderUtils renderUtils;
	private final TasksRegistry tasksRegistry;
	private final PageNotFoundHandler pageNotFoundHandler;
	private final ResourceAggregator resourceAggregator;
	private final CacheTemplate<String> renderCache;
	private final PrintReadyTemplateNameResolveTask printReadyTemplateNameResolveTask;
	private final NewsletterSignupService newsletterSignupService;
	private final TemplateNameResolveTask defaultTemplateNameResolveTask;
	private final UGCRatingsTask ugcRatingsTask;
	private final UGCFeedbackTask ugcFeedbackTask;
	private final DebugActionTasks debugActionTasks;
	private final BusinessOwnedVerticalDataTask businessOwnedVerticalDataTask;
	private final SomethingMagicalService somethingMagicalService;
	private final CommonTemplateNames commonTemplateNames;
	private final RequestContextSource requestContextSource;
	private final MantleLinkHeaderHandlerMethods mantleLinkHeaderHandlerMethods;
	private final DocumentService documentService;
	private final DDMAccountAuthService ddmAccountAuthService;
	private final LegacyUrlMap legacyUrlMap;
	private final ResoundService resoundService;
	private final List<String> excludedTemplatesFromLinkHeaders;

	public MantleRequestHandlerMethods(EnvironmentConfig envConfig, UrlDataFactory urlDataFactory, DependencyRequestManager dependencyRequestManager, MantleRenderUtils renderUtils,
									   TasksRegistry tasksRegistry, PageNotFoundHandler pageNotFoundHandler,
									   ResourceAggregator resourceAggregator, CacheTemplate<String> renderCache,
									   PrintReadyTemplateNameResolveTask printReadyTemplateNameResolveTask,
									   NewsletterSignupService newsletterSignupService, TemplateNameResolveTask defaultTemplateNameResolveTask,
									   UGCRatingsTask ugcRatingsTask, UGCFeedbackTask ugcFeedbackTask, DebugActionTasks debugActionTasks,
									   BusinessOwnedVerticalDataTask businessOwnedVerticalDataTask,
									   SomethingMagicalService somethingMagicalService, CommonTemplateNames mantleHandlerMethodsSupport,
									   RequestContextSource requestContextSource, ComplianceTask complianceTask,
									   MantleLinkHeaderHandlerMethods mantleLinkHeaderHandlerMethods, DocumentService documentService,
									   DDMAccountAuthService ddmAccountAuthService, LegacyUrlMap legacyUrlMap,ResoundService resoundService) {

		this.envConfig = envConfig;
		this.urlDataFactory = urlDataFactory;
		this.dependencyRequestManager = dependencyRequestManager;
		this.renderUtils = renderUtils;
		this.tasksRegistry = tasksRegistry;
		this.pageNotFoundHandler = pageNotFoundHandler;
		this.resourceAggregator = resourceAggregator;
		this.renderCache = renderCache;
		this.printReadyTemplateNameResolveTask = printReadyTemplateNameResolveTask;
		this.newsletterSignupService = newsletterSignupService;
		this.defaultTemplateNameResolveTask = defaultTemplateNameResolveTask;
		this.ugcRatingsTask = ugcRatingsTask;
		this.ugcFeedbackTask = ugcFeedbackTask;
		this.debugActionTasks = debugActionTasks;
		this.somethingMagicalService = somethingMagicalService;
		this.commonTemplateNames = mantleHandlerMethodsSupport;
		this.requestContextSource = requestContextSource;
		this.businessOwnedVerticalDataTask = businessOwnedVerticalDataTask;
		this.complianceTask = complianceTask;
		this.mantleLinkHeaderHandlerMethods = mantleLinkHeaderHandlerMethods;
		this.documentService = documentService;
		this.ddmAccountAuthService = ddmAccountAuthService;
		this.legacyUrlMap = legacyUrlMap;
		this.resoundService = resoundService;
		this.excludedTemplatesFromLinkHeaders = excludedTemplatesFromLinkHeaders();
	}

	/**
	 * This task will return a json representation of a model.
	 *
	 * The modelId parameter needs to correspond to a model task. All the properties
	 * need to be present in the query string. Currently only supports model tasks
	 * that can have their properties be declared in a query string
	 *
	 ** @param request
	 * @param response
	 * @throws IOException
	 * @throws GlobeException
	 */
	public void serveModel(RequestContext requestContext, HttpServletRequest request, HttpServletResponse response)
			throws IOException, GlobeException {
		
		response.setContentType("application/json");

		TaskModel m = buildModel(requestContext, tasksRegistry);
		if (m == null) {
			throw new GlobeException("TaskModel could not be created for requestContext: " + requestContext.toString(),
					null);
		}

		TaskMethod tm = m.getTaskMethod();
		if (tm == null) {
			throw new GlobeException("TaskMethod not found for requestContext: " + requestContext.toString());
		}

		Object modelResponse = tm.invoke(requestContext, m.getParameterMap(requestContext));
		if (modelResponse == null) {
			return;
		}

		ObjectMapper objectMapper = new StandardObjectMapperProvider().getContext(null);
		ServletOutputStream outputStream = response.getOutputStream();

		String valueJson = objectMapper.writeValueAsString(modelResponse);
		outputStream.write(valueJson.getBytes("UTF-8"));

	}
	
	public void showLoadedConfigs(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response, Map<String, String> configUsageMap) throws IOException, GlobeException {

		//Sort config map by key
		Map<String, String> sortedConfigUsageMap = configUsageMap
				.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		
		ObjectMapper objectMapper = new StandardObjectMapperProvider().getContext(null);
		ServletOutputStream outputStream = response.getOutputStream();
		String valueJson = objectMapper.writeValueAsString(sortedConfigUsageMap);
		response.setContentType("application/json");
		outputStream.write(valueJson.getBytes("UTF-8"));
	}

	/**
	 * This task will return the requested resource.
	 *
	 * @param path
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws GlobeException
	 */
	public void serveResource(String path, RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) throws IOException, GlobeException {

		if (path == null) {
			throw new GlobeException("Resource path should not be null");
		}

		ServletOutputStream outputStream = response.getOutputStream();
		InputStream inputStream = getClass().getResourceAsStream(path);

		if (inputStream == null) {
			throw new GlobeException("Resource cannot be served for path: " + path);
		}

		IOUtils.copy(inputStream, outputStream);

	}

	protected TaskModel buildModel(RequestContext requestContext, TasksRegistry tasksRegistry) {
		TaskModel.Builder builder = new TaskModel.Builder();
		Map<String, String[]> params = requestContext.getParameters();
		if (params.get("modelId") == null) {
			return null;
		}
		builder.name(params.get("modelId")[0]);
		for (String key : params.keySet()) {
			if (!key.equals("modelId")) {
				builder.dependency(Property.builder().name(key).value(params.get(key)[0]).type(String.class).build());
			}
		}
		builder.tasksRegistry(tasksRegistry);
		return builder.build();
	}

	public void cachedRenderTemplate(String templateName, RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) throws GlobeException, ServletException, IOException {

		try {
			String output = renderCache.get(new RenderCacheKey(requestContext.getUrlData().getUrl(), templateName),
					() -> {
						StringWriter outputWriter = new StringWriter();
						try {
							renderTemplate(templateName, true, requestContext, request, response, outputWriter);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						return outputWriter.toString();
					});
			response.getWriter().write(output);
		} catch (RuntimeException e) {
			if (e.getCause() != null)
				throw e;
			else
				throw e;
		}
	}

	public void renderTemplate(String templateName, RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) throws GlobeException {
		try {
			renderTemplate(templateName, false, requestContext, request, response, response.getWriter());
		} catch (IOException e) {
			throw new GlobeRenderException(
					"Error rendering template component [" + templateName + "] for request " + requestContext, e);
		}
	}

	public void renderTemplate(String templateName, Boolean isPvExcluded, RequestContext requestContext,
			HttpServletRequest request, HttpServletResponse response) throws GlobeException {
		try {
			renderTemplate(templateName, isPvExcluded, requestContext, request, response, response.getWriter());
		} catch (IOException e) {
			throw new GlobeRenderException(
					"Error rendering template component [" + templateName + "] for request " + requestContext, e);
		}
	}

	protected void renderTemplate(String templateName, boolean isPvExcluded, RequestContext requestContext,
			HttpServletRequest request, HttpServletResponse response, Writer writer) throws GlobeException {

		if (templateName == null) {
			handleNullTemplate(requestContext, request, response);
			return;
		}

		tracer.startProcess("renderTemplate", "templateName", templateName);

		TemplateComponentResolver templateComponentResolver = requestContext.getGlobe().getTemplateComponentResolver();
		// tracer.trace("Using template resolver: {}",
		// tracer.asJson(templateComponentResolver));
		TemplateComponent templateComponent = templateComponentResolver.resolve(templateName, requestContext);
		if (templateComponent == null) {
			tracer.endProcess();
			throw new GlobeTemplateNotFoundException(
					"Could not resolve template component [" + templateName + "] for request " + requestContext);
		}
		requestContext = requestContext.with().setTemplateComponent(templateComponent)
				.setPageview(determineIsPageview(requestContext, isPvExcluded)).build(request);
		render(templateComponent, requestContext, response, writer);

		tracer.endProcess();
	}

	/**
	 * Handles documents with null templates by either throwing a 529 exception or forwarding the request as a 404
	 *
	 * @param requestContext
	 * @param request
	 * @param response
	 * @throws GlobeException
	 */
	private void handleNullTemplate(RequestContext requestContext, HttpServletRequest request,
									HttpServletResponse response) throws GlobeException {
		Long docId = null;
		if ((requestContext.getUrlData() instanceof VerticalUrlData)) {
			docId = ((VerticalUrlData) requestContext.getUrlData()).getDocId();
		}

		if (docId != null) {
			List<TemplateTypeEx> templatesToForwardAsNotFound = List.of(TemplateTypeEx.PROGRAMMEDSUMMARY, TemplateTypeEx.JWPLAYERVIDEO);
			List<TemplateTypeEx> templatesToForwardAsPageGone = List.of(TemplateTypeEx.AMAZONOSP, TemplateTypeEx.BRIGHTCOVEVIDEO);

			BaseDocumentEx document = documentService.getDocument(docId.toString(), requestContext.getParameters());

			if (document != null) {
				try {
					// We want the document to return a 4xx response instead of a 529
					if (templatesToForwardAsNotFound.contains(document.getTemplateType())) {
						forwardToPageNotFound(request, response, requestContext);
						return;
					} else if (templatesToForwardAsPageGone.contains(document.getTemplateType())) {
						renderPageGone(requestContext, request, response);
						return;
					}
				} catch (IOException | ServletException e) {
					throw new GlobeTemplateNotFoundException("Error returning 4xx response for document with ID " + docId + ".");
				}
			}
		}

		/* Null template names are a more likely error case than an unresolved template
		 * component and should be given a more user-friendly error message.
		 */
		throw new GlobeTemplateNotFoundException("Document " + docId + " is not supported by any template on this vertical.");
	}

	public void renderPrintTemplate(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) {
		String printTemplateName = printReadyTemplateNameResolveTask.getTemplateName(requestContext);
		renderTemplate(printTemplateName, requestContext, request, response);
	}

	public void handleNewsletterSubmit(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) throws IOException, URISyntaxException {
		newsletterSignupService.signupNewsletter(request, response, requestContext);
	}

	public void renderPrivacyRequestTemplate(RequestContext requestContext, HttpServletRequest request,
											 HttpServletResponse response) {
		renderTemplate(commonTemplateNames.privacyRequestTemplate(), requestContext, request, response);
	}

	public void postPrivacyRequestSubmission(HttpServletRequest request, HttpServletResponse response) {
		complianceTask.privacyRequestSubmission(request, response);
	}

	public void renderDocumentTemplate(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) {
		renderTemplate(defaultTemplateNameResolveTask.getTemplateName(requestContext), requestContext, request,
				response);
	}

	public void renderSearchTemplate(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) {
		renderTemplate(commonTemplateNames.searchTemplateName(), requestContext, request, response);
	}

	public void renderEmbedEndpointTemplate(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) {
		renderTemplate(commonTemplateNames.embedEndPointTemplate(), true, requestContext, request, response);
	}
	
	public void renderHomePageTemplate(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) {
		renderTemplate(commonTemplateNames.homeTemplateName(), requestContext, request, response);
	}

	public void renderPlTemplate(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) {
		renderTemplate(commonTemplateNames.plTemplateName(), true, RequestContext.get(request),
				request, response);
	}
	
	public void renderPlComponentTemplate(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) {
		renderTemplate(commonTemplateNames.plComponentTemplateName(), true, RequestContext.get(request),
				request, response);
	}

	public void renderEditPlComponentTemplate(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) {
		renderTemplate(commonTemplateNames.editPlComponentTemplateName(), true, RequestContext.get(request),
				request, response);
	}

	public void renderFacebookInstantArticleTemplate(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) {
		renderTemplate(commonTemplateNames.facebookInstantArticleTemplate(), requestContext, request, response);
	}

	public void renderFacebookShareRedirectTemplate(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) {
		renderTemplate(commonTemplateNames.facebookShareRedirectTemplateName(), requestContext, request,
				response);
	}

	public void renderAppleNewsTemplate(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) {
		renderTemplate(commonTemplateNames.appleNewsTemplate(), requestContext, request, response);
	}

	public void handleUgcRating(RequestContext requestContext) {
		ugcRatingsTask.patchUserRating(requestContext);
	}

	public void postUgcRatingFeedback(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) {
		ugcFeedbackTask.submitFeedback(request, response, requestContext);
	}

	public void debugEffectiveTemplate(HttpServletRequest request, HttpServletResponse response)
			throws GlobeException, IOException {
		debugSupportMethod(request, response, "debugEffectiveTemplate");
	}

	public void debugResolvedEffectiveTemplate(HttpServletRequest request, HttpServletResponse response)
			throws GlobeException, IOException {
		debugSupportMethod(request, response, "debugResolvedEffectiveTemplate");
	}

	public void debugModels(HttpServletRequest request, HttpServletResponse response)
			throws GlobeException, IOException {
		debugSupportMethod(request, response, "debugModels");
	}

	public void debugModelTimings(HttpServletRequest request, HttpServletResponse response)
			throws GlobeException, IOException {
		debugSupportMethod(request, response, "debugModelTimings");
	}

	//Temp Method for GLBE-8768
	public void debugBlockedParams(HttpServletRequest request, HttpServletResponse response)
			throws GlobeException, IOException {
		debugActionTasks.debugBlockedParams(request, response);
	}

	public void debugClearCache(HttpServletRequest request, HttpServletResponse response, CacheClearanceCandidateRepo cacheClearanceCandidateRepository) throws IOException {
		if("PROD".equalsIgnoreCase(envConfig.getAccountName())) {
			response.getWriter().write("Debug cache clearance is not supported for PROD");
			return;
		}

		String url = request.getParameter("url");
		if (StringUtils.isBlank(url)) {
			response.getWriter().write("`url` is a required parameter for this request.");
			return;
		}

		UrlData urlData = urlDataFactory.create(url);
		if (!(urlData instanceof VerticalUrlData)) {
			response.getWriter().write("Enter a valid vertical url");
			return;
		}

		String urlPath = urlData.getPath();
		cacheClearanceCandidateRepository.handleDebugCacheClearanceEvent(urlPath);
		response.getWriter().write("Debug cache clearance is accepted for url path: " + urlPath);
	}

	public void debugLegacyUrlMapRefresh(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if("PROD".equalsIgnoreCase(envConfig.getAccountName())) {
			response.getWriter().write("Refreshing legacy url map is not supported for PROD");
			return;
		}

		// Cache clearance set to true to ensure fresh deion search
		CacheClearanceThreadLocalUtils.setClearCacheRequest(Collections.singletonList(
				CacheClearanceRequest.createBasicCacheClearanceRequest()));
		legacyUrlMap.repopulateMap();

		response.getWriter().write("Triggered refresh of legacy url map");
	}

	private void debugSupportMethod(HttpServletRequest request, HttpServletResponse response, String method) throws IOException {
		final RequestContext requestContext = requestContextSource.get(new DebugHttpServletRequest(request));
		RequestContext.set(request, requestContext);

		if ("debugEffectiveTemplate".equals(method)) {
			debugActionTasks.debugEffectiveTemplate(defaultTemplateNameResolveTask.getTemplateName(RequestContext.get(request)), request, response, false);
		} else if ("debugResolvedEffectiveTemplate".equals(method)) {
			debugActionTasks.debugEffectiveTemplate(defaultTemplateNameResolveTask.getTemplateName(RequestContext.get(request)), request, response, true);
		} else if ("debugModels".equals(method)) {
			debugActionTasks.debugModels(defaultTemplateNameResolveTask.getTemplateName(RequestContext.get(request)), request, response);
		} else if ("debugModelTimings".equals(method)) {
			debugActionTasks.debugModelTimings(defaultTemplateNameResolveTask.getTemplateName(RequestContext.get(request)), request, response);
		}
	}
	
	public void renderFlamegraph(HttpServletRequest request, HttpServletResponse response)
			throws GlobeException, IOException, InterruptedException {
		debugActionTasks.renderFlamegraph(request, response);
	}

	public void doSomethingMagical(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) throws GlobeException, ServletException, IOException {
		somethingMagicalService.testLogger(requestContext, request, response);
	}

	private boolean determineIsPageview(RequestContext requestContext, boolean isPvExcluded) {
		if (!requestContext.isPageview() && !requestContext.isDeferred() && !isPvExcluded) {
			return (requestContext.getHeaders().getAccept() != null)
					&& (requestContext.getHeaders().getAccept().contains("text/html"));
		}
		return requestContext.isPageview();
	}

	protected void renderTemplate(TemplateComponent templateComponent, final RequestContext requestContext,
			HttpServletResponse response, Writer writer, boolean deferComponents) throws GlobeRenderException,
			GlobeDependencyUnsatisfiedException, GlobeTaskHaltException, GlobeInvalidConfigurationException {

		dependencyRequestManager.satisfyDependencies(templateComponent, requestContext);

		RenderManifest renderManifest = templateComponent.getRenderManifest(requestContext, deferComponents);

		// We don't want to add link headers for certain templates that don't benefit as much from the preload
		if (!excludedTemplatesFromLinkHeaders.contains(requestContext.getTemplateName())) {
			mantleLinkHeaderHandlerMethods.addLinkHeaders(requestContext, response, renderManifest);
		}

		renderTemplate(templateComponent, requestContext, response, writer, renderManifest);
	}

	protected void renderTemplate(TemplateComponent templateComponent, final RequestContext requestContext,
			HttpServletResponse response, Writer writer, RenderManifest renderManifest) throws GlobeRenderException,
			GlobeDependencyUnsatisfiedException, GlobeTaskHaltException, GlobeInvalidConfigurationException {

		RenderingEngine renderingEngine = requestContext.getGlobe().getRenderingEngine();
		renderingEngine.render(templateComponent, renderManifest, writer, requestContext, response);
	}

	protected void render(TemplateComponent templateComponent, RequestContext requestContext,
			HttpServletResponse response, Writer writer) throws GlobeRenderException,
			GlobeDependencyUnsatisfiedException, GlobeTaskHaltException, GlobeInvalidConfigurationException {

		if (requestContext.getComponentIds() != null) {
			tracer.trace("Start rendering deferred components");
			renderDeferredComponents(templateComponent, requestContext, response, writer);
			tracer.trace("End rendering deferred components");
		} else {
			renderTemplate(templateComponent, requestContext, response, writer, true);
		}
	}

	protected void renderDeferredComponents(TemplateComponent templateComponent, final RequestContext requestContext,
			HttpServletResponse response, Writer writer) throws GlobeRenderException,
			GlobeDependencyUnsatisfiedException, GlobeTaskHaltException, GlobeInvalidConfigurationException {

		String[] version = requestContext.getParameters().get("globeDeferVersion");
		if (ArrayUtils.isNotEmpty(version) && "2".equals(version[0])) {
			tracer.trace("Using defer version 2");
			renderDeferredComponentsResourceTracking(templateComponent, requestContext, response, writer);
		} else {
			tracer.trace("Using defer version w/ implicit resource calc");
			renderDeferredComponentsImplicitResourceCalculation(templateComponent, requestContext, response, writer);
		}
	}

	protected void renderDeferredComponentsResourceTracking(TemplateComponent templateComponent,
			final RequestContext requestContext, HttpServletResponse response, Writer writer)
			throws GlobeRenderException, GlobeDependencyUnsatisfiedException, GlobeTaskHaltException,
			GlobeInvalidConfigurationException {
		Globe globe = requestContext.getGlobe();
		ResourceResolver cssResourceResolver = globe.getCssResourceResolver();
		ResourceResolver jsResourceResolver = globe.getJsResourceResolver();
		ResourceResolver svgResourceResolver = globe.getSvgResourceResolver();

		renderDeferredComponentsWithPredicate(templateComponent, requestContext, response, writer,
				new Function<ResourceHandle, ResolvedResource>() {
					@Override
					public ResolvedResource apply(ResourceHandle script) {
						try {
							if (!(script.isViewable(requestContext) && script.isInTest(requestContext))) {
								return null;
							}
							ResolvedResource resolvedScript = jsResourceResolver.resolve(script, requestContext);
							Assert.notNull(resolvedScript, "Resource " + script.getPath() + " not available");
							String pathToCheck = resolvedScript.getResource().getPath();
							if (requestContext.isResourceConcat() && requestContext.isResourceMinify()) {
								pathToCheck = pathToCheck.replace(".min.js", "");
							} else if (requestContext.isResourceConcat() && !requestContext.isResourceMinify()) {
								pathToCheck = pathToCheck.replace(".js", "");
							}
							if (!requestContext.getLoadedJs().contains(pathToCheck)) {
								return resolvedScript;
							}
						} catch (GlobeInvalidConfigurationException e) {
							throw new RuntimeException(e);
						}
						return null;
					}
				}, new Function<ResourceHandle, ResolvedResource>() {
					@Override
					public ResolvedResource apply(ResourceHandle stylesheet) {
						try {
							if (!(stylesheet.isViewable(requestContext) && stylesheet.isInTest(requestContext))) {
								return null;
							}
							ResolvedResource resolvedStylesheet = cssResourceResolver.resolve(stylesheet,
									requestContext);
							Assert.notNull(resolvedStylesheet,
									"Resource " + resolvedStylesheet.getResourceHandle().getPath() + " not available");
							String pathToCheck = resolvedStylesheet.getResource().getPath();
							if (requestContext.isResourceConcat() && requestContext.isResourceMinify()) {
								pathToCheck = pathToCheck.replace(".min.css", "");
							} else if (requestContext.isResourceConcat() && !requestContext.isResourceMinify()) {
								pathToCheck = pathToCheck.replace(".css", "");
							}
							if (!requestContext.getLoadedCss().contains(pathToCheck)) {
								return resolvedStylesheet;
							}
						} catch (GlobeInvalidConfigurationException e) {
							throw new RuntimeException(e);
						}
						return null;
					}
				}, new Function<ResourceHandle, ResolvedResource>() {
					@Override
					public ResolvedResource apply(ResourceHandle svg) {
						try {
							if (!(svg.isViewable(requestContext) && svg.isInTest(requestContext))) {
								return null;
							}
							ResolvedResource resolvedSvg = svgResourceResolver.resolve(svg, requestContext);
							Assert.notNull(resolvedSvg,
									"Resource " + resolvedSvg.getResourceHandle().getPath() + " not available");
							String idToCheck = resolvedSvg.getResourceHandle().getId();
							if (!requestContext.getLoadedSvg().contains(idToCheck)) {
								return resolvedSvg;
							}
						} catch (GlobeInvalidConfigurationException e) {
							throw new RuntimeException(e);
						}
						return null;
					}
				});
	}

	protected void renderDeferredComponentsImplicitResourceCalculation(TemplateComponent templateComponent,
			final RequestContext requestContext, HttpServletResponse response, Writer writer)
			throws GlobeRenderException, GlobeDependencyUnsatisfiedException, GlobeTaskHaltException,
			GlobeInvalidConfigurationException {

		Globe globe = requestContext.getGlobe();
		ResourceResolver cssResourceResolver = globe.getCssResourceResolver();
		ResourceResolver jsResourceResolver = globe.getJsResourceResolver();
		ResourceResolver svgResourceResolver = globe.getSvgResourceResolver();

		Triple<Set<ResolvedResource>, Set<ResolvedResource>, Set<ResolvedResource>> loadedResources = getLoadedResources(
				templateComponent, requestContext);
		Set<ResolvedResource> loadedScripts = loadedResources.getLeft();
		Set<ResolvedResource> loadedStylesheets = loadedResources.getMiddle();
		Set<ResolvedResource> loadedSvgs = loadedResources.getRight();

		renderDeferredComponentsWithPredicate(templateComponent, requestContext, response, writer,
				new Function<ResourceHandle, ResolvedResource>() {
					@Override
					public ResolvedResource apply(ResourceHandle script) {
						try {
							ResolvedResource resolvedScript = jsResourceResolver.resolve(script, requestContext);
							if (!loadedScripts.contains(resolvedScript)) {
								return resolvedScript;
							}
						} catch (GlobeInvalidConfigurationException e) {
							throw new RuntimeException(e);
						}
						return null;
					}
				}, new Function<ResourceHandle, ResolvedResource>() {
					@Override
					public ResolvedResource apply(ResourceHandle stylesheet) {
						try {
							ResolvedResource resolvedStylesheet = cssResourceResolver.resolve(stylesheet,
									requestContext);
							if (!loadedStylesheets.contains(resolvedStylesheet)) {
								return resolvedStylesheet;
							}
						} catch (GlobeInvalidConfigurationException e) {
							throw new RuntimeException(e);
						}
						return null;
					}
				}, new Function<ResourceHandle, ResolvedResource>() {
					@Override
					public ResolvedResource apply(ResourceHandle svg) {
						try {
							ResolvedResource resolvedSvg = svgResourceResolver.resolve(svg, requestContext);
							if (!loadedSvgs.contains(resolvedSvg)) {
								return resolvedSvg;
							}
						} catch (GlobeInvalidConfigurationException e) {
							throw new RuntimeException(e);
						}
						return null;
					}
				});
	}

	protected void renderDeferredComponentsWithPredicate(TemplateComponent templateComponent,
			final RequestContext requestContext, HttpServletResponse response, Writer writer,
			Function<ResourceHandle, ResolvedResource> jsPredicate,
			Function<ResourceHandle, ResolvedResource> cssPredicate,
			Function<ResourceHandle, ResolvedResource> svgPredicate) throws GlobeRenderException,
			GlobeDependencyUnsatisfiedException, GlobeTaskHaltException, GlobeInvalidConfigurationException {
		MediaType mediaType = MediaType.JSON_UTF_8;
		if (response != null) {
			response.setContentType(mediaType.toString());
		}

		Builder deferBuilder = TemplateComponent.builder()
				.resourcePath("/components/internal/defer/response/" + mediaType.subtype() + ".ftl");

		// Use array since lambda needs effectively final variable
		int[] idSuffix = { 0 };

		templateComponent.acceptComponents((filterable) -> filterable.isRenderable(requestContext), (component) -> {
			if (component.isRequested(requestContext)) {
				// @formatter:off
				Builder dumpBuilder = new TemplateComponent.Builder()
						.resourcePath("/components/internal/defer/response/dump.ftl").location(component.getUniqueId())
						.component(component).id(mediaType.subtype() + (idSuffix[0] += 1));
				// @formatter:on

				if (component.getDeferment() == null || component.isComponentSuffixForced(requestContext)) {
					dumpBuilder.location(component.getUniqueId(requestContext));
				}

				deferBuilder.component(dumpBuilder.build());
			}
		});

		deferBuilder.dependency(Property.builder().id("prefix").value(renderUtils.getStaticPath()).build());

		TemplateComponent deferTemplateComponent = deferBuilder.build();

		dependencyRequestManager.satisfyDependencies(deferTemplateComponent, requestContext);

 		RenderManifest renderManifest = deferTemplateComponent.getRenderManifest(requestContext, false);

		// Need to get scripts and stylesheets after building the render manifest
		// because doing so will resolve unresolved references.
		final Set<ResolvedResource> externalScripts = new LinkedHashSet<>();
		final Set<ResolvedResource> externalStylesheets = new LinkedHashSet<>();
		final Set<ResolvedResource> inlineSvgs = new LinkedHashSet<>();

		for (ResolvedResource script : renderManifest.getAllScriptsNotEvaluated()) {
			ResolvedResource resolvedScript = jsPredicate.apply(script.getResourceHandle());
			if (resolvedScript != null) {
				externalScripts.add(resolvedScript);
			}
		}

		for (ResolvedResource stylesheet : renderManifest.getAllStylesheetsNotEvaluated()) {
			ResolvedResource resolvedStylesheet = cssPredicate.apply(stylesheet.getResourceHandle());
			if (resolvedStylesheet != null) {
				externalStylesheets.add(resolvedStylesheet);
			}
		}

		for (ResolvedResource svg : renderManifest.getAllSvgs()) {
			ResolvedResource resolvedSvg = svgPredicate.apply(svg.getResourceHandle());
			if (resolvedSvg != null) {
				inlineSvgs.add(resolvedSvg);
			}
		}

		Set<ResolvedResource> aggregatedExternalScripts = externalScripts;
		Set<ResolvedResource> aggregatedExternalStylesheets = externalStylesheets;

		if (requestContext.isResourceConcat()) {
			if (!externalScripts.isEmpty()) {
				aggregatedExternalScripts = ImmutableSet
						.of(resourceAggregator.aggregate(externalScripts, requestContext));
			}
			if (!externalStylesheets.isEmpty()) {
				aggregatedExternalStylesheets = ImmutableSet
						.of(resourceAggregator.aggregate(externalStylesheets, requestContext));
			}
		}

		renderManifest.addModel("externalScripts", aggregatedExternalScripts);
		renderManifest.addModel("externalStylesheets", aggregatedExternalStylesheets);
		renderManifest.addModel("inlineSvgs", inlineSvgs);

		renderTemplate(deferTemplateComponent, requestContext, response, writer, renderManifest);
	}

	protected Triple<Set<ResolvedResource>, Set<ResolvedResource>, Set<ResolvedResource>> getLoadedResources(
			TemplateComponent templateComponent, final RequestContext requestContext) {

		Set<ResolvedResource> loadedScripts = new HashSet<>();
		Set<ResolvedResource> loadedStylesheets = new HashSet<>();
		Set<ResolvedResource> loadedSvgs = new HashSet<>();

		Set<String> loadedComponentIds = defaultIfNull(requestContext.getLoadedComponentIds(),
				Collections.<String>emptySet());

		Globe globe = requestContext.getGlobe();
		ResourceResolver cssResourceResolver = globe.getCssResourceResolver();
		ResourceResolver jsResourceResolver = globe.getJsResourceResolver();
		ResourceResolver svgResourceResolver = globe.getSvgResourceResolver();

		templateComponent.acceptComponents((filterable) -> {

			boolean renderable = !filterable.isDeferred() && filterable.isViewable(requestContext)
					&& filterable.isInTest(requestContext);
			boolean loaded = loadedComponentIds.contains(filterable.getId());

			return loaded || renderable;
		}, (component) -> {
			for (ResourceHandle script : component.getScripts()) {
				try {
					ResolvedResource jsResource = jsResourceResolver.resolve(script, requestContext);
					if (jsResource != null)
						loadedScripts.add(jsResource);
				} catch (GlobeInvalidConfigurationException e) {
					logger.error("Failed resolving script resource for request " + requestContext, e);
				}
			}
			for (ResourceHandle stylesheet : component.getStylesheets()) {
				try {
					ResolvedResource resource = cssResourceResolver.resolve(stylesheet, requestContext);
					if (resource != null)
						loadedStylesheets.add(resource);
				} catch (GlobeInvalidConfigurationException e) {
					logger.error("Failed resolving stylesheet resource for request " + requestContext, e);
				}
			}
			for (ResourceHandle svg : component.getSVGs()) {
				try {
					ResolvedResource resource = svgResourceResolver.resolve(svg, requestContext);
					if (resource != null)
						loadedSvgs.add(resource);
				} catch (GlobeInvalidConfigurationException e) {
					logger.error("Failed resolving svg resource for request " + requestContext, e);
				}
			}
		});

		return Triple.of(loadedScripts, loadedStylesheets, loadedSvgs);
	}

	public void redirectPattern(String pattern, boolean fullUrl, String replace, Integer statusCode, HttpServletRequest request, HttpServletResponse response,
			RequestContext requestContext) throws GlobeException {

		Pattern compiledPattern = Pattern.compile(pattern);

		String match = fullUrl ? requestContext.getFullRequestUrl() : requestContext.getRequestUri();
		Matcher patternMatcher = compiledPattern.matcher(match);

		if (!patternMatcher.matches())
			throw new GlobeInvalidDestinationException(
					"pattern " + pattern + " couldn't match url [" + requestContext.getFullRequestUrl() + "]");

		Matcher replaceMatcher = BACKMATCH_PATTERN.matcher(replace);
		StringBuilder builder = new StringBuilder();
		int previousIndex = 0;
		while (replaceMatcher.find() && (replaceMatcher.group(1) != null)) {
			Integer group = Integer.parseInt(replaceMatcher.group(1));
			if (group <= patternMatcher.groupCount() && (patternMatcher.group(group) != null)) {
				builder.append(replace.substring(previousIndex, replaceMatcher.start()))
						.append(patternMatcher.group(group));
				previousIndex = replaceMatcher.end();
			} else {
				throw new GlobeInvalidDestinationException("pattern " + pattern + " does not have a group number " + group);
			}
		}
		builder.append(replace.substring(previousIndex));

		redirectUrl(builder.toString(), statusCode, request, response, requestContext);
	}

	public void redirectUrl(String url, Integer statusCode, HttpServletRequest request,
			HttpServletResponse response, RequestContext requestContext) throws GlobeException {

		if (StringUtils.isBlank(url))
			throw new GlobeInvalidDestinationException(" invalid destination url [" + url + "]");
		StringBuilder builder = new StringBuilder().append(url);
		if (StringUtils.isNotBlank(request.getQueryString()))
			builder.append("?").append(request.getQueryString());
		response.setHeader("Location", builder.toString());
		response.setStatus(statusCode);
	}

	public void forwardToPageNotFound(HttpServletRequest request,
			HttpServletResponse response, RequestContext requestContext)
			throws GlobeException, ServletException, IOException {

		pageNotFoundHandler.handle404(request, response);
	}

	public Map<String, ComponentNode> componentMap(RequestContext rc) {
		return rc.getGlobe().getComponentMap();
	}

	public ComponentNode componentNode(RequestContext rc, String template) {
		return rc.getGlobe().getComponentTree().get(template);
	}

	public Map<String, ComponentNode> componentTree(RequestContext rc, String componentId) {

		Set<String> templates = rc.getGlobe().getComponentMap().get(componentId).getContainedInTemplate();
		Map<String, ComponentNode> componentTree = rc.getGlobe().getComponentTree();
		return templates.stream()
				.collect(Collectors.toMap(component -> component, component -> componentTree.get(component)));
	}

	public void renderPageNotFound(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) throws GlobeException, ServletException, IOException {

		renderPageNotFound(commonTemplateNames.pageNotFoundTemplateName(), requestContext, request, response);
	}

	protected void renderPageNotFound(String pageNotFoundTemplate, RequestContext requestContext,
			HttpServletRequest request, HttpServletResponse response)
			throws GlobeException, ServletException, IOException {

		//Not setting status as 404 for deferred requests as those should to be handled separately (e.g. for newsletter dialog)
		if (!requestContext.isDeferred()) {
			response.setStatus(HttpStatus.NOT_FOUND_404);
		}

		renderTemplate(pageNotFoundTemplate, true, requestContext, request, response);
	}

	public void serverStatus(RequestContext requestContext, HttpServletRequest request, HttpServletResponse response)
			throws GlobeException {

		response.setStatus(HttpStatus.OK_200);
		renderTemplate("serverStatus", true, requestContext, request, response);
	}

	public void renderAdsTxt(RequestContext requestContext, HttpServletRequest request, HttpServletResponse response, String adsTxtBovdPath)
			throws GlobeException, IOException {
		businessOwnedVerticalDataTask.serveBovdResource(adsTxtBovdPath, "text/plain", response);
	}
	
	public void getTaskConfiguration(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) throws IOException, GlobeException {

		List<TaskOutput> taskOutputs = new LinkedList<>();
		
		for(TaskGroup taskGroup : tasksRegistry.getTasks().values()) {
			taskOutputs.addAll(generateTaskOutputs(taskGroup));
		}
		
		Collections.sort(taskOutputs);
		
		ObjectMapper objectMapper = new StandardObjectMapperProvider().getContext(null);
		ServletOutputStream outputStream = response.getOutputStream();
		String valueJson = objectMapper.writeValueAsString(taskOutputs);
		response.setContentType("application/json");
		outputStream.write(valueJson.getBytes("UTF-8"));
	}
	
	/**
	 * Builds a list of tasks within a task group
	 * @param taskGroup
	 * @return
	 */
	private List<TaskOutput> generateTaskOutputs(TaskGroup taskGroup){
		
		List<TaskOutput> taskOutputs = new LinkedList<>();
		for(TaskMethod taskMethod : taskGroup.getTaskMethods().values()) {
			
			TaskOutput taskOutput = new TaskOutput();
			taskOutput.setTaskName(taskGroup.getName());
			taskOutput.setReturnType(handleType(taskMethod.getReturnType().toString()));
			taskOutput.setTaskInputConfigurations(generateTaskParameterList(taskMethod));
			taskOutputs.add(taskOutput);
		}
		
		return taskOutputs;
	}
	
	/**
	 * Builds a list of parameters for a task method
	 * 
	 * @param taskMethod
	 * @return
	 */
	private List<TaskParameterOutput> generateTaskParameterList(TaskMethod taskMethod){
		
		List<TaskParameterOutput> taskParameters = new ArrayList<>();
		
		for(MethodParameterUtil methodParameterUtil : taskMethod.getMethod().getMethodParameters()) {
			
			TaskParameterOutput taskParameterOutput = new TaskParameterOutput();
			
			if(methodParameterUtil.getAnnotation(TaskParameter.class) != null) {
				taskParameterOutput.setName(methodParameterUtil.getAnnotation(TaskParameter.class).name().toString());
				taskParameterOutput.setType(handleType(methodParameterUtil.getType().toString()));
				taskParameters.add(taskParameterOutput);
			}
			
		}
		
		return taskParameters;
	}
	
	/**
	 * Sends a hardcoded message to the logger at the level specified by the
	 * `logLevel` query param
	 * 
	 * @param request
	 * @param response
	 * @throws GlobeException
	 * @throws ServletException
	 * @throws IOException
	 */
	public void testLogger(RequestContext requestContext, HttpServletRequest request, HttpServletResponse response)
			throws GlobeException, ServletException, IOException {

		if (send404IfNotInternalIp(requestContext, request, response))
			return;

		String logLevel = requestContext.getParameterSingle("logLevel");

		if (!(StringUtils.isBlank(logLevel))) {
			logTestMessage(logLevel);
		}

	}

	public void renderPageGone(RequestContext requestContext, HttpServletRequest request,
							   HttpServletResponse response) throws GlobeException, ServletException, IOException {

		renderPageGone(commonTemplateNames.pageGoneTemplateName(), requestContext, request, response);
	}

	protected void renderPageGone(String pageGoneTemplate, RequestContext requestContext,
								  HttpServletRequest request, HttpServletResponse response)
			throws GlobeException, ServletException, IOException {

		//Not setting status as 410 for deferred requests as those should to be handled separately (e.g. for newsletter dialog)
		if(!requestContext.isDeferred())  {
			response.setStatus(HttpStatus.GONE_410);
		}

		renderTemplate(pageGoneTemplate, true, requestContext, request, response);
	}

	public void ddmAccountAuth(HttpServletRequest request, HttpServletResponse response){
		AccountInfo accountInfo = ddmAccountAuthService.getAccountInfoFromRequest(request);

		if(accountInfo != null){
			try {
				for(Cookie cookie : ddmAccountAuthService.createCookiesForDDMAccountAuth(accountInfo)){
					response.addCookie(cookie);
				}
				response.setStatus(HttpStatus.OK_200);
			}catch (JsonProcessingException E){
				throw new GlobeException("Error creating cookies for mdpAccountAuth", E);
			}
		}else{
			throw new GlobeException("Unable to get AccountInfo");
		}
	}

	public void deleteCookiesForDDMAccountLogout(HttpServletRequest request, HttpServletResponse response){
		for(Cookie cookie : ddmAccountAuthService.createCookiesForDeletion()){
			response.addCookie(cookie);
		}

		response.setHeader("Content-Type", "text/html");
		response.setStatus(HttpStatus.OK_200);
		renderTemplate(commonTemplateNames.logoutTemplateName(), true, RequestContext.get(request), request, response);
	}

	public void submitResoundReview(HttpServletRequest request, HttpServletResponse response) throws IOException {

		if(resoundService == null){
			throw new GlobeException("Resound service was not created, please check configs");
		}

		MantleResoundReviewWriteWrapper writeResponse = resoundService.submitReview(request);

		if(writeResponse != null) {
			ObjectMapper objectMapper = new StandardObjectMapperProvider().getContext(null);
			ServletOutputStream outputStream = response.getOutputStream();
			String valueJson = objectMapper.writeValueAsString(writeResponse);
			response.setContentType("application/json");
			outputStream.write(valueJson.getBytes("UTF-8"));
		}else{
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}

	}

	public void submitResoundReact(HttpServletRequest request, HttpServletResponse response, AbstractMantleEndpointController.Operation operation) throws IOException {
		if (resoundService == null){
			throw new GlobeException("Resound service was not created, please check configs");
		}

		ResoundReactResponse reactResponse = resoundService.submitReact(request, operation);

		if (reactResponse != null) {
			ObjectMapper objectMapper = new StandardObjectMapperProvider().getContext(null);
			ServletOutputStream outputStream = response.getOutputStream();
			String valueJson = objectMapper.writeValueAsString(reactResponse);
			response.setContentType("application/json");
			outputStream.write(valueJson.getBytes("UTF-8"));
		} else {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
	}

	public void resoundReviewStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if(resoundService == null){
			throw new GlobeException("Resound service was not created, please check configs");
		}

		MantleResoundReviewStatusWrapper reviewStatusResponse = resoundService.reviewStatus(request);

		if(reviewStatusResponse != null) {
			ObjectMapper objectMapper = new StandardObjectMapperProvider().getContext(null);
			ServletOutputStream outputStream = response.getOutputStream();
			String valueJson = objectMapper.writeValueAsString(reviewStatusResponse);
			response.setContentType("application/json");
			outputStream.write(valueJson.getBytes("UTF-8"));
		}else{
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
	}

	protected List<String> excludedTemplatesFromLinkHeaders() {
		return new ArrayList<>(
				List.of(
					commonTemplateNames.embedEndPointTemplate(),
					commonTemplateNames.logoutTemplateName()
				)
		);
	}

	/**
	 * Sends 404 and returns true if requester's IP address is not internal
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	private boolean send404IfNotInternalIp(RequestContext requestContext, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (!(renderUtils.isInternalIp(requestContext.getHeaders().getRemoteIp()))) {
			response.setStatus(HttpStatus.NOT_FOUND_404);
			renderPageNotFound("pageNotFoundTemplate", requestContext, request, response);
			return true;
		}
		return false;
	}
	
	/**
	 * Takes in a type from the annotations and cleans it up so it's more digestable
	 * 
	 * When you get the type from the annotation it comes back like interface java.lang.util.List
	 * This cleans it up to be just List
	 * 
	 * @param type
	 * @return
	 */
	private String handleType(String type) {
		int subStringIndex = type.lastIndexOf(".")+1;
		
		if(subStringIndex <= 1) {
			subStringIndex = 0;
		}

		return type.substring(subStringIndex).replace("class ", "").replace("interface ", "").replace("$", ".");
	}

	/**
	 * Sends a test log message. There is probably a better home for this.
	 * 
	 * @param level
	 */
	public static void logTestMessage(String level) {

		String debugMsg = "This is a test log entry at level `{}`.  Please ignore.";

		level = level.toLowerCase();

		switch (level) {
		case "debug":
			logger.debug(debugMsg, level);
			break;
		case "error":
			logger.error(debugMsg, level);
			break;
		case "info":
			logger.info(debugMsg, level);
			break;
		case "warn":
			logger.warn(debugMsg, level);
			break;
		case "trace":
			logger.trace(debugMsg, level);
			break;
		default:
		}
	}

	private static class RenderCacheKey {
		private final String url;
		private final String template;

		public RenderCacheKey(String url, String template) {
			this.url = url;
			this.template = template;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			result = prime * result + ((template == null) ? 0 : template.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RenderCacheKey other = (RenderCacheKey) obj;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			if (template == null) {
				if (other.template != null)
					return false;
			} else if (!template.equals(other.template))
				return false;
			return true;
		}
	}

}
