package com.about.mantle.model.services.impl;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.responses.DocSchemaExResponse;
import com.about.mantle.model.services.DocSchemaService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DocSchemaServiceImpl extends AbstractHttpServiceClient implements DocSchemaService {

	public static final String SELENE_DOC_SCHEMA_PATH = "/docschema";
	private static final Logger logger = LoggerFactory.getLogger(DocSchemaServiceImpl.class);

	public DocSchemaServiceImpl(HttpServiceClientConfig httpServiceClientConfig) {
		super(httpServiceClientConfig);
	}

	public String getDocSchemaByDocId(Long docId) {
		DocSchemaExResponse docSchema;

		docSchema = fetch(SELENE_DOC_SCHEMA_PATH,
				new DocSchemaRequest.Builder().setDocId(docId).build(), DocSchemaExResponse.class);

		if (docSchema == null || docSchema.getData() == null) {
			if (docSchema.getStatus().getErrors() != null) {
				String errorMessage = docSchema.getStatus().getErrors().toString();

				/**
				 * Selene counts a doc not having a schema as an error but we don't want to log it as it creates a
				 * large number of errors. Ticket: https://dotdash.atlassian.net/browse/BEAUT-2158
				 */
				if (!errorMessage.contains("sel.docschema.notfound")) {
					logger.error(errorMessage);
				}
			}
			return null;
		}

		String schema = getParsedArray(docSchema);

		return schema;
	}

	/**
	 * To add the schema to the about array in the ftl it can not just be a string of the list.
	 * It has to be a string of the items needed in the schema. This function parses that out.
	 * @param docSchema
	 * @return
	 */
	private String getParsedArray(DocSchemaExResponse docSchema) {
		Gson gson = new Gson();
		String schema = null;
		try {
			if (docSchema.getData().get("list") != null) {
				schema = ((ArrayList<Object>) docSchema.getData().get("list"))
						.stream()
						.map(gson::toJson)
						.collect(Collectors.joining(","));
			} else {
				logger.error("Data from /docschema endpoint does not include a list");
			}
		} catch ( ClassCastException e) {
			logger.error("Data from /docschema endpoint is in an unexpected format", e);
			return null;
		}
		return schema;
	}

	public <T extends BaseResponse<?>> T fetch(String basePath, DocSchemaRequest key, Class<T> bindToTarget) {

		WebTarget webTarget = baseTarget.path(basePath).path(Long.toString(key.getDocId()));

		T response = readResponse(webTarget, bindToTarget);
		return response;
	}
}
