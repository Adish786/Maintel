package com.about.mantle.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Convenience class for creating HTTP filters
 */
public abstract class AbstractHttpFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ; // no init
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = null;
        HttpServletResponse httpServletResponse = null;

        try {
            httpServletRequest = (HttpServletRequest) request;
            httpServletResponse = (HttpServletResponse) response;
        } catch (ClassCastException e) {
            throw new RuntimeException("AbstractHttpFilter can only be used for HTTP Filters", e);
        }

        doHttpFilter(httpServletRequest, httpServletResponse, chain);

    }

    protected abstract void doHttpFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException;

    @Override
    public void destroy() {
        ; // no destroy
    }
}
