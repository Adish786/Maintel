package com.about.mantle.endpoint.controllers;

import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResoundReactController extends AbstractMantleEndpointController {
    private final MantleRequestHandlerMethods handlerMethods;

    public ResoundReactController(MantleRequestHandlerMethods handlerMethods) {
        this.handlerMethods = handlerMethods;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AbstractMantleEndpointController.Operation operation = parseRequestOperation(request);
        handlerMethods.submitResoundReact(request, response, operation);
    }

    @Override
    public String getPath() {
        return "/resound/react/{operation:save|delete}";
    }
}
