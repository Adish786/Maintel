package com.about.mantle.web.filter.redirect;

import javax.servlet.http.HttpServletRequest;

/**
 * For bespoke redirecting.  Returns a URL if the provided request needs to be redirected
 */
public interface RedirectRuleResolver {
    /**
     * Returns a URL if the provided request needs to be redirected, otherwise returns null
     * @param request
     * @return
     */
    public String resolve(HttpServletRequest request);
}
