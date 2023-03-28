package com.about.mantle.model.services.ugc.impl;

import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.services.ugc.UGCUserService;
import com.about.mantle.model.services.ugc.dto.UGCUserDto;
import com.about.mantle.model.services.ugc.response.UGCUserResponse;

import javax.ws.rs.client.WebTarget;

public class UGCUserServiceImpl extends AbstractHttpServiceClient implements UGCUserService {
    private static final String USER_PATH = "/user";

    public UGCUserServiceImpl(HttpServiceClientConfig httpServiceClientConfig) {
        super(httpServiceClientConfig);
    }

    @Override
    public UGCUserDto getUgcUserDtoById(String id) {
        WebTarget webTarget = baseTarget.path(USER_PATH).path(id);
        UGCUserResponse userResponse =  readResponse(webTarget, UGCUserResponse.class);
        return userResponse == null ? null : userResponse.getData();
    }
}