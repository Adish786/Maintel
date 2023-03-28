package com.about.mantle.model.shared_services.responses;

import com.about.hippodrome.models.response.BaseResponse;
import com.about.hippodrome.models.response.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This base response can be used for all legacy meredith's shared services
 */
public class SharedServicesBaseResponse<T> extends BaseResponse<T> {

    //Overriding & jsonIgnoring as json response from legacy meredith's shared-service api doesn't provide status
    @Override
    @JsonIgnore
    public Status getStatus() {
        return null;
    }
}
