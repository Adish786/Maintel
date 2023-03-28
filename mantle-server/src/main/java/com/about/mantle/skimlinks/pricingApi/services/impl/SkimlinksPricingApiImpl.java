package com.about.mantle.skimlinks.pricingApi.services.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.skimlinks.pricingApi.model.SkimlinksItem;
import com.about.mantle.skimlinks.pricingApi.services.SkimlinksPricingApi;

public class SkimlinksPricingApiImpl implements SkimlinksPricingApi {
	
	private Logger logger = LoggerFactory.getLogger(SkimlinksPricingApiImpl.class);
	
	private final WebTarget modelTarget;
	private final String apiKey;
	private final String skimlinksId;

	public SkimlinksPricingApiImpl(String baseUrl, String apiKey, String skimlinksId){
		this.modelTarget = createClient().target(baseUrl);
		this.apiKey = apiKey;
		this.skimlinksId = skimlinksId;
}
	
	@Override
	public Map<String, SkimlinksItem> lookupItems(Collection<String> urls) {
		
		logger.debug("Requesting Skimlinks pricing data for urls `{}`", urls);
		
		WebTarget webTarget = modelTarget.path("skimlinks-v1.0").path("scraper").path("sale-prices");
		
		Response response;
		try{
			response = webTarget.request().header("x-api-key", apiKey)
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.header("content-type", MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(buildRequest(skimlinksId, urls)));
		} catch(Exception e) {
			throw new GlobeException("Skimlinks connection failed", e);
		}
		
		Map<String, SkimlinksItem> m = response.readEntity(new GenericType<Map<String, SkimlinksItem>>() {});
		
		return m;
	}
	
	private Map<String, Object> buildRequest(String skimlinksId, Collection<String> urls){
		Map<String, Object> request = new HashMap<>();
		request.put("id", skimlinksId);
		request.put("links", urls);
		return request;
	}
	
	private static Client createClient() {
        ClientConfig clientConfig = new ClientConfig();
        ConnectorProvider provider = new ApacheConnectorProvider();
        clientConfig.connectorProvider(provider);
        Client client = ClientBuilder.newBuilder().withConfig(clientConfig).hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String requestedHost, SSLSession remoteServerSession) {
                return requestedHost.equalsIgnoreCase(remoteServerSession.getPeerHost());
            }
        }).build();
        return client;
    }

}
