package com.about.mantle.model.services.impl;

import com.about.globe.core.exception.GlobeException;
import com.about.hippodrome.config.servicediscovery.Service;
import com.about.mantle.model.services.ExternalComponentService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides some baseline functionality for a {@link com.about.mantle.model.services.ExternalComponentService},
 * including naming, service discovery, and adding itself to the registry.
 */
public abstract class AbstractExternalComponentService implements ExternalComponentService {

    private final String name;
    private final URI serviceUri;
    private final String version;

    public AbstractExternalComponentService(String name, Service externalService) {
        this.name = name;
        this.version = externalService.getVersion();
        this.serviceUri = resolveUriFromService(externalService);
    }

    /**
     * If only one URI is available, will return that, otherwise will filter out non-https URIs, afterwhich the code
     * expects that there'll be only one URI remaining.  If the resolution logic is more complicated,
     * a subclass will need to override.
     * @param externalService
     * @return
     */
    protected URI resolveUriFromService(Service externalService) {

        URI answer = null;
        List<URI> availableUris = externalService.getUris();

        if (availableUris.size() == 1) {
            answer = availableUris.get(0);
        } else {

            List<URI> filteredUris = availableUris.stream()
                    .filter(uri -> "https".equals(uri.getScheme()))
                    .filter(uri -> !uri.getHost().startsWith("www"))
                    .collect(Collectors.toList());

            if (filteredUris.size() > 1) {
                throw new GlobeException("Could not filter URIs for service '" + externalService.getName() + "' to a single " +
                        "url.  URIs after filtering: " + filteredUris);
            } else if (filteredUris.size() == 0) {
                throw new GlobeException("No viable URLs found for service '" + externalService.getName() + "'.  Non-" +
                        "viable URIs: " + availableUris);
            }

            answer = filteredUris.get(0);
        }

        return answer;

    }

    @Override
    public URI getUri() {
       return serviceUri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
	public String getVersion() {
		return version;
	}
}
