package com.about.mantle.model.services.client.selene;

import java.io.Serializable;

import com.about.hippodrome.jwt.model.JsonWebTokenData;
import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.models.response.Status;
import com.about.hippodrome.restclient.CredentialsProvider;
import com.about.hippodrome.restclient.jwt.JwtAuthClient;
import com.about.hippodrome.restclient.jwt.JwtAuthTokenProvider;

/**
 * This is injected in {@link JwtAuthTokenProvider}.
 * {@link JwtAuthTokenProvider} uses this to fetch the token and validate it periodically.
 * {@link SeleneAuthServiceHttpClient} is used to call Selene Auth API which gives back a token.
 */

public class JwtAuthClientImpl implements JwtAuthClient {
	private SeleneAuthServiceHttpClient seleneAuthClient;

	public JwtAuthClientImpl(SeleneAuthServiceHttpClient seleneAuthClient) {
		this.seleneAuthClient = seleneAuthClient;
	}

	@Override
	public String authenticate(CredentialsProvider credentialsProvider) {
		UserCredentials userCredentials = new UserCredentials(credentialsProvider.getUserName(),
				credentialsProvider.getSecret());

		GrantedAuthenticationResponse response = seleneAuthClient.authenticate(userCredentials,
				GrantedAuthenticationResponse.class);
		if (response != null && response.getStatus().getCode().equals(Status.Code.SUCCESS)) {
			return response.getData().getSignedToken();
		} else {
			return null;
		}
	}

	@Override
	public boolean validate(String signedToken) {
		AuthenticationValidationResponse response = seleneAuthClient.validate(signedToken,
				AuthenticationValidationResponse.class);
		return response != null && response.getStatus().getCode().equals(Status.Code.SUCCESS);
	}

	public static class GrantedAuthenticationResponse extends BaseResponse<GrantedAuthentication>
			implements Serializable {
		private static final long serialVersionUID = 1L;
	}

	public static class AuthenticationValidationResponse extends BaseResponse<Boolean> implements Serializable {
		private static final long serialVersionUID = 1L;
	}

	public static class GrantedAuthentication implements Serializable {
		private static final long serialVersionUID = 1L;

		private String signedToken;
		private JsonWebTokenData token;

		public String getSignedToken() {
			return signedToken;
		}

		public void setSignedToken(String signedToken) {
			this.signedToken = signedToken;
		}

		public JsonWebTokenData getToken() {
			return token;
		}

		public void setToken(JsonWebTokenData token) {
			this.token = token;
		}

	}
}