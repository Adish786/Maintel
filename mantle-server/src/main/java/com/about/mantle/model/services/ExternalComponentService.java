package com.about.mantle.model.services;

import java.net.URI;

import com.about.globe.core.http.RequestContext;
import com.about.hippodrome.url.UrlData;
import com.about.mantle.model.ExternalComponent;

/**
 * Provides an {@link ExternalComponent} from an external source. Basically a
 * way to have an outside server
 * render HTML and provide javascript, and we plop it in as though we rendered
 * it.
 */
public interface ExternalComponentService {

    /**
     * Gets the {@link ExternalComponent} by its component id for the URL being
     * requested on the vertical.
     * 
     * @param componentId    the ID of the component on the remote server to fetch
     * @param requestUrl     the Url of _this_ request which is used to create the
     *                       Url of the remote request
     * @param uuid           The unique identifier of the structured content block
     *                       requested if applicable
     * @param optional       is the requested component optional for display
     * @param requestContext the request context of _this_ request which can be
     *                       deconstructed and passed with the remote request
     */
    ExternalComponent getExternalComponent(String componentId, UrlData requestUrl, String uuid, Boolean optional,
            RequestContext requestContext);

    /**
     * The URI of the external service
     */
    URI getUri();

    /**
     * Name of the external service. Used by the
     * {@link ExternalComponentServiceRegistry} to be able to find external
     * services dynamically
     * 
     * @return
     */
    String getName();

    /**
     * Gets version of the external service. Used by the gtm task to report what
     * version of an external service is being used.
     */
    String getVersion();
}
