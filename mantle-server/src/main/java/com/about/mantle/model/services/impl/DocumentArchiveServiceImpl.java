package com.about.mantle.model.services.impl;

import com.about.globe.core.exception.GlobeException;
import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.models.response.Error;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.responses.ArchivedDocumentExResponse;
import com.about.mantle.model.services.DocumentArchiveService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.WebTarget;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DocumentArchiveServiceImpl extends AbstractHttpServiceClient implements DocumentArchiveService {

	protected static final String SELENE_DOCUMENT_ARCHIVE_PATH = "/document/archive/list";
	private static Logger logger = LoggerFactory.getLogger(DocumentArchiveServiceImpl.class);
	private final String vertical;

	public DocumentArchiveServiceImpl(HttpServiceClientConfig httpServiceClientConfig, String vertical) {
		super(httpServiceClientConfig);
		this.vertical = vertical;
	}

	/**
	 * Returns the set of URLs of all archived documents. BaseDocumentEx objects are not returned as only the
	 * URL is currently required. As the utility of archived documents should be limited to handling 410 response
	 * codes, there should be no issues changing this return type in the future if needed as verticals have no
	 * need to extend or reference this service.
	 * @return
	 */
	public Set<String> getArchivedDocuments() {
		int batchCounter = 0;
		int limitBatchSize = 500;
		int offset = 0;
		ArchivedDocumentExResponse searchResult = null;
		Set<String> itemsAggregated = new HashSet<>();
		try {
			/*
			 * Page over results until no more results found
			 */
			do {
				logger.info("Retrieving {} archived documents: offset={}, limit={}, items-aggregated={}", vertical,
						offset, limitBatchSize, itemsAggregated.size());
				searchResult = getBatch(offset, limitBatchSize, ArchivedDocumentExResponse.class);
				addDocumentUrls(itemsAggregated, searchResult);
				batchCounter++;
				offset = batchCounter * limitBatchSize;
			} while (CollectionUtils.isNotEmpty(searchResult.getData().getList()));

		} catch (Exception ex) {
			throw new GlobeException("Error while getting list of archived documents", ex);
		}
		return itemsAggregated;
	}


	private <T extends BaseResponse<?>> T getBatch(Integer offset, Integer limit, Class<T> bindToTarget) {
		WebTarget webTarget = baseTarget.path(SELENE_DOCUMENT_ARCHIVE_PATH);

		if (this.vertical != null) {
			webTarget = webTarget.queryParam("vertical", this.vertical);
		}
		webTarget = webTarget.queryParam("offset", offset);
		webTarget = webTarget.queryParam("limit", limit);

		T response = readResponse(webTarget, bindToTarget);

		if (response != null && response.getStatus().getErrors() != null) {
			// Log errors
			for (Error error : response.getStatus().getErrors()) {
				logger.error("Error retrieving archived document list: {}", error.getMessage());
			}
		}

		return response;
	}

	private void addDocumentUrls(Set<String> urls, ArchivedDocumentExResponse response) {
		if (response != null && response.getData() != null && !response.getData().isEmpty()) {
			urls.addAll(response.getData().stream().map(x -> x.getUrl()).collect(Collectors.toList()));
		}
	}
}