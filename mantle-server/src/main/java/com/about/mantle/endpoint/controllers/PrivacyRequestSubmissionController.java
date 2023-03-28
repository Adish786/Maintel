package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class PrivacyRequestSubmissionController extends AbstractMantleEndpointController {

    private MantleRequestHandlerMethods handlerMethods;

    public PrivacyRequestSubmissionController(MantleRequestHandlerMethods handlerMethods) {
        this.handlerMethods = handlerMethods;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        handlerMethods.postPrivacyRequestSubmission(request, response);
    }

    @Override
    public String getPath() {
        return "/privacy-request/submit";
    }
}