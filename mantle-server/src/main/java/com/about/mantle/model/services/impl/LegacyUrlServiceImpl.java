package com.about.mantle.model.services.impl;

import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.LegacyUrlResultEx;
import com.about.mantle.model.extended.responses.LegacyUrlResponseEx;
import com.about.mantle.model.services.LegacyUrlService;

import javax.ws.rs.client.WebTarget;

public class LegacyUrlServiceImpl extends AbstractHttpServiceClient implements LegacyUrlService {
    private final static String LEGACY_URL_PATH = "/legacyurl";
    private final String vertical;

    public LegacyUrlServiceImpl(HttpServiceClientConfig httpClientConfig, String vertical) {
        super(httpClientConfig);
        this.vertical = vertical;
    }

    private WebTarget createWebTarget(long cursor, int limit){
        WebTarget webTarget = baseTarget.path(LEGACY_URL_PATH);
        webTarget = webTarget.queryParam("vertical", vertical);
        webTarget = webTarget.queryParam("cursor", cursor);
        webTarget = webTarget.queryParam("limit", limit);

        return webTarget;
    }

    @Override
    public LegacyUrlResultEx getUrlList(long cursor, int limit) {
        WebTarget webTarget = createWebTarget(cursor, limit);
        LegacyUrlResponseEx response = readResponse(webTarget, LegacyUrlResponseEx.class);
        return response.getData();
    }
}
