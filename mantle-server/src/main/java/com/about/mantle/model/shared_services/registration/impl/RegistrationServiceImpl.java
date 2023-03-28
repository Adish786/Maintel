package com.about.mantle.model.shared_services.registration.impl;

import com.about.hippodrome.models.request.HttpMethod;
import com.about.hippodrome.restclient.http.AbstractHttpServiceClient;
import com.about.hippodrome.restclient.http.HttpServiceClientConfig;
import com.about.mantle.model.shared_services.registration.RegistrationService;
import com.about.mantle.model.shared_services.registration.request.RegistrationRequestBody;
import com.about.mantle.model.shared_services.registration.response.RegistrationResponse;
import com.about.mantle.model.shared_services.registration.response.RegistrationResponse.RegistrationResponseData;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import java.util.Map;

public class RegistrationServiceImpl extends AbstractHttpServiceClient implements RegistrationService {

    private static final String REGISTRATION_PATH = "/registration";
    private final Map<String, Object> authHeader;

    public RegistrationServiceImpl(HttpServiceClientConfig httpServiceClientConfig, Map<String, Object> authHeader) {
        super(httpServiceClientConfig);
        this.authHeader = authHeader;
    }

    /**
     * @param registrationRequestBody Registration request body
     * @return {@link RegistrationResponseData}
     * There are two type of success(200) responses: One where {@linkplain RegistrationResponseData#getEmailReputation()} is SAFE_TO_EMAIL & other is DO_NOT_EMAIL.
     * In case of non success response will be null
     */
    @Override
    public RegistrationResponseData postRegistration(RegistrationRequestBody registrationRequestBody) {
        WebTarget webTarget = baseTarget.path(REGISTRATION_PATH);
        RegistrationResponse registrationResponse = readResponse(webTarget, RegistrationResponse.class,
                HttpMethod.POST, Entity.json(registrationRequestBody), null, authHeader);
        return registrationResponse == null ? null : registrationResponse.getData();
    }
}
