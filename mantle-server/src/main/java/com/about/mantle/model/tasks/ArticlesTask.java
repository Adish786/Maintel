package com.about.mantle.model.tasks;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.globe.core.testing.GlobeBucket;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.hippodrome.url.UrlDataFactory;
import com.about.mantle.model.PageRequest;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.ArticleService;
import com.about.mantle.model.services.ArticleService.ArticleFilterRequest;
import com.about.mantle.model.services.ArticleService.RelatedArticleRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.stripToNull;

@Tasks
public class ArticlesTask {
	public static final Logger logger = LoggerFactory.getLogger(ArticlesTask.class);

	protected final ArticleService articleService;
	protected final DocumentTask documentTask;
	protected final UrlDataFactory urlDataFactory;

	/**
	 * @param articleService
	 * @param documentTask
	 * @param urlDataFactory
	 */
	public ArticlesTask(ArticleService articleService, DocumentTask documentTask, UrlDataFactory urlDataFactory) {
		this.articleService = articleService;
		this.documentTask = documentTask;
		this.urlDataFactory = urlDataFactory;
	}

	@Task(name = "relatedArticles")
	public SliceableListEx<BaseDocumentEx> getRelatedArticles(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "pageNum") Integer pageNum,
			@TaskParameter(name = "itemsPerPage") Integer itemsPerPage) {

		return getRelatedArticles(requestContext, pageNum, itemsPerPage, null);
	}

	@Task(name = "relatedArticles")
	public SliceableListEx<BaseDocumentEx> getRelatedArticles(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "url") String url,
			@TaskParameter(name = "pageNum") Integer pageNum,
			@TaskParameter(name = "itemsPerPage") Integer itemsPerPage) {

		return getRelatedArticles(requestContext, url, pageNum, itemsPerPage, null);
	}

	@Task(name = "relatedArticles")
	public SliceableListEx<BaseDocumentEx> getRelatedArticles(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "pageNum") Integer pageNum,
			@TaskParameter(name = "itemsPerPage") Integer itemsPerPage,
			@TaskParameter(name = "templateType") String templateType) {

		return getRelatedArticles(requestContext, requestContext.getRequestUrl(), pageNum, itemsPerPage, templateType);
	}

	
	@Task(name = "relatedArticles")
	@TimedComponent(category = "task")
	public SliceableListEx<BaseDocumentEx> getRelatedArticles(
			@RequestContextTaskParameter RequestContext requestContext, 
			@TaskParameter(name = "url") String url,
			@TaskParameter(name = "pageNum") Integer pageNum,
			@TaskParameter(name = "itemsPerPage") Integer itemsPerPage,
			@TaskParameter(name = "templateType") String templateType) {

		return getRelatedArticles(requestContext, url, pageNum, itemsPerPage, templateType, null);
	}

	@Task(name = "relatedArticles")
	@TimedComponent(category = "task")
	public SliceableListEx<BaseDocumentEx> getRelatedArticles(
			@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "url") String url,
			@TaskParameter(name = "pageNum") Integer pageNum,
			@TaskParameter(name = "itemsPerPage") Integer itemsPerPage,
			@TaskParameter(name = "templateType") String templateType,
			@TaskParameter(name = "algorithm") String algorithm) {

		PageRequest pageRequest = PageRequest.fromPageNumberAndSize(pageNum, itemsPerPage);
		ArticleFilterRequest.Builder articleFilterRequestBuilder = new ArticleFilterRequest.Builder();

		if (templateType != null) {
			Set<TemplateTypeEx> typeSet = Arrays.asList(split(templateType, ',')).stream()
					.map(t -> TemplateTypeEx.valueOf(t)).collect(Collectors.toSet());

			articleFilterRequestBuilder.setTemplateTypes(typeSet);
		}

		ArticleFilterRequest articleFilterRequest = articleFilterRequestBuilder.build();

		RelatedArticleRequestContext.Builder ctxBuilder = new RelatedArticleRequestContext.Builder();
		try {
			// Only use case for this that I can tell is pattern library which should be using mock data anyway.
			// Otherwise the general case for this _should_ be to use the canonical url or the docId.
			ctxBuilder.setUrl(urlDataFactory.create(url).with().port(null).environment(null).query(null).build().toString());
		} catch (UnsupportedEncodingException | URISyntaxException e) {
			ctxBuilder.setUrl(requestContext.getCanonicalUrl());
		}
		ctxBuilder.setAlgorithm(algorithm != null ? stripToNull(algorithm) : getRelatedArticlesAlgorithm(requestContext));
		RelatedArticleRequestContext ctx = ctxBuilder.build();

		SliceableListEx<BaseDocumentEx> relatedArticles = articleService.getRelated(ctx, pageRequest, articleFilterRequest);

		if (relatedArticles == null || relatedArticles.isEmpty()) {
            // Revisit this in GLBE-7304.  We _should_ log when we expect recirc and are not getting it.
//			if (ctx.getAlgorithm() != null) {
//				logger.error("Failed to get related articles using algorithm [{}]", ctx.getAlgorithm());
//			}
			return null;
		}

		return relatedArticles;
	}

	@Task(name = "filterArticles")
	@TimedComponent(category = "task")
	public List<BaseDocumentEx> filterArticles(@TaskParameter(name = "articles") List<BaseDocumentEx> articles,
											   @TaskParameter(name = "articlesToFilterOut") List<BaseDocumentEx> articlesToFilterOut) {
		List<BaseDocumentEx> filteredArticles = new ArrayList<>(articles);
		Set<Long> docIds = articlesToFilterOut.stream().map(doc -> doc.getDocumentId()).collect(Collectors.toSet());

		return filteredArticles.stream().filter(doc -> !docIds.contains(doc.getDocumentId())).collect(Collectors.toList());
	}

	/**
	 * Sciences team wants the ability to A/B test the related articles algorithm.
	 * https://iacpublishing.atlassian.net/browse/GLBE-6516
	 * Rather than having verticals update their task model dependencies to inject this test payload,
	 * which would likely be duplicate code, we check for the existence of this payload on the requestContext.
	 */
	private String getRelatedArticlesAlgorithm(RequestContext requestContext) {
		GlobeBucket bucket = requestContext.getTests().get("relatedArticlesAlgorithm");
		return (bucket == null || bucket.getPayload() == null) ? null : stripToNull(bucket.getPayload(String.class));
	}
}
