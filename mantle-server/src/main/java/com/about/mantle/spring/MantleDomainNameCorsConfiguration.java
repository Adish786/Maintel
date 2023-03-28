package com.about.mantle.spring;

import java.net.URI;
import java.net.URISyntaxException;

import com.google.common.collect.Lists;
import com.google.common.net.InternetDomainName;

import org.springframework.web.cors.CorsConfiguration;

/**
 * Override of the CorsConfiguration to extract the domain from subdomains to make configuration of cors 
 * easier as the out of the box implementation would make configuration for squadron generated urls painful
 * this side steps that issue looking for domain instead. 
 */
public class MantleDomainNameCorsConfiguration extends CorsConfiguration {

    @Override
    public String checkOrigin(String requestOrigin) {
        String hostname = getDomainName(requestOrigin);
        boolean isAcceptedHost = super.checkOrigin(hostname) != null;

        return isAcceptedHost ? requestOrigin : null;
    }

    private String getDomainName(String requestOrigin) {
        try {
            String host = new URI(requestOrigin).getHost();
            if (host == null) return null;
            return InternetDomainName.from(host).topPrivateDomain().toString();
        } catch (URISyntaxException e) {
        	// If we are unable to parse the request origin it's invalid, so do nothing and return null
		}
        return null;
    }

    /**
     * Method builds org.springframework.web.cors.CorsConfiguration from mantle cors configs
     * MantleCorsConfigs holds default and opinionated cors configs.
     */
    public static CorsConfiguration buildCorsConfigsFromMantleConfigs(MantleCorsConfigs mantleCorsConfigs) {
        CorsConfiguration corsConfigurations = new MantleDomainNameCorsConfiguration();
        corsConfigurations.setAllowedOrigins(Lists.newArrayList(mantleCorsConfigs.getAllowedOrigins()));
        corsConfigurations.setAllowCredentials(mantleCorsConfigs.getAllowCredentials());
        corsConfigurations.setMaxAge(mantleCorsConfigs.getPreFlightMaxAgeInSeconds());
        corsConfigurations.setAllowedHeaders(mantleCorsConfigs.getHeaders());
        corsConfigurations.setExposedHeaders(mantleCorsConfigs.getHeaders());
        corsConfigurations.setAllowedMethods(mantleCorsConfigs.getHttpMethods());
        return corsConfigurations;
    }
}