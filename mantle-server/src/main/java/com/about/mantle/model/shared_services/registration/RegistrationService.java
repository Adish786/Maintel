package com.about.mantle.model.shared_services.registration;

import com.about.mantle.model.shared_services.registration.request.RegistrationRequestBody;
import com.about.mantle.model.shared_services.registration.response.RegistrationResponse.RegistrationResponseData;

/**
 * Service for sending registrations to legacy meredith's shared services
 * Api url: https://(test.)shared-services.meredithcorp.io/registration
 */
public interface RegistrationService {

    RegistrationResponseData postRegistration(RegistrationRequestBody registrationRequestBody);

}
