package com.about.mantle.utils.selene.meta.client;

import com.about.mantle.utils.jsonfileutils.MetaFromFile;
import com.about.mantle.utils.selene.api.common.DataWithStatus;
import com.about.mantle.utils.selene.client.common.RestAssuredClient;
import com.about.mantle.utils.selene.meta.api.Meta;
import com.about.mantle.utils.selene.meta.request.ReadRequest;
import com.about.mantle.utils.selene.version.Version;
import io.restassured.common.mapper.TypeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.OK;

public class MetaClient extends RestAssuredClient {
	Logger logger = LoggerFactory.getLogger(MetaClient.class);

	private DataWithStatus<Meta> metaSaveResponse;
	private String filePath;
	private Boolean readFromJar = true;

	public MetaClient() {}

	public MetaClient(String filePath) {
		this.filePath = filePath;
	}

	public MetaClient(String filePath, boolean readFromJar) {
		this.filePath = filePath;
		this.readFromJar = readFromJar;
	}

	public DataWithStatus delete(Response.Status status) {
		DataWithStatus response = null;
		if(metaSaveResponse.getData().getDocId() != null) {
			response = delete(status, null, metaSaveResponse.getData().getDocId());
		}
		return response;
	}

	public DataWithStatus delete(Response.Status status, String url, Long docId) {
		Map<String, Object> params = new HashMap();
		if(url != null) params.put("url", url);
		if(docId != null) params.put("docId",docId);
		return waitForExpectedStatusCode(() ->
												 given().params(params).spec(requestSpecification())
														.headers(headers(Version.V1.getContentType()))
														.when()
														.delete("/metadata")
				,status)
				.as(new TypeRef<DataWithStatus<Meta>>() {});
	}

	public DataWithStatus put(Response.Status status) throws IOException {
		if(filePath != null) {
			return put(status, new MetaFromFile(filePath).meta(readFromJar));
		}
		throw new RuntimeException("this method supported only with filePath constructor");
	}

	public DataWithStatus put(Response.Status status, Meta meta) {
		return metaSaveResponse = waitForExpectedStatusCode(() -> given().spec(requestSpecification()).body(meta)
																		 .with().contentType(Version.V1.getContentType())
																		 .headers(headers(Version.V1.getContentType()))
																		 .put("/metadata"), status)
				.as(new TypeRef<DataWithStatus<Meta>>() {});
	}

	public DataWithStatus<Meta> patch(Response.Status status, boolean remove) throws IOException {
		if(filePath != null) {
			return patch(status, new MetaFromFile(filePath).meta(), remove);
		}
		throw new RuntimeException("this method supported only with filePath constructor");
	}

	public DataWithStatus<Meta> patch(Response.Status status, Meta meta, boolean remove) {
		Map<String, Object> params = new HashMap();
		if(remove) params.put("patchOperation","REMOVE");
		return metaSaveResponse = waitForExpectedStatusCode(() -> given().params(params)
																		 .spec(requestSpecification()).body(meta)
																		 .with().contentType(Version.V1.getContentType())
																		 .headers(headers(Version.V1.getContentType()))
																		 .patch("/metadata"), status)
				.as(new TypeRef<DataWithStatus<Meta>>() {});
	}

	public DataWithStatus<Meta> read(Response.Status status, ReadRequest request) {

		return waitForExpectedStatusCode(() -> given()
				.params(params(request))
				.spec(requestSpecification())
				.with().contentType(Version.V1.getContentType())
				.headers(headers(Version.V1.getContentType()))
				.get("/metadata"), status)
				.as(new TypeRef<DataWithStatus<Meta>>() {});
	}

	public String read(ReadRequest request) {
		io.restassured.response.Response response = given()
				.params(params(request))
				.spec(requestSpecification())
				.with().contentType(Version.V1.getContentType())
				.headers(headers(Version.V1.getContentType()))
				.get("/metadata");
		if(response.statusCode() == Response.Status.OK.getStatusCode()) {
			return response.prettyPrint();
		}
		logger.warn("meta data could not be retrieved for document " + request.getDocId());
		return null;
	}

	private Map<String, Object> params(ReadRequest request) {
		Map<String, Object> params = new HashMap();
		if(request.getUrl() != null) params.put("url", request.getUrl());
		if(request.getDocId() != null) params.put("docId",request.getDocId());
		return params;
	}

	public Meta setDocumentMeta(Long docId) throws IOException {
		return setDocumentMeta(docId, new MetaFromFile(filePath).meta(readFromJar));
	}

	public Meta setDocumentMeta(Long docId, Meta meta) throws IOException {
		meta.setDocId(docId);
		DataWithStatus<Meta> response = put(OK, meta);
		return response.getData();
	}

}
