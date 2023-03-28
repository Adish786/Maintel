package com.about.mantle.utils.selene.client.common;

import com.about.hippodrome.config.servicediscovery.Service;
import com.about.mantle.utils.selene.auth.Authentication;
import com.about.mantle.utils.selene.restassured.RequestSpec;
import com.about.mantle.utils.selene.version.Version;
import com.about.mantle.utils.servicediscovery.SeleneServiceDiscovery;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.awaitility.Awaitility.await;

public class RestAssuredClient {

	Logger logger = LoggerFactory.getLogger(RestAssuredClient.class);

	private String signedToken;
	private Service seleneService = null;

	/*
        @param supplier of Response, this is the code that we want to execute to get selene response
        @param status an expected status code for the supplier
        @return response that was received after executing supplier
     */
	public Response waitForExpectedStatusCode(Supplier<Response> supplier, javax.ws.rs.core.Response.Status status) {
		Response response = supplier.get();
		int count = 0;
		while(response.statusCode() != status.getStatusCode() && count++ <= 3) {
			await().atMost(10, TimeUnit.SECONDS);
			response = supplier.get();
		}
		response.then().assertThat().statusCode(status.getStatusCode());
		return response;
	}

	/*
	Get one signed token per test run
	 */
	protected String signedToken() {
		if(signedToken == null) {
			signedToken = Authentication.getToken();
		}
		return signedToken;
	}

	/*
	Use service discovery for Selene url and
	build request specification for REST-assured
	 */
	public RequestSpecification requestSpecification() {
		return RequestSpec.getInstance(SeleneServiceDiscovery.getInstance());
	}

	/*
	Request Headers for version
	 */
	public Headers headers(String version) {
		List<Header> headers = new ArrayList<>();
		headers.add(new Header(HttpHeaders.ACCEPT, version));
		headers.add(new Header(HttpHeaders.CONTENT_TYPE, version));
		headers.add(new Header("Selene-Client-Id", "venus-automation"));
		headers.add(new Header(HttpHeaders.AUTHORIZATION, new StringBuilder().append("Token ").append(signedToken()).toString()));
		return new Headers(headers);
	}
	/*
	Request headers with signed token and version v2
	 */
	public Headers headers() {
		return headers(Version.V2.getContentType() + ";charset=UTF-8");
	}

	public Headers v1Headers() {
		return headers(Version.V1.getContentType() + ";charset=UTF-8");
	}
}
