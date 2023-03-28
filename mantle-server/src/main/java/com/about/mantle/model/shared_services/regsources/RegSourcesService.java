package com.about.mantle.model.shared_services.regsources;

import com.about.mantle.model.shared_services.regsources.response.RegSource;

/**
 * Service for registration sources data from legacy meredith's shared services
 * Api url: https://(test.)shared-services.meredithcorp.io/regsources/{regSourceId}
 */
public interface RegSourcesService {

    RegSource getRegSourceById(String id);
}
