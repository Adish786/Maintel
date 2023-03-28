package com.about.mantle.web.filter;

import com.about.mantle.app.MantleExternalConfigKeys;
import com.netflix.archaius.api.PropertyFactory;
import org.eclipse.jetty.http.HttpHeader;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter tests whether the URL appears to be for the untrusted sponsored content action and whether the request
 * is coming through the domain for untrusted sponsored content. The request proceeds if and only if the domain is
 * appropriate for the URL. (Sponsored content domain can only vend untrusted sponsored content. Normal domain can not
 * vend untrusted sponsored content.)
 */
public class SandboxedContentDomainFilter extends AbstractMantleFilter {

    private final String sandboxedContentPath;
    private final String sandboxedContentDomain;

    public SandboxedContentDomainFilter(PropertyFactory propertyFactory) {
        this.sandboxedContentPath = "/sponsor-tracking-codes";
        this.sandboxedContentDomain = propertyFactory.getProperty(MantleExternalConfigKeys.SANDBOXED_CONTENT_DOMAIN).asString("forwardco.com").get();
    }

    @Override
    public void doHttpFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Fastly should set x-forwarded-host header for requests coming through alternate domains
        String xForwardHost = request.getHeader(HttpHeader.X_FORWARDED_HOST.toString());

        // Must always compare action value to domain value
        // If protected action domain is used for normal action call, should fail with "not allowed"
        boolean isProtectedPath = request.getServletPath().equals(this.sandboxedContentPath);
        boolean isProtectedDomain = xForwardHost != null && (xForwardHost.contains(this.sandboxedContentDomain + ",")
                || xForwardHost.endsWith(this.sandboxedContentDomain));

        // The protected action should be evaluated if and only if the request is made to the
        // protected / sandbox domain
        if (isProtectedDomain != isProtectedPath) {
            HttpServletResponse servletResponse = (HttpServletResponse) response;
            servletResponse.setStatus(org.eclipse.jetty.http.HttpStatus.Code.METHOD_NOT_ALLOWED.getCode());
        } else {
            chain.doFilter(request, response);
        }
    }

}