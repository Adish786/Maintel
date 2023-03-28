package com.about.mantle.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ResponseHeadersFilter implements Filter {
    
    public static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";
    public static final String REFERRER_POLICY = "Referrer-Policy";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // HSTS Header
        // Set to 6 months. To remove security from browser, set max-age to 0
        ((HttpServletResponse)response).setHeader(STRICT_TRANSPORT_SECURITY, "max-age=15552000");

        // Ensure full referrer is sent to 3rd party sites (e.g. skimlinks)
        ((HttpServletResponse)response).setHeader(REFERRER_POLICY, "no-referrer-when-downgrade");

        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    }

}
