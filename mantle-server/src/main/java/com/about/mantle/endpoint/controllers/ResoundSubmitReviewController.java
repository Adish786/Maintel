package com.about.mantle.endpoint.controllers;

import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResoundSubmitReviewController extends AbstractMantleEndpointController {
    private final MantleRequestHandlerMethods handlerMethods;

    public ResoundSubmitReviewController(MantleRequestHandlerMethods handlerMethods) {
        this.handlerMethods = handlerMethods;
    }

    @Override
    public String getPath() {
        return "/resound/review/submit";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        handlerMethods.submitResoundReview(request, response);
    }
}
