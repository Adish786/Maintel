package com.about.mantle.utils.selene.auth.client;

import com.about.mantle.utils.selene.api.common.DataWithStatus;
import com.about.mantle.utils.selene.auth.api.SignedToken;
import com.about.mantle.utils.selene.auth.api.Token;
import com.about.mantle.utils.selene.client.common.RestAssuredClient;
import com.about.mantle.utils.selene.version.Version;
import io.restassured.common.mapper.TypeRef;
import org.junit.Assert;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.OK;

public class AuthenticationClient extends RestAssuredClient {

	public String getSignedToken(String username, String secret) {
		Token token = Token.builder().username(username).secret(secret).build();
		DataWithStatus<SignedToken> response = token(OK, token);
		String signedToken = response.getData().getSignedToken();
		Assert.assertFalse("POST Token response signedToken is empty", signedToken.isEmpty());
		return signedToken;
	}

	public DataWithStatus token(Response.Status status, Token token) {
		return token(status, token, true);
	}

	public DataWithStatus<SignedToken> token(Response.Status status, Token token, boolean secure) {
		return waitForExpectedStatusCode(() -> given()
				.spec(requestSpecification())
				.body(token)
				.with().contentType(Version.V2.getContentType()).post("/auth/token"), status)
				.as(new TypeRef<DataWithStatus<SignedToken>>() {});
	}
}
