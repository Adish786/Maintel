package com.about.mantle.spring.interceptor;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.web.filter.GlobeMdcPopulator;
import com.about.hippodrome.url.UrlData;
import com.about.hippodrome.url.VerticalUrlData;

import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;

public class MantleMdcPopulator extends GlobeMdcPopulator {

    /**
     * Executed before the request is sent further down the pipe for rendering.  Most MDC data can be populated
     * here, unless it needs a RequestContext that has a fully populated model in which case it must be done in
     * {@link #populatePostRequestMdc(HttpServletRequest, RequestContext)}
     *
     * @param httpServletRequest
     * @param reqCtx             Might be null
     */
    @Override
    public void populatePreRequestMdc(HttpServletRequest httpServletRequest, RequestContext reqCtx) {

        MDC.clear();

        super.populatePreRequestMdc(httpServletRequest, reqCtx);

        if (reqCtx != null) {
            UrlData urlData = reqCtx.getUrlData();
            populateMdcFromUrlData(urlData);
        }

    }

    /**
     * For other classes that need to populate the MDC but have a reqCtx.
     * @param urlData
     */
    public static void populateMdcFromUrlData(UrlData urlData) {
        if (urlData != null && urlData instanceof VerticalUrlData) {
            VerticalUrlData verticalUrlData = (VerticalUrlData) urlData;
            if (verticalUrlData.getDocId() != null) MDC.put("documentId", verticalUrlData.getDocId().toString());
        }
    }
}
