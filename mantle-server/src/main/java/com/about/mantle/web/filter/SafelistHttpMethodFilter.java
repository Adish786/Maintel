package com.about.mantle.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class SafelistHttpMethodFilter extends AbstractHttpFilter {

    private final Set<String> allowedMethods;

    public SafelistHttpMethodFilter(Set<String> methods) {
        this.allowedMethods = methods.stream().map(method -> method.toUpperCase()).collect(Collectors.toSet());
    }

    @Override
    protected void doHttpFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (allowedMethods.contains(request.getMethod().toUpperCase())) {
            chain.doFilter(request,response);
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }
}
