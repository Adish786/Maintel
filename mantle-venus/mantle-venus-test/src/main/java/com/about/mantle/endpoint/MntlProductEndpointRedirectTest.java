package com.about.mantle.endpoint;

import com.about.mantle.commons.MntlCommonTestMethods;
import com.about.mantle.utils.test.MntlVenusTest;
import com.about.mantle.venus.utils.MntlUrl;
import com.about.venus.core.utils.HTMLUtils;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.google.common.net.HttpHeaders;

import org.junit.jupiter.api.Assertions;
import static org.hamcrest.CoreMatchers.is;

/* For this test we'll need to pass the url like: baseUrl + "/product?url=https://domain"
(e.g."/product?url=https://www.google.com" or "/product?url=https://www.nike.com" etc), pass `true` if checking for
redirect response code 301 and pass the userAgent.
It will check if the response code is 301 and whether or not it has redirected to that specific domain
(https://www.google.com or https://www.nike.com etc). This only affects verticals where CAES is enabled.
If CAES is NOT enabled we expect 404 and should also pass url like above, `false` and the userAgent.
 */
public class MntlProductEndpointRedirectTest extends MntlVenusTest implements MntlCommonTestMethods {

    public void productEndpointRedirectTest(String redirectUrl, boolean isRedirect, String userAgent) {
        String domain = redirectUrl.substring(redirectUrl.lastIndexOf("https://")) + "/";
        String urlToRedirect = new MntlUrl(redirectUrl, true).url();

        WebResponse webResponse = null;
        try {
            webResponse = HTMLUtils.htmlPage(redirectUrl, userAgent).getWebResponse();
        } catch (Exception e) {
            Assertions.fail("Couldn't get web response");
        }

        if (isRedirect) {
            collector.checkThat("Response code is not 301", webResponse.getStatusCode(), is(301));
            collector.checkThat("Url didn't redirect to " + domain,
                    webResponse.getResponseHeaderValue(HttpHeaders.LOCATION), is(domain));
        } else {
            collector.checkThat("Response code is not 404", webResponse.getStatusCode(), is(404));
        }
    };

}
