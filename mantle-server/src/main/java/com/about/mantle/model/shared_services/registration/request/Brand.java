package com.about.mantle.model.shared_services.registration.request;

import com.about.mantle.model.shared_services.registration.request.brands.parents.Parents;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({ @JsonSubTypes.Type(value = Parents.class, name = "parents") })
public abstract class Brand {}
