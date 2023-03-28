package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class PrivacyRequestController extends AbstractMantleEndpointController {

    private MantleRequestHandlerMethods handlerMethods;

    public PrivacyRequestController(MantleRequestHandlerMethods handlerMethods) {
        this.handlerMethods = handlerMethods;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "text/html");
        handlerMethods.renderPrivacyRequestTemplate(RequestContext.get(request), request, response);
    }

    @Override
    public String getPath() {
        return "/privacy-request";
    }
}