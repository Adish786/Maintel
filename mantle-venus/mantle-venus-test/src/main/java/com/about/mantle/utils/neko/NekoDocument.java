package com.about.mantle.utils.neko;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.OK;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.HttpHeaders;

import org.json.JSONObject;

import com.about.mantle.utils.selene.restassured.RequestSpec;
import com.about.mantle.utils.selene.version.Version;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public abstract class NekoDocument {

	public abstract String getNeko();

	public JSONObject getProduct(String blockID) {

		await().atMost(120, TimeUnit.SECONDS).pollInterval(2, TimeUnit.SECONDS)
				.until(() -> given().spec(requestSpecification(getNeko())).with()
						.contentType(Version.V1.getContentType()).get("/product/" + blockID).statusCode() == 200);

		Response response = given()
				.spec(requestSpecification(getNeko()))
				.with()
				.contentType(Version.V1.getContentType())
				.get("/product/" + blockID);
		response.then().statusCode(OK.getStatusCode());

		return (new JSONObject(response.asString())).getJSONObject("data");	
	}

	public RequestSpecification requestSpecification(String neko) {
		return RequestSpec.getInstance(neko);
	}

	public Headers headers(String version) {
		List<Header> headers = new ArrayList<>();
		headers.add(new Header(HttpHeaders.ACCEPT, version));
		headers.add(new Header(HttpHeaders.CONTENT_TYPE, version));
		return new Headers(headers);
	}

	public Headers headers() {
		return headers(Version.V1.getContentType() + ";charset=UTF-8");
	}
}
