package com.about.mantle.endpoint.controllers;

import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResoundReviewStatusController extends AbstractMantleEndpointController {
    private final MantleRequestHandlerMethods handlerMethods;

    public ResoundReviewStatusController (MantleRequestHandlerMethods handlerMethods) {
        this.handlerMethods = handlerMethods;
    }

    @Override
    public String getPath() {
        return "/resound/review/status";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        handlerMethods.resoundReviewStatus(request, response);
    }
}
