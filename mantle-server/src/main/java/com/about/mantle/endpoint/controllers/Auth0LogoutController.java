package com.about.mantle.endpoint.controllers;

import com.about.mantle.handlers.methods.MantleRequestHandlerMethods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Auth0LogoutController extends AbstractMantleEndpointController {
        private MantleRequestHandlerMethods handlerMethods;

        public Auth0LogoutController(MantleRequestHandlerMethods handlerMethods) {
            this.handlerMethods = handlerMethods;
        }

        @Override
        public String getPath() {
            return "/auth0/logout";
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
            handlerMethods.deleteCookiesForDDMAccountLogout(request,response);
        }
}
