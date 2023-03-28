package com.about.mantle.endpoint.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.about.mantle.cache.clearance.CacheClearanceCandidateRepo;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

public class DebugCacheClearanceController extends AbstractMantleEndpointController {

    private final MantleRequestHandlerMethods handlerMethods;
    private CacheClearanceCandidateRepo cacheClearanceCandidateRepository;

    public DebugCacheClearanceController(MantleRequestHandlerMethods handlerMethods, CacheClearanceCandidateRepo cacheClearanceCandidateRepository) {
        this.handlerMethods = handlerMethods;
        this.cacheClearanceCandidateRepository = cacheClearanceCandidateRepository;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "text/plain");
        handlerMethods.debugClearCache(request, response, cacheClearanceCandidateRepository);
    }

    @Override
    public String getPath() {
        return "/debug/clearcache";
    }
}
