package com.about.mantle.model.services.impl;

import javax.ws.rs.client.WebTarget;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.PageRequest;
import com.about.mantle.model.extended.TemplateTypeEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.responses.DocumentExSliceableResponse;
import com.about.mantle.model.services.ArticleService;

public class ArticleServiceImpl extends AbstractHttpServiceClient implements ArticleService {

	private static final String SELENE_ARTICLE_PATH = "/article";
	private static final String SELENE_RELATED_PATH = "/related";
	
	public ArticleServiceImpl(HttpServiceClientConfig httpServiceClientConfig) {
		super(httpServiceClientConfig);
		this.baseTarget = baseTarget.path(SELENE_ARTICLE_PATH);
	}
	
	@Override
	public SliceableListEx<BaseDocumentEx> getRelated(String url, PageRequest pageRequest,
			ArticleFilterRequest articleFilterRequest) {

		RelatedArticleRequestContext ctx = new RelatedArticleRequestContext.Builder().setUrl(url).build();
		return getRelated(ctx, pageRequest, articleFilterRequest);
	}

	@Override
	public SliceableListEx<BaseDocumentEx> getRelated(RelatedArticleRequestContext ctx, PageRequest pageRequest,
			ArticleFilterRequest articleFilterRequest) {

		DocumentExSliceableResponse response = getRelated(ctx, pageRequest, articleFilterRequest,
				DocumentExSliceableResponse.class);

		if (response.getData() == null) return null;

		SliceableListEx<BaseDocumentEx> data = response.getData();
		data.setLimit(pageRequest.getLimit());
		data.setOffset(pageRequest.getOffset());

		return data;
	}

	private <T extends BaseResponse<?>> T getRelated(RelatedArticleRequestContext reqCtx, PageRequest pageRequest,
			ArticleFilterRequest articleFilterRequest, Class<T> bindToTarget) {
		WebTarget webTarget = baseTarget.path(SELENE_RELATED_PATH);

		if (reqCtx != null) {
			if (reqCtx.getAlgorithm() != null) webTarget = webTarget.queryParam("algorithm", reqCtx.getAlgorithm());
			if (reqCtx.getDocId() != null) webTarget = webTarget.queryParam("docId", reqCtx.getDocId());
			if (StringUtils.isNotEmpty(reqCtx.getUrl())) webTarget = webTarget.queryParam("url", reqCtx.getUrl());
		}

		webTarget = addPageRequestParams(webTarget, pageRequest);
		webTarget = addArticleFilterRequestParams(webTarget, articleFilterRequest, true, true, true);

		return readResponse(webTarget, bindToTarget);
	}

	private WebTarget addPageRequestParams(WebTarget webTarget, PageRequest pageRequest) {

		if (pageRequest != null && pageRequest.getLimit() != null) {
			webTarget = webTarget.queryParam("limit", pageRequest.getLimit());
		}

		if (pageRequest != null && pageRequest.getOffset() != null) {
			webTarget = webTarget.queryParam("offset", pageRequest.getOffset());
		}

		return webTarget;
	}

	private WebTarget addArticleFilterRequestParams(WebTarget webTarget, ArticleFilterRequest articleFilterRequest,
			boolean withTemplateTypes, boolean withExcludeKeys, boolean withExcludeDocIds) {

		if (articleFilterRequest == null) return webTarget;

		if (withTemplateTypes && CollectionUtils.isNotEmpty(articleFilterRequest.getTemplateTypes())) {
			for (TemplateTypeEx template : articleFilterRequest.getTemplateTypes()) {
				webTarget = webTarget.queryParam("templateType", template.name());
			}
		}

		if (withExcludeKeys && CollectionUtils.isNotEmpty(articleFilterRequest.getExcludeKeys())) {
			for (String key : articleFilterRequest.getExcludeKeys()) {
				webTarget = webTarget.queryParam("excludeKey", key);
			}
		}
		if (withExcludeDocIds && CollectionUtils.isNotEmpty(articleFilterRequest.getExcludeDocIds())) {
			for (Long docId : articleFilterRequest.getExcludeDocIds()) {
				webTarget = webTarget.queryParam("excludeDocId", docId);
			}
		}

		return webTarget;
	}

}
