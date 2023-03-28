package com.about.mantle.model.services.impl;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.extended.docv2.MetaDataEx;
import com.about.mantle.model.extended.responses.MetaDataExResponse;
import com.about.mantle.model.services.MetaDataService;

import javax.ws.rs.client.WebTarget;

public class MetaDataServiceImpl extends AbstractHttpServiceClient implements MetaDataService {

    private static final String SELENE_METADATA_PATH = "/metadata";

    public MetaDataServiceImpl(HttpServiceClientConfig config) {
        super(config);
    }

    @Override
    public MetaDataEx getMetaData(MetaDataRequestContext requestContext) {
        WebTarget webTarget = baseTarget.path(SELENE_METADATA_PATH);

        if (requestContext.getUrl() != null) {
            webTarget = webTarget.queryParam("url", requestContext.getUrl());
        }

        if (requestContext.getDocId() != null) {
            webTarget = webTarget.queryParam("docId", requestContext.getDocId());
        }

        if (requestContext.getProjection() != null) {
            webTarget = webTarget.queryParam("projection", requestContext.getProjection());
        }


        return readResponse(webTarget, MetaDataExResponse.class).getData();
    }
}
