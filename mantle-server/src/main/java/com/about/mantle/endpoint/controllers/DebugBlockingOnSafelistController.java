package com.about.mantle.endpoint.controllers;

import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Get json representing parameters that would be blocked
 * Temp for GLBE-8768
 *
 */
public class DebugBlockingOnSafelistController  extends AbstractMantleEndpointController {

    private MantleRequestHandlerMethods handlerMethods;

    public DebugBlockingOnSafelistController(MantleRequestHandlerMethods handlerMethods) {
        this.handlerMethods = handlerMethods;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "application/json");
        handlerMethods.debugBlockedParams(request, response);
    }

    @Override
    public String getPath() {
        return "/debug/safelistblockedparameters.json";
    }

}
