package com.about.mantle.service.selene.authentication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.about.mantle.model.services.document.preprocessor.DocumentPreprocessor;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.about.hippodrome.restclient.AuthTokenProvider;
import com.about.hippodrome.restclient.CredentialsProvider;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.hippodrome.restclient.jwt.JwtAuthTokenProvider;
import com.about.mantle.htmlslicing.HtmlSlicerConfig;
import com.about.mantle.infocat.services.ProductService;
import com.about.mantle.infocat.services.impl.ProductServiceImpl;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.services.DocumentService;
import com.about.mantle.model.services.DocumentService.DocumentReadRequestContext;
import com.about.mantle.model.services.client.ServiceClientUtils.ClientMediaType;
import com.about.mantle.model.services.client.selene.CredentialsProviderImpl;
import com.about.mantle.model.services.client.selene.JwtAuthClientImpl;
import com.about.mantle.model.services.client.selene.SeleneAuthServiceHttpClient;
import com.about.mantle.model.services.impl.DocumentServiceImpl;

/**
 * This is for dev testing only, shouldn't run as part of regular build. That's why it's ignored. Account used in this
 * test only has permission on QA Selene. Certain tests are to be run exclusively. Few need debug changes, everything
 * needed is mentioned inline.
 * 
 * @author spatil
 *
 */

public class ServiceWithSeleneAuthTest {

	private static final String CORRECT_SECRET = "f9Y%#Pj2";
	private static final String INCORRECT_SECRET = "THIS-IS-BAD-PASSWORD";
	final String SELENE_SECURE_BASE_URL_KEY = "https://selene-stable-us-east-1.internal1.continual.app.qa.aws.dotdash.com/";
	final String SELENE_BASE_URL_KEY = "http://selene-stable-us-east-1.internal1.continual.app.qa.aws.dotdash.com/";
	final long numberOfOperation = 1000;
	final String USER_ACCOUNT = "mantle-ref.qa@dotdash.com";
	final long docId = 4156414L;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	

	//@Test
	public void testWithCorrectCredentials() {
		BaseDocumentEx document = testServiceWithSeleneAuthentication(true, false, true, false);
		assertNotNull(document);
	}

	//@Test
	public void testWithIncorrectCredentials() {
		thrown.expect(RuntimeException.class);
		BaseDocumentEx document = testServiceWithSeleneAuthentication(false, false, true, false);
		assertNull(document);
	}

	/**
	 * This test tries to simulate failure in token validation from Selene. After 10 failed requests (403) from Selene,
	 * app should switch back to no-auth. CredentialsProvider is mocked to return bad secrete in second attempt. see
	 * getCorrectConsulJwtCredentialsProvider method. There is no other way to simulate 403s from Selene except
	 * tampering (in fetchToken method) of token manually in debug mode. By changing expirationDuration in
	 * SeleneJwtProvider, we make sure that token is fetched at 5 mins interval and not default interval of 18 hours.
	 * Note: This should be run in exclusion, ignore all other test before running this one.
	 * 
	 * @throws InterruptedException
	 */
	//@Ignore
	//@Test
	public void testWithIncorrectCredentials10Times() throws InterruptedException {
		BaseDocumentEx document = null;
		DocumentService service = getDocumentService(true, true, false, false);
		// put a debug point at next statement and tamper signedToken in SeleneJwtProvider class.
		// and this test should pass
		for (int i = 0; i < 12; i++) {
			document = getDocumentFromService(service);
			if (i < 11) assertNull(document);
		}
		assertNotNull(document);
	}

	/**
	 * All further tests are to be run exclusively. Ignore all other tests while running each one of following.
	 */

	//@Test
	public void testWithHttpsWithAuth() throws InterruptedException {
		logTimeTaken(getResponseTimeConsumer(), "WithHttpsWithAuth", getDocumentService(true, false, false, false));
	}
	
	//testing with bad credentials but with auth disabled, bad credentials shoudn't matter in this case
	//@Test
	public void testWithHttpsWithoutAuthWithBadCredentials() throws InterruptedException {
		logTimeTaken(getResponseTimeConsumer(), "WithHttpsWithoutAuth", getDocumentService(false, false, false, true));
	}
	
	private BiConsumer<String, DocumentService> getResponseTimeConsumer() {
		return (operationName, service) -> {
			BaseDocumentEx document = null;
			for (int i = 0; i < numberOfOperation; i++) {
				document = getDocumentFromService(service);
				assertNotNull(document);
			}
			assertNotNull(document);
		};
	}

	private void logTimeTaken(BiConsumer<String, DocumentService> responseTimeBiConsumer, String operation,
			DocumentService service) {
		long start = System.currentTimeMillis();
		responseTimeBiConsumer.accept(operation, service);
		System.out.println(String.format("Took %d millis per operation  for %s",
				((System.currentTimeMillis() - start)) / numberOfOperation, operation));

	}

	private BaseDocumentEx testServiceWithSeleneAuthentication(boolean testWithCorrectCredentials,
			boolean testFailureInTheMiddle, boolean isAuthRequired, boolean disableAuth) {
		DocumentService service = getDocumentService(testWithCorrectCredentials, testFailureInTheMiddle, isAuthRequired, disableAuth);

		return getDocumentFromService(service);
	}

