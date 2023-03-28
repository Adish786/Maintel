package com.about.mantle.http;

import com.about.globe.core.exception.GlobeException;
import com.about.mantle.cache.clearance.CacheClearanceCandidateRepo;
import com.about.mantle.cache.clearance.CacheClearanceRequest;
import com.about.mantle.cache.clearance.CacheClearanceThreadLocalUtils;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.log.Log;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Proxies a request to another server.  Thin wrapper around {@link ProxyServlet}
 */
public class MantleHttpProxy {

    private final HttpServlet servlet;

    /**
     * Use {@link MantleHttpProxyBuilder}
     * @param uriRewriter
     * @param hostHeader
     */
    private MantleHttpProxy(UriRewriter uriRewriter, String hostHeader, HttpClient httpClient) {
        this.servlet = new MantleHttpProxyServlet(uriRewriter, hostHeader, httpClient);
        try {
        	servlet.init();
        } catch (ServletException e) {
            throw new GlobeException("Couldn't initialize proxy", e);
        }
    }

    /**
     * Runs the request through the proxy
     */
    public void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            servlet.service(req, resp);
        } catch (ServletException | IOException e) {
            throw new GlobeException("Could not send proxy request", e);
        }
    }

    /**
     * Rewrites the URL used for the proxy
     */
    public static interface UriRewriter {
        String rewrite(String req);
    }

	/**
     * UriRewriter that uses a single regex pass to rewrite
     */
    public static class RegexUriRewriter implements UriRewriter {

        private final String with;
        private final Pattern regexPattern;

        public RegexUriRewriter(String regex, String with) {
            this.with = with;
			this.regexPattern = Pattern.compile(regex);
        }

        @Override
        public String rewrite(String req) {
        	Matcher matcher = regexPattern.matcher(req);
        	return matcher.replaceAll(with);
        }
    }

    private static class MantleHttpProxyServlet extends ProxyServlet {

        private static final long serialVersionUID = 1L;
        private final UriRewriter uriRewriter;
        private final MantleHttpProxyServletConfig servletConfig;
        private final HttpClient httpClient;
        public MantleHttpProxyServlet(UriRewriter uriRewriter, String hostHeader, HttpClient httpClient) {
            this.uriRewriter = uriRewriter;
            this.servletConfig = new MantleHttpProxyServletConfig(hostHeader);
            this.httpClient =  httpClient;
        }

        @Override
        protected void addProxyHeaders(HttpServletRequest clientRequest, Request proxyRequest) {
        	super.addProxyHeaders(clientRequest, proxyRequest);

        	//Add cache clearance header if the request is resupplying caches.
            List<CacheClearanceRequest> cacheClearRequest = CacheClearanceThreadLocalUtils.getClearCacheRequests();

            if(cacheClearRequest != null && cacheClearRequest.size() > 0) {
              proxyRequest = proxyRequest.header(CacheClearanceCandidateRepo.CACHE_CLEARANCE_HEADER, "true");
          }
        }

        /**
         * Required to be overridden for the proxy.
         *
         * @param request
         * @return
         */
        protected String rewriteTarget(HttpServletRequest request) {
            return URI.create(uriRewriter.rewrite(extractFullUri(request))).toString();
        }

        /**
         * Pulls the full URI out of an {@link HttpServletRequest}
         * Thanks to https://stackoverflow.com/a/2222268/295797
         */
        private String extractFullUri(HttpServletRequest request) {
            StringBuffer sb = request.getRequestURL();
            if (request.getQueryString() != null) {
                sb.append('?').append(request.getQueryString());
            }
            return sb.toString();
        }

        @Override
        public ServletConfig getServletConfig() {
            return servletConfig;
        }

        /**
         * Because we need Ssl we can't use the default httpClient out of the box
         */
        @Override
        protected HttpClient newHttpClient() {
            return httpClient;
        }

        /**
         * Overridden because the name created by this logger was difficult to exclude in Logback (GLBE-5809)
         */
        @Override
        protected org.eclipse.jetty.util.log.Logger createLogger() {
            return Log.getLogger(this.getClass().getCanonicalName());
        }
    }

    /**
     * Basically a mock {@link ServletConfig}, since we're not running the servlet as a servlet
     */
    private static class MantleHttpProxyServletConfig implements ServletConfig {

        private final ServletContext servletContext;
        private final Map<String, String> initParams;

        /**
         * @param hostHeader If not provided, proxy will use the host of the caller.
         */
        private MantleHttpProxyServletConfig(String hostHeader) {

            servletContext = new ContextHandler.StaticContext();

            initParams = new HashMap<>();
            initParams.put("hostHeader", hostHeader);
            // In jetty 9.0 maxthreads default value of maxThreads was 256 if not set, which is not the case in jetty 9.4. 
            // So we have to explicitly set the maxthreads value or we have to set `org.eclipse.jetty.server.Executor` initparam in servlet config
            // so that http client in this proxyservlet can use jetty's server thread pool. 
            //Setting it to 256 as before. See usage of maxthreads in AbstractProxyServlet more details.
            //https://stackoverflow.com/a/27625380/1478852
            initParams.put("maxThreads", "256");
        }

        @Override
        public String getServletName() {
            return "mantleHttpProxyServlet";
        }

        @Override
        public ServletContext getServletContext() {
            return servletContext;
        }

        @Override
        public String getInitParameter(String name) {
            return initParams.get(name);
        }

        @Override
        public Enumeration<String> getInitParameterNames() {
            return Collections.enumeration(initParams.keySet());
        }
    }

    public static class MantleHttpProxyBuilder {
        private final UriRewriter uriRewriter;
        private String hostHeader;
        private final HttpClient httpClient;

        public MantleHttpProxyBuilder(UriRewriter uriRewriter, HttpClient httpClient) {
            this.uriRewriter = uriRewriter;
            this.httpClient = httpClient;
        }

        /**
         * Set if you need the host to be explicitly set.  The default is to send the Host header provided by the client
         * of the proxy.
         *
         * {@see https://docs.oracle.com/cd/E49933_01/studio.320/studio_install/src/cidi_studio_reverse_proxy_preserve_host_headers.html}
         * @param hostHeader
         * @return
         */
        public MantleHttpProxyBuilder setHostHeader(String hostHeader) {
            this.hostHeader = hostHeader;
            return this;
        }

        public MantleHttpProxy build() {
            return new MantleHttpProxy(uriRewriter, hostHeader, httpClient);
        }
    }
}
