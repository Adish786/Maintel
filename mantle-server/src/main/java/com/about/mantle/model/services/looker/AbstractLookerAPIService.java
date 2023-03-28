package com.about.mantle.model.services.looker;

import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.refresh.Refreshable;
import com.about.globe.core.refresh.exception.GlobeRefreshException;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * An AbstractLookerAPIService that is designed to have queries ran at scheduled intervals
 * This handles performing the auth and getting the query
 *
 * The child class provides the functionality for what to do with the query once it gets it. 
 */
public abstract class AbstractLookerAPIService implements Refreshable{
	
	//Note the use of webtarget is tbd based on the influx changing of how we handle connection 
	//Post keycloak we may revisit this
	private final static String AUTH = "/login";
	private final static String RUN_QUERY = "/queries/run/json";
	private final WebTarget baseTarget; 
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractLookerAPIService.class);
	private volatile boolean refreshing;
	private long refreshCount;
	private long lastRefreshTime;
	private long lastRefreshDuration;
	private Form authForm;
	
	
	/**
	 * Creates the service 
	 * 
	 * @param api - the base url for the looker api
	 * @param clientId - client id used for authentication
	 * @param secret - secret used for authentication
	 */
	public AbstractLookerAPIService (String api, String clientId, String secret) {
		authForm = new Form();
		authForm.param("client_id", clientId);
		authForm.param("client_secret", secret);
		
		JerseyClient client = new JerseyClientBuilder().register(JacksonJsonProvider.class).build();
		client.property(ClientProperties.CONNECT_TIMEOUT, 10000);
		client.property(ClientProperties.READ_TIMEOUT, 10000); //time out at 10s
		this.baseTarget = client.target(api);
	}
	
	protected abstract void handleQueryResults(Response response);
	
	protected abstract LookerQueryRequest getQuery();
	
	protected static LookerQueryRequest buildQuery(String model, String view, String limit,
			String rowTotal, List<String> fields, List<String> pivots, Map<String,String> filters) {
		
		LookerQueryRequest query = new LookerQueryRequest();
		query.setModel(model);
		query.setView(view);
		query.setLimit(limit);
		query.setRow_total(rowTotal);
		query.setQuery_timezone("America/New_York");
		query.setFields(fields);
		query.setPivots(pivots);
		query.setFilters(filters);
		return query;
	}
	
	/**
	 * Runs the query and passes a 200 response to the handler
	 */
	private void runQuery() {
		Response response = null;
		
		try{
			response = baseTarget.path(RUN_QUERY).request()
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("content-type", MediaType.APPLICATION_JSON_TYPE)
					.header(HttpHeaders.AUTHORIZATION, "token " + getAccessToken())
					.post(Entity.json(getQuery()));
		}catch (Exception e){
			throw new GlobeException("Looker failed to run query", e);
		}
		
		if(response.getStatus() != 200) {
			throw new GlobeException(buildLookerError("Query", response));
		}
	
		handleQueryResults(response);
	}

	@Override
	public boolean isRefreshing() {
		return refreshing;
	}

	@Override
	public  void refresh() throws GlobeRefreshException{
		if (refreshing) {
			logger.warn("Ignoring Looker refresh, one is already in progress");
			return;
		}

		long start = System.currentTimeMillis();

		try {
			refreshing = true;

			try {
				runQuery();
			} catch (Exception e) {
			    throw new GlobeRefreshException("Error refreshing Query from Looker ", e);
			}

			refreshCount += 1;
			lastRefreshTime = System.currentTimeMillis();
			lastRefreshDuration = lastRefreshTime - start;
		} finally {
			refreshing = false;
		}
	}
	

	@Override
	public long getRefreshCount() {
		return refreshCount;
	}

	@Override
	public long getLastRefreshTime() {
		return lastRefreshTime;
	}

	@Override
	public long getLastRefreshDuration() {
		return lastRefreshDuration;
	}
	
	/** 
	 * Returns header token
	 * @return
	 */
	private String getAccessToken() {
		
		Response response = null;
		
		try{
			response = baseTarget.path(AUTH).request()
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("content-type", MediaType.APPLICATION_FORM_URLENCODED)
					.post(Entity.form(authForm));
		}catch (Exception e){
			throw new GlobeException("Looker failed to get Authentication", e);
		}
		
		if(response.getStatus() != 200) {
			throw new GlobeException(buildLookerError("Authentication", response));
		}
		
		return response.readEntity(LookerAPIAuthResponse.class).getAccess_token();
	}
	
	/**
	 * Used to generate exceptions if the LookerAPI returns an error status 
	 * 
	 * @param apiCallInError - what call failed
	 * @param erroredResponse - the response to extract the message and documentation error
	 * 
	 * @return the string for the exception being thrown
	 */
	private String buildLookerError(String apiCallInError, Response erroredResponse) {
		LookerErrorResponse error = erroredResponse.readEntity(LookerErrorResponse.class);
		return "Looker failed to get " + apiCallInError +" with response "+erroredResponse.getStatus()+ "With message "+ error.getMessage() +" and documentation url"+ error.getDocumentation_url();
	}
}