	private DocumentService getDocumentService(boolean testWithCorrectCredentials, boolean testFailureInTheMiddle, boolean isAuthRequired, boolean disableAuth) {
		List<DocumentPreprocessor> documentPreprocessors = new ArrayList<>();
		return new DocumentServiceImpl(defaultClientConfig("ServiceWithSeleneAuthTest.class", testWithCorrectCredentials, testFailureInTheMiddle, isAuthRequired, disableAuth),documentPreprocessors);
	}

	private BaseDocumentEx getDocumentFromService(DocumentService service) {
		
		DocumentReadRequestContext.Builder documentRequestContext = new DocumentReadRequestContext.Builder().setDocId(docId);

		return service.getDocument(documentRequestContext.build());
	}

	private HttpServiceClientConfig defaultClientConfig(String clientName, boolean withCorrectCrendentials,
			boolean testFailureInTheMiddle, boolean isAuthRequired, boolean disableAuth) {
		HttpServiceClientConfig.Builder httpConfigBuilder = new HttpServiceClientConfig.Builder();
		httpConfigBuilder.setBaseUrl(SELENE_SECURE_BASE_URL_KEY);
		HttpServiceClientConfig.ClientId clientId = new HttpServiceClientConfig.ClientId("Selene-Client-id", "Junit");
		httpConfigBuilder.setClientId(clientId);
		httpConfigBuilder.setMediaType(ClientMediaType.SMILE_V2.getMediaType());
		AuthTokenProvider authTokenProvider = null;
		httpConfigBuilder.setAuthenticationRequired(isAuthRequired);
		httpConfigBuilder.setMaxNumberOf403ResponseStatus(10);

		if (!disableAuth) {
			if (withCorrectCrendentials && testFailureInTheMiddle)
				authTokenProvider = authTokenProviderWithCorrectCredentialAlwaysFetchToken();
			else if (!withCorrectCrendentials)
				authTokenProvider = authTokenProviderWithIncorrectCredential();
			else
				authTokenProvider = authTokenProviderWithCorrectCredential();

			httpConfigBuilder.setAuthTokenProvider(authTokenProvider);
		}
		return httpConfigBuilder.build();
	}
	
	private HttpServiceClientConfig defaultClientConfigForAuthTokenProvider() {
		HttpServiceClientConfig.Builder httpConfigBuilder = new HttpServiceClientConfig.Builder();
		httpConfigBuilder.setBaseUrl(SELENE_SECURE_BASE_URL_KEY);
		httpConfigBuilder.setMediaType(ClientMediaType.SMILE_V2.getMediaType());
		return httpConfigBuilder.build();
	}
	
	private AuthTokenProvider authTokenProviderWithCorrectCredential() {
		CredentialsProvider credentialsProvider = getCorrectConsulJwtCredentialsProvider();
		SeleneAuthServiceHttpClient authClient = new SeleneAuthServiceHttpClient(defaultClientConfigForAuthTokenProvider());
		return new JwtAuthTokenProvider(new JwtAuthClientImpl(authClient), credentialsProvider, null);
	}

	// Token expiration is set to 24, causing it to invalidate token every five minutes and fetch a new one
	private AuthTokenProvider authTokenProviderWithCorrectCredentialAlwaysFetchToken() {
		CredentialsProviderImpl credentialsProvider = getCorrectConsulJwtCredentialsProvider();
		SeleneAuthServiceHttpClient authClient = new SeleneAuthServiceHttpClient(defaultClientConfigForAuthTokenProvider());
 		return new JwtAuthTokenProvider(new JwtAuthClientImpl(authClient), credentialsProvider, null);
	}

	private AuthTokenProvider authTokenProviderWithIncorrectCredential() {
		CredentialsProviderImpl credentialsProvider = getConsulJwtIncorrectCredentialsProvider();
		SeleneAuthServiceHttpClient authClient = new SeleneAuthServiceHttpClient(defaultClientConfigForAuthTokenProvider());
		return new JwtAuthTokenProvider(new JwtAuthClientImpl(authClient), credentialsProvider, null);
		
	}

	private CredentialsProviderImpl getCorrectConsulJwtCredentialsProvider() {
		CredentialsProviderImpl consulJwtCredentialsProvider = mock(CredentialsProviderImpl.class);
		when(consulJwtCredentialsProvider.getUserName()).thenReturn(USER_ACCOUNT);
		// this is done to simulate if token can't be granted and after 10 attempts it should switch to no-auth
		when(consulJwtCredentialsProvider.getSecret()).thenReturn(CORRECT_SECRET).thenReturn(INCORRECT_SECRET);
		return consulJwtCredentialsProvider;
	}

	private CredentialsProviderImpl getConsulJwtIncorrectCredentialsProvider() {
		CredentialsProviderImpl consulJwtCredentialsProvider = mock(CredentialsProviderImpl.class);
		when(consulJwtCredentialsProvider.getUserName()).thenReturn(USER_ACCOUNT);
		when(consulJwtCredentialsProvider.getSecret()).thenReturn(INCORRECT_SECRET);
		return consulJwtCredentialsProvider;
	}
}
