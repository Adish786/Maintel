package com.about.mantle.endpoint.controllers;

import com.about.globe.core.http.RequestContext;
import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_XML;

public class SmartNewsRssFeedController extends AbstractMantleEndpointController {

    private MantleRequestHandlerMethods handlerMethods;

    public SmartNewsRssFeedController(MantleRequestHandlerMethods handlerMethods) {
        this.handlerMethods = handlerMethods;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader(CONTENT_TYPE, TEXT_XML);
        handlerMethods.renderTemplate("mntl-smart-news-feed-template", RequestContext.get(request), request, response);
    }

    @Override
    public String getPath() {
        return "/feeds/smart-news";
    }
}
