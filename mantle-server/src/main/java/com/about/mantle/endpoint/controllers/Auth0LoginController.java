package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class Auth0LoginController extends AbstractMantleEndpointController {
    private final MantleRequestHandlerMethods handlerMethods;

    public Auth0LoginController(MantleRequestHandlerMethods handlerMethods) {
        this.handlerMethods = handlerMethods;
    }

    @Override
    public String getPath() {
        return "/auth0/login";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        handlerMethods.ddmAccountAuth(request, response);
    }
}
