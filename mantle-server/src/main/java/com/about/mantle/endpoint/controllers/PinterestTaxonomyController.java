package com.about.mantle.endpoint.controllers;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PinterestTaxonomyController extends AbstractMantleEndpointController {

    private MantleRequestHandlerMethods handlerMethods;

    public PinterestTaxonomyController(MantleRequestHandlerMethods handlerMethods) {
        this.handlerMethods = handlerMethods;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "text/xml");
        handlerMethods.renderTemplate("mntl-pinterest-taxonomy-feed-template", RequestContext.get(request), request, response);
    }

    public String getPath() {
        return "/feeds/pinterest-taxonomy";
    }
}