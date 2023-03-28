package com.about.mantle.model.services.impl;

import java.util.List;

import javax.ws.rs.client.WebTarget;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeNotFoundException;
import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.models.response.Error;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.infocat.services.ProductService;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.BaseDocumentEx.State;
import com.about.mantle.model.extended.responses.DocumentExResponse;
import com.about.mantle.model.services.DocumentService;
import com.about.mantle.model.services.document.preprocessor.DocumentPreprocessor;

public class DocumentServiceImpl extends AbstractHttpServiceClient implements DocumentService {

	protected static final String SELENE_DOCUMENT_PATH = "/document";
	protected final List<DocumentPreprocessor> documentPreprocessors;
	private static Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

	
	public DocumentServiceImpl(HttpServiceClientConfig httpServiceClientConfig, List<DocumentPreprocessor>documentPreprocessors) {
		super(httpServiceClientConfig);
		this.documentPreprocessors = documentPreprocessors;
	}

	private <T extends BaseResponse<?>> T getDocument(DocumentReadRequestContext documentRequestContext, Class<T> bindToTarget) {

		WebTarget webTarget = baseTarget.path(SELENE_DOCUMENT_PATH);

		webTarget = addDocumentRequestContext(webTarget, documentRequestContext);

		T response = readResponse(webTarget, bindToTarget);

		return response;

	}

	private WebTarget addDocumentRequestContext(WebTarget webTarget,
			DocumentReadRequestContext documentRequestContext) {

		if (documentRequestContext != null) {

			if (documentRequestContext.getUrl() != null) {
				webTarget = webTarget.queryParam("url", documentRequestContext.getUrl());
			}

			if (documentRequestContext.getDocId() != null) {
				webTarget = webTarget.queryParam("docId", documentRequestContext.getDocId());
			}

			webTarget = webTarget.queryParam("includesummaries", true);

			webTarget = webTarget.queryParam("followsummaryredirects", true);

			
			if (documentRequestContext.getState() != null) {
				webTarget = webTarget.queryParam("state", documentRequestContext.getState().name());

				// Use active data parameter only when state is PREVIEW
				if (documentRequestContext.getActiveDate() != null
						&& documentRequestContext.getState() == State.PREVIEW) {
					webTarget = webTarget.queryParam("activeDate", documentRequestContext.getActiveDate());
				}
			}
		}

		return webTarget;
	}

	protected BaseDocumentEx processResponse(DocumentReadRequestContext documentRequestContext,
			DocumentExResponse documentResponse) throws GlobeNotFoundException {
		String url = documentRequestContext.getUrl();
		if (documentResponse.getStatus().getErrors() != null) {
			for (Error e : documentResponse.getStatus().getErrors()) {
				if (e.getCode().equals("sel.notfound.document") || e.getCode().equals("sel.document.notfound")
						|| e.getCode().equals("sel.document.paginater.page.notfound")) {
					if (documentResponse.getData() != null
							&& StringUtils.isNotEmpty(documentResponse.getData().getUrl())) {
						String redirectUrl = documentResponse.getData().getRootUrl();
						if (!redirectUrl.equals(url)) {

							return DocumentService.createRedirectDocument(url, redirectUrl);
						}
					}
					throw new GlobeNotFoundException(e.getCode());
				}
			}
		}

		return documentResponse.getData();
	}

	@Override
	public BaseDocumentEx getDocument(DocumentReadRequestContext documentRequestContext) {
		DocumentExResponse documentResponse = getDocument(documentRequestContext, DocumentExResponse.class);
		BaseDocumentEx document = processResponse(documentRequestContext, documentResponse);
		
		document = processDocument(document);
		
		return document;
	}
	
	@Override
	public BaseDocumentEx processDocument(BaseDocumentEx document) {
		
		for(DocumentPreprocessor documentPreprocessor : documentPreprocessors) {
			document = documentPreprocessor.preProcessDocument(document);
		}
		return document;
	}
}