package com.about.mantle.model.tasks;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeInvalidTaskParameterException;
import com.about.globe.core.exception.GlobeNotFoundException;
import com.about.globe.core.exception.GlobeTaskHaltException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.htmlslicing.HtmlSlicer;
import com.about.mantle.htmlslicing.HtmlSlicerConfig;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.State;
import com.about.mantle.model.extended.docv2.LinkEx;
import com.about.mantle.model.extended.docv2.QuizDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.quiz.QuizResultEx;
import com.about.mantle.model.services.DocumentService;
import com.about.mantle.model.services.DocumentService.DocumentReadRequestContext;

@Tasks
public class DocumentTask {
	public static final Logger logger = LoggerFactory.getLogger(DocumentTask.class);

	protected final DocumentService documentService;
	private final HtmlSlicer htmlSlicer;

	public DocumentTask(DocumentService documentService, HtmlSlicer htmlSlicer) {
		this.documentService = documentService;
		this.htmlSlicer = htmlSlicer;
	}

	@Task(name = "entityTags")
	public List<String> entityTags(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "document") BaseDocumentEx document) {

		if (document == null) {
			throw new GlobeInvalidTaskParameterException("document is null");
		}
		// @formatter:off
		Stream<String> entities = (document.getMetaData() != null && document.getMetaData().getEntities() != null)
				? document.getMetaData().getEntities().stream()
						.sorted((e1, e2) -> defaultIfNull(e2.getScore(), BigDecimal.ZERO)
								.compareTo(defaultIfNull(e1.getScore(), BigDecimal.ZERO)))
						.map(entity -> entity.getName()).limit(5)
				: Stream.of();
		// @formatter:on

		return entities.collect(Collectors.toList());
	}

	@Task(name = "DOCUMENT")
	public BaseDocumentEx fetchDocumentWithBlocks(@RequestContextTaskParameter RequestContext requestContext,
											  @TaskParameter(name = "blocks") String blockConfig)
			throws GlobeTaskHaltException, GlobeInvalidTaskParameterException {

		State state = getStateFromRequestParameters(requestContext.getParameters());
		Long activeDate = getDateFromRequestParameters(requestContext.getParameters());

		return fetchDocument(requestContext, getUrlForDocumentPageRequest(requestContext), state, activeDate, blockConfig);

	}

	@Task(name = "DOCUMENT")
	public BaseDocumentEx fetchDocument(@RequestContextTaskParameter RequestContext requestContext)
			throws GlobeTaskHaltException, GlobeInvalidTaskParameterException {

		State state = getStateFromRequestParameters(requestContext.getParameters());
		Long activeDate = getDateFromRequestParameters(requestContext.getParameters());

		return fetchDocument(requestContext, getUrlForDocumentPageRequest(requestContext), state, activeDate, null);
	}

	@Task(name = "DOCUMENT")
	public BaseDocumentEx fetchDocument(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "url") String url) throws GlobeTaskHaltException, GlobeInvalidTaskParameterException {

		return this.fetchDocument(requestContext, url, State.ACTIVE, null,null);

	}
	
	@Task(name = "DOCUMENT")
	public BaseDocumentEx fetchDocument(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "url") String url, @TaskParameter(name = "state") State state, 
			@TaskParameter(name = "et") Long activeDate) throws GlobeTaskHaltException, GlobeInvalidTaskParameterException {

		return this.fetchDocument(requestContext, url, state, activeDate,null);

	}

	@Task(name = "DOCUMENT")
	@TimedComponent(category = "task")
	public BaseDocumentEx fetchDocument(@RequestContextTaskParameter RequestContext requestContext,
											  @TaskParameter(name = "url") String url,
											  @TaskParameter(name = "state") State state, @TaskParameter(name = "et") Long activeDate,
											  @TaskParameter(name = "blocks") String blockConfig)
			throws GlobeTaskHaltException, GlobeInvalidTaskParameterException {

		DocumentReadRequestContext documentRequestContext = createDocumentRequestContext(url, state, activeDate);
		BaseDocumentEx document = documentService.getDocument(documentRequestContext);

		// need to do slicing even if the blockConfig is empty because in addition to slicing
		// it's also what sets the character and word counts
		HtmlSlicerConfig slicerConfig = new HtmlSlicerConfig(blockConfig);
		document = htmlSlicer.slice(slicerConfig, document);
		return document;
	}

	/**
	 *	Returns null instead of throwing an error if the no document exists for the request.
	 *  Useful for when a component may be on non-document template e.g. search, embed
	 */
	@Task(name = "fetchDocumentIfValid")
	public BaseDocumentEx fetchDocumentIfValid(@RequestContextTaskParameter RequestContext requestContext)
			throws GlobeTaskHaltException, GlobeInvalidTaskParameterException {
		try {
			return fetchDocument(requestContext);
		} catch (GlobeNotFoundException e) {
			return null;
		}
	}

	/**
	 *	Returns null instead of throwing an error if the url is not a selene document.
	 *  Useful for when the	url doesn't have to be a selene document e.g. guest author
	 */
	@Task(name = "fetchDocumentIfValidUrl")
	public BaseDocumentEx fetchDocumentIfValidUrl(@RequestContextTaskParameter RequestContext requestContext,
										@TaskParameter(name = "url") String url) {
		try {
			return fetchDocument(requestContext, url);
		} catch (GlobeNotFoundException e) {
			return null;
		}
	}

	@Task(name = "quizResultRecirc")
	@TimedComponent(category = "task")
	public Map<String, Object> getQuizResultRecirc(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "id") String id) throws GlobeTaskHaltException, GlobeInvalidTaskParameterException {
		if (StringUtils.isEmpty(id)) return null;

		QuizDocumentEx quiz = (QuizDocumentEx) fetchDocument(requestContext);

		QuizResultEx result;
		try {
			result = quiz.getResults().getList().stream().filter(r -> r.getId().equals(id)).findFirst().get();
		} catch (NoSuchElementException e) {
			logger.error("ID: " + id + " not found for quiz url: " + quiz.getUrl());
			return null;
		}
		if (result.getLinkboxes().isEmpty()) return null;

		SliceableListEx<LinkEx> links = result.getLinkboxes().getList().get(0).getLinks();

		SliceableListEx<BaseDocumentEx> results = new SliceableListEx<>();
		results.setLimit(links.getLimit());
		results.setOffset(links.getOffset());
		results.setTotalSize(links.getTotalSize());
		results.setList(links.getList().stream().map((link) -> link.getDocument()).collect(Collectors.toList()));

		Map<String, Object> resultss = new HashMap<>();
		resultss.put("recirc", results);
		resultss.put("heading", result.getLinkboxes().getList().get(0).getHeading());
		return resultss;

	}

	protected String getUrlForDocumentPageRequest(RequestContext requestContext) throws GlobeTaskHaltException {
		try {
			return requestContext.getUrlData().with().environment(null).port(null).query(null).build().toString();
		} catch (UnsupportedEncodingException | URISyntaxException e) {
			throw new GlobeTaskHaltException("failed to build url from requestContext " + requestContext, e);
		}
	}

	/**
	 * @deprecated Use the version in DocumentService instead
	 */
	@Deprecated
	public static State getStateFromRequestParameters(final Map<String, String[]> params) {
		return DocumentService.getStateFromRequestParameters(params);
	}

	/**
	 * @deprecated Use the version in DocumentService instead
	 */
	@Deprecated
	public static Long getDateFromRequestParameters(final Map<String, String[]> params) {
		return DocumentService.getDateFromRequestParameters(params);
	}

	protected DocumentReadRequestContext createDocumentRequestContext(String key, State state, Long activeDate) {
		return DocumentService.createDocumentRequestContext(key, state, activeDate);
	}

	/**
	 * This is used in {@link com.about.mantle.spring.interceptor.CacheClearanceInterceptor} to know if request is valid preview request
	 * which generally comes from CMS. We want to cache-clear all preview requests. 
	 */
	public static boolean isValidPreviewRequest(final Map<String, String[]> params) {
		Long date = getDateFromRequestParameters(params);
		State state = getStateFromRequestParameters(params);
		return date != null && State.PREVIEW.equals(state);
	}
}
