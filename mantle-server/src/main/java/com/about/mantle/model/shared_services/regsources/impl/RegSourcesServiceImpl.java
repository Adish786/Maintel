package com.about.mantle.model.shared_services.regsources.impl;

import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.shared_services.regsources.RegSourcesService;
import com.about.mantle.model.shared_services.regsources.response.RegSource;
import com.about.mantle.model.shared_services.regsources.response.RegSourceResponse;
import com.google.common.collect.ImmutableMap;

import javax.ws.rs.client.WebTarget;
import java.util.Map;

public class RegSourcesServiceImpl extends AbstractHttpServiceClient implements RegSourcesService {

    private static final String REG_SOURCES_PATH = "/regsources";
    private final Map<String, Object> authHeader;

    public RegSourcesServiceImpl(HttpServiceClientConfig httpServiceClientConfig, Map<String, Object> authHeader) {
        super(httpServiceClientConfig);
        this.authHeader = authHeader;
    }

    @Override
    public RegSource getRegSourceById(String id) {
        WebTarget webTarget = baseTarget.path(REG_SOURCES_PATH).path(id);
        RegSourceResponse regSourceResponse =  readResponse(webTarget, RegSourceResponse.class,
                null, null, null, authHeader);
        return regSourceResponse == null ? null : regSourceResponse.getData();
    }

}
