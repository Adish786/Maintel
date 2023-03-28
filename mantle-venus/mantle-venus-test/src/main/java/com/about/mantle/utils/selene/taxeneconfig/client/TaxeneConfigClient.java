package com.about.mantle.utils.selene.taxeneconfig.client;

import com.about.mantle.utils.selene.client.common.RestAssuredClient;
import com.about.mantle.utils.selene.taxeneconfig.request.WriteRequest;
import com.about.mantle.utils.selene.version.Version;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;

public class TaxeneConfigClient extends RestAssuredClient {
	private static final String CONTENT_TYPE = Version.V1.getContentType();

	private WriteRequest request;

	public TaxeneConfigClient(WriteRequest request) {
		this.request = request;
	}

	public void create(Response.Status status) {
		StringBuilder query = new StringBuilder("?x=1");
		if (this.request.getDocId() != null)
			query.append("&docId=").append(this.request.getDocId());
		if (StringUtils.isNotEmpty(this.request.getConfigName()))
			query.append("&configName=").append(this.request.getConfigName());
		waitForExpectedStatusCode(() -> given().spec(requestSpecification())
											   .headers(headers(CONTENT_TYPE))
											   .body(this.request.getConfig())
											   .with().post("/taxeneconfig" + query),
								  Response.Status.OK);


	}

	public io.restassured.response.Response read(Response.Status status) {
		return waitForExpectedStatusCode(
				() -> given().spec(requestSpecification()).with().contentType(CONTENT_TYPE)
							 .get("/taxeneconfig/" + request.getDocId()), status);
	}

	public void pollTaxeneConfig() {
		await().atMost(120, TimeUnit.SECONDS).pollInterval(2, TimeUnit.SECONDS).until(() -> read(Response.Status.OK).statusCode() == 200);
	}
}
