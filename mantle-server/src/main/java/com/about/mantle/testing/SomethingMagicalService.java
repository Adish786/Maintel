package com.about.mantle.testing;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.mantle.render.MantleRenderUtils;

/**
 * Does something Magical!  Actually, what it's intended to do is to give you a place where you can write some testing
 * code w/in the application.  By default it will do nothing and just return a 404.  If you actually want to use it,
 * in your spring config override the `somethingMagicalService` method to return a subclass of this class that
 * overrides `doSomethingMagical`
 */
public class SomethingMagicalService {

    private final MantleRenderUtils renderUtils;

    public SomethingMagicalService(MantleRenderUtils renderUtils) {
        this.renderUtils = renderUtils;
    }

    public final Object testLogger(RequestContext requestContext, HttpServletRequest request, HttpServletResponse response)
            throws GlobeException, ServletException, IOException {

        if (!(renderUtils.isInternalIp(requestContext.getHeaders().getRemoteIp()))) {
            send404(response);
            return null;
        }

        return doSomethingMagical(response);

    }

    /**
     * Does the magical thing
     * @param resolvedAction
     * @param request
     * @param response
     * @return
     */
    protected Object doSomethingMagical(HttpServletResponse response) {

        send404(response);
        return null;
    }

    protected void send404(HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND_404);
    }
}
