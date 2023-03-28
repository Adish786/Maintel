package com.about.mantle.utils.selene.document.client;

import com.about.mantle.utils.GsonGenericDeserializer;
import com.about.mantle.utils.jsonfileutils.ObjectFromFile;
import com.about.mantle.utils.selene.api.common.DataWithStatus;
import com.about.mantle.utils.selene.client.common.RestAssuredClient;
import com.about.mantle.utils.selene.document.api.Document;
import com.about.mantle.utils.selene.document.api.SCDocument;
import com.about.mantle.utils.selene.document.request.DocumentReadRequest;
import com.about.mantle.utils.selene.version.Version;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.OK;

public class DocumentClient<T extends Document> extends RestAssuredClient {
	private String filePath;
	private final Class<T> type;
	private DataWithStatus<T> documentSaveResponse;
	private T data;
	private final String contentType = Version.V2.getContentType();

	public DocumentClient(T data, Class<T> type) {
		this.data = data;
		this.type = type;
	}

	public DocumentClient(String filePath, Class type) throws IOException {
		this.filePath = filePath;
		this.type = type;
		ObjectFromFile<T> documentObjectFromFile = new ObjectFromFile<T>(filePath, type);
		this.data = documentObjectFromFile.getObject();
	}

	public DocumentClient(Class<T> type) {
		this.type = type;
	}

	public Class<T> getType() {
		return this.type;
	}

	public DataWithStatus delete(Response.Status status) {
		return delete(status, true);
	}

	public DataWithStatus delete(Response.Status status, Boolean forceMode) {
		T responseData = documentSaveResponse.getData();
		return delete(status, responseData.getDocId(), responseData.getDocumentState().getState(),
					  responseData.getDocumentState().getActiveDate(), forceMode);
	}

	public DataWithStatus delete(Response.Status status, Long docId, String state, Long activeDate, Boolean forceMode) {
		Map<String, Object> params = new HashMap();
		params.put("docId", docId);
		params.put("state", state);
		params.put("activeDate", activeDate);
		if(forceMode != null) params.put("forceMode", forceMode);
		return documentSaveResponse = new GsonGenericDeserializer(waitForExpectedStatusCode(() -> given().spec(requestSpecification())
																			 .params(params)
																			 .delete("/document"), status), type)
				.getResponse();
	}

	public DataWithStatus create(Response.Status status) {
		return create(status, false);
	}

	public DataWithStatus create(Response.Status status, boolean synchSolr) {
		return documentSaveResponse = new GsonGenericDeserializer(waitForExpectedStatusCode(() -> given().spec(requestSpecification()).body(data)
																				  .queryParam("synchSolr", synchSolr)
																				  .with().contentType(contentType)
																				  .headers(headers())
																				  .post("/document"), status), type).getResponse();
	}

	public DataWithStatus read(DocumentReadRequest readRequest, Response.Status status) {
		Map<String, Object> params = new HashMap();
		if(readRequest.getUrl() != null) params.put("url", readRequest.getUrl());
		if(readRequest.getDocId() != null) params.put("docId", readRequest.getDocId());
		if(readRequest.getState() != null) params.put("state", readRequest.getState());
		if(readRequest.getProjection() != null) params.put("projection", readRequest.getProjection());
		if(readRequest.getActiveDate() != null) params.put("activeDate", readRequest.getActiveDate());
		if(readRequest.getUseCache()) params.put("useCache", readRequest.getUseCache());
		if(readRequest.getFollowRedirect()) params.put("followsummaryredirects", readRequest.getFollowRedirect());
		if(readRequest.getIncludeSummaries()) params.put("includesummaries", readRequest.getIncludeSummaries());

		io.restassured.response.Response restassuredResponse = waitForExpectedStatusCode(() -> given()
				.spec(requestSpecification())
				.params(params)
				.with().contentType(contentType).get("/document"), status);


		return new GsonGenericDeserializer(restassuredResponse, type).getResponse();
	}

	public DataWithStatus update(Response.Status status, boolean synchSolr, boolean updateDisplayedDate) {
		Map<String, Object> params = new HashMap<>();
		params.put("updateDisplayedDate", updateDisplayedDate);
		params.put("synchSolr", synchSolr);
		return documentSaveResponse =  new GsonGenericDeserializer(waitForExpectedStatusCode(() -> given().spec(requestSpecification()).body(documentSaveResponse.getData())
																			  .with().contentType(contentType)
																			  .headers(headers()).params(params)
																			  .put("/document"), status), type)
				.getResponse();
	}

	public DataWithStatus update(String filePath) {
		return update(OK, filePath, false, false);
	}

	public DataWithStatus update(Response.Status status, String filePath, boolean synchSolr, boolean updateDisplayedDate) {
		Map<String, Object> params = new HashMap<>();
		params.put("updateDisplayedDate", updateDisplayedDate);
		params.put("synchSolr", synchSolr);
		return documentSaveResponse =  new GsonGenericDeserializer(waitForExpectedStatusCode(() -> given().spec(requestSpecification()).body(new File(filePath))
																										  .with().contentType(contentType)
																										  .headers(headers()).params(params)
																										  .put("/document"), status), type)
				.getResponse();
	}

	public DataWithStatus update(Response.Status status, T data) {
		return update(status, data, false, false);
	}

	public DataWithStatus update(Response.Status status, T data, boolean synchSolr, boolean updateDisplayedDate) {
		Map<String, Object> params = new HashMap<>();
		params.put("updateDisplayedDate", updateDisplayedDate);
		params.put("synchSolr", synchSolr);
		return update(status, data, params);
	}

	public DataWithStatus update(Response.Status status, T data, Map<String, Object> params) {
		return documentSaveResponse = new GsonGenericDeserializer(waitForExpectedStatusCode(() -> given().spec(requestSpecification())
																			 .body(data).with().contentType(contentType)
																			 .headers(headers())
																			 .params(params)
																			 .put("/document"), status), type)
				.getResponse();
	}

	public DataWithStatus updateAndOverrideDisplayedDate(Response.Status status) {
		return documentSaveResponse =  new GsonGenericDeserializer(waitForExpectedStatusCode(() -> given().spec(requestSpecification()).body(data).with()
																										  .contentType(contentType)
																										  .headers(headers())
																			  .put("/document/overrideDisplayedDate"), status), type)
				.getResponse();
	}

	public DataWithStatus<T> getResponse() {
		return documentSaveResponse;
	}

	public DataWithStatus fullDelete(Response.Status status, boolean forceMode) {
		SCDocument doc = (SCDocument) documentSaveResponse.getData();
		Long docId = doc.getDocId();
		return new GsonGenericDeserializer(waitForExpectedStatusCode(() -> given().spec(requestSpecification())
													  .headers(headers())
													  .with().contentType(contentType)
													  .delete("document/full?docId="+ docId + "&forceMode=" + forceMode), status), type)
				.getResponse();
	}

	public DataWithStatus importDocument(Response.Status status) {
		return new GsonGenericDeserializer(waitForExpectedStatusCode(() -> given().spec(requestSpecification())
													  .headers(headers())
													  .body(data)
													  .with().contentType(contentType)
													  .post("document/import"), status), type)
				.getResponse();
	}

	public DataWithStatus skipValidation(Response.Status status, SCDocument updateData) {
		return new GsonGenericDeserializer(waitForExpectedStatusCode(() -> given().spec(requestSpecification())
													  .headers(headers())
													  .body(updateData)
													  .with().contentType(contentType)
													  .put("document/skipValidation"), status), type)
				.getResponse();
	}

}
