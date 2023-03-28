package com.about.mantle.model.services.impl;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.servlet.http.Cookie;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.about.mantle.cache.clearance.CacheClearanceRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Clock;
import com.codahale.metrics.MetricRegistry;

import com.about.globe.core.cache.RedisCacheKey;
import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.RequestHeaders;
import com.about.hippodrome.config.servicediscovery.Service;
import com.about.hippodrome.ehcache.template.CacheTemplate;
import com.about.hippodrome.metrics.transformer.MetricNameBuilder;
import com.about.hippodrome.url.UrlData;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.cache.clearance.CacheClearanceCandidateRepo;
import com.about.mantle.cache.clearance.CacheClearanceThreadLocalUtils;
import com.about.mantle.model.ExternalComponent;
import com.about.mantle.model.services.ExternalComponentService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * An {@link ExternalComponentService} that is Globe-based, meaning we can make
 * some assumptions on how the data is sent
 * and received (e.g. using the existing Globe defer mechanism)
 */
public class GlobeBasedExternalComponentService extends AbstractExternalComponentService {

    private static Logger logger = LoggerFactory.getLogger(GlobeBasedExternalComponentService.class);
    private Client client = createClient();
    private final CacheTemplate<String> resourceCache;
    private final ExecutorService executor;
    private final MetricRegistry metricRegistry;
    private final Clock clock = Clock.defaultClock();

    public GlobeBasedExternalComponentService(String name, Service externalService,
            CacheTemplate<String> resourceCache, ExecutorService executor, MetricRegistry metricRegistry) {
        super(name, externalService);
        this.resourceCache = resourceCache;
        this.executor = defaultIfNull(executor, MoreExecutors.newDirectExecutorService());
        this.metricRegistry = metricRegistry;
    }

    @Override
    public ExternalComponent getExternalComponent(String componentId, UrlData requestUrl, String uuid, Boolean optional,
            RequestContext requestContext) {
        ExternalComponent component;

        // Check against metric registry to make sure it is added
        if (metricRegistry != null) {
            component = measureDeferRequestResponse(componentId, requestUrl, uuid, optional, requestContext);
        } else {
            component = getExternalComponentWrapped(componentId, requestUrl, uuid, optional, requestContext);
        }

        return component;
    }

    private ExternalComponent getExternalComponentWrapped(String componentId, UrlData requestUrl, String uuid,
            Boolean optional,
            RequestContext requestContext) {
        Response response = tryMakeDeferRequest(componentId, requestUrl, uuid, requestContext);
        return tryExtractComponentFromDeferResponse(response, optional);
    }

    private ExternalComponent tryExtractComponentFromDeferResponse(Response response, Boolean optional) {
        ExternalComponent component;
        try {
            component = extractComponentFromDeferResponse(response, optional);
        } catch (Exception e) {
            throw new GlobeException("Could not parse external component response: " + response, e);
        }
        return component;
    }

    private Response tryMakeDeferRequest(String componentId, UrlData requestUrl, String uuid,
            RequestContext requestContext) {
        Response response;
        try {
            response = makeDeferRequest(componentId, requestUrl, uuid, requestContext);
        } catch (Exception e) {
            throw new GlobeException("Could not make external component request for component " + componentId, e);
        }
        return response;
    }

    private ExternalComponent measureDeferRequestResponse(String componentId, UrlData requestUrl, String uuid,
            Boolean optional,
            RequestContext requestContext) {
        MetricNameBuilder metricNameBuilder = MetricNameBuilder.get();

        ExternalComponent response = null;

        metricNameBuilder.setPrefix("task");
        metricNameBuilder.appendFullTag("");
        metricNameBuilder.putTag("componentid", componentId)
                .putTag("servicename", getName());

        final long startTime = clock.getTick();
        try {
            response = getExternalComponentWrapped(componentId, requestUrl, uuid, optional, requestContext);
            metricRegistry
                    .timer(metricNameBuilder.putTag("status", "success")
                            .putTag("responseisempty", Boolean.toString(responseIsEmpty(response))).build())
                    .update(clock.getTick() - startTime, TimeUnit.NANOSECONDS);
            metricNameBuilder.clear();
            return response;

        } finally {
            if (response == null && !optional) {
                metricRegistry
                        .timer(metricNameBuilder.putTag("status", "exception")
                                .putTag("responseisempty", Boolean.toString(response == null)).build())
                        .update(clock.getTick() - startTime, TimeUnit.NANOSECONDS);
                metricNameBuilder.clear();
            }
        }
    }

    private Response makeDeferRequest(String componentId, UrlData requestUrl, String uuid,
            RequestContext requestContext) {
        WebTarget webTarget = client.target(getUri()).path(requestUrl.getPath());
        if (requestUrl.getQueryParams() != null) {
            // GLBE-9484 Encode query params so that Jersey client doesn't try to evaluate
            // any {} as template variables because they're not
            for (Entry<String, List<String>> param : requestUrl.getQueryParams().entrySet()) {
                Object[] values = param.getValue().stream().map(
                        value -> URLEncoder.encode(StringUtils.defaultString(value), StandardCharsets.UTF_8)).toArray();
                if (!param.getKey().equals("uuid")) {
                    webTarget = webTarget.queryParam(param.getKey(), values);
                }
            }
        }
        webTarget = webTarget.queryParam("globeDeferVersion", "2").queryParam("cr", componentId).queryParam("uuid",
                uuid);
        Invocation.Builder builder = webTarget.request().accept(MediaType.APPLICATION_JSON);

        // Add proxy request data
        addProxyHeaders(builder, requestContext);
        addProxyCookies(builder, requestContext.getCookies());

        return builder.get();
    }

    private boolean responseIsEmpty(ExternalComponent response) {
        return response == null || (isBlank(response.getCss()) && isBlank(response.getHtml())
                && isBlank(response.getJavascript()) && isBlank(response.getSvg()));
    }

    private void addProxyHeaders(Invocation.Builder builder, RequestContext requestContext) {

        RequestHeaders requestHeaders = requestContext.getHeaders();
        UrlData urlData = requestContext.getUrlData();

        if (requestHeaders != null) {
            builder.header(HttpHeader.X_FORWARDED_HOST.asString(),
                    defaultIfNull(requestHeaders.getXForwardedHost(), requestHeaders.getHost()));
            builder.header(HttpHeader.X_FORWARDED_PROTO.asString(), defaultIfNull(requestHeaders.getXForwardedProto(),
                    requestHeaders.isRemoteSecure() ? "https" : "http"));
            builder.header(HttpHeader.USER_AGENT.asString(), requestHeaders.getUserAgent());
            builder.header(HttpHeader.ACCEPT.asString(), requestHeaders.getAccept());
            if (requestHeaders.getXFastlyDevice() != null)
                builder.header("X-Fastly-Device", requestHeaders.getXFastlyDevice());

            // Add origin document header if request has a docId. This is useful if document
            // has a legacy url so external models do not need to fetch
            // the document or do a legacy url deionsearch
            if (urlData instanceof VerticalUrlData) {
                VerticalUrlData verticalUrlData = ((VerticalUrlData) urlData);
                if (verticalUrlData.getDocId() != null)
                    builder.header("X-Origin-Document", verticalUrlData.getDocId());
            }

            // set origin request id in proxy headers
            builder.header("X-Origin-RequestId", requestContext.getRequestId());

            // Add cache clearance header if the request is resupplying caches.
            List<CacheClearanceRequest> cacheClearRequest = CacheClearanceThreadLocalUtils.getClearCacheRequests();

            if (cacheClearRequest != null && cacheClearRequest.size() > 0) {
                builder.header(CacheClearanceCandidateRepo.CACHE_CLEARANCE_HEADER, "true");
            }

            // Add GeoData related headers
            addGeoHeaders(builder, requestHeaders);
        }
    }

    private void addGeoHeaders(Invocation.Builder builder, RequestHeaders requestHeaders) {
        builder.header("country_code_iso2", requestHeaders.getCountry());
        builder.header("geo_region", requestHeaders.getRegion());
        builder.header("country_code", requestHeaders.isEuropeanUnion() ? "EU" : null);
    }

    private void addProxyCookies(Invocation.Builder builder, Map<String, Cookie> cookies) {
        if (cookies != null) {
            StringBuilder cookieStringBuilder = new StringBuilder();
            for (Map.Entry<String, Cookie> cookieEntry : cookies.entrySet()) {
                cookieStringBuilder.append(cookieEntry.getKey()).append('=').append(cookieEntry.getValue().getValue())
                        .append("; ");
            }

            // Pass as header to avoid version being appended to value when converting from
            // javax.ws.rs.core.Cookie to javax.servlet.http.Cookie
            String cookieString = cookieStringBuilder.toString();
            if (StringUtils.isNotEmpty(cookieString))
                builder.header("Cookie", cookieString);
        }
    }

    @SuppressWarnings("unchecked")
    private ExternalComponent extractComponentFromDeferResponse(Response response, Boolean optional) {
        // We can't serialize this into an easy-to-use class because the not all of the
        // keys are static.
        HashMap<String, Object> jsonObject = response.readEntity(HashMap.class);

        String componentHtml = null;
        String componentSvg = null;
        String inlineScripts = null;
        String inlineStylesheets = null;
        Future<String> cssFuture = CompletableFuture.completedFuture(StringUtils.EMPTY);
        Future<String> jsFuture = CompletableFuture.completedFuture(StringUtils.EMPTY);

        for (String key : jsonObject.keySet()) {

            if ("externalScripts".equals(key)) {
                List<String> externalScripts = requireAndCast(jsonObject.get(key), List.class);
                jsFuture = CompletableFuture.supplyAsync(() -> loadExternalResources(externalScripts), executor);
            } else if ("externalStylesheets".equals(key)) {
                List<String> externalStylesheets = requireAndCast(jsonObject.get(key), List.class);
                cssFuture = CompletableFuture.supplyAsync(() -> loadExternalResources(externalStylesheets), executor);
            } else if ("inlineSVGs".equals(key)) {
                componentSvg = loadInlineResources(requireAndCast(jsonObject.get(key), List.class));
            } else {
                // At this point we can assume that we're getting a component

                if (componentHtml != null) {
                    throw new GlobeException(
                            "Got multiple components back from deferred response.  Expected only one.  " +
                                    "Response: " + response.toString());
                }

                HashMap component = requireAndCast(jsonObject.get(key), HashMap.class);
                componentHtml = requireAndCast(component.get("html"), String.class);
                inlineScripts = loadInlineResources(requireAndCast(component.get("inlineScripts"), List.class));
                inlineStylesheets = loadInlineResources(requireAndCast(component.get("inlineStylesheets"), List.class));
            }
        }

        StringBuilder componentCssBuilder = new StringBuilder();
        StringBuilder componentJsBuilder = new StringBuilder();

        try {
            componentCssBuilder.append(cssFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Could not asynchronously load external css", e);
        }

        try {
            componentJsBuilder.append(jsFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Could not asynchronously load external js", e);
        }

        String componentCss = inlineStylesheets != null ? componentCssBuilder.append(inlineStylesheets).toString()
                : null;
        String componentJs = inlineScripts != null ? componentJsBuilder.append(inlineScripts).toString() : null;

        if (!BooleanUtils.isTrue(optional) && isBlank(componentHtml) && isBlank(componentCss) && isBlank(componentJs)
                && isBlank(componentSvg)) {
            throw new GlobeException("Component not found on response");
        }

        ExternalComponent.Builder builder = new ExternalComponent.Builder();
        builder.html(componentHtml);
        builder.css(componentCss);
        builder.javascript(componentJs);
        builder.svg(componentSvg);
        return builder.build();
    }

    private String loadExternalResources(List<String> resourcePaths) {
        // Did not implement parallelized requests at this level because in practice
        // resourcePaths is one aggregated request
        // This is not true when debugging, however at that point performance is not a
        // concern
        StringBuilder sb = new StringBuilder();
        for (String resourcePath : resourcePaths) {
            sb.append(getResource(resourcePath)).append('\n');
        }
        return sb.toString();
    }

    private String getResource(String resourcePath) {
        try {
            if (resourceCache == null)
                return getExternalResource(resourcePath);
            CacheKey key = new CacheKey(getName(), resourcePath);
            return resourceCache.get(key, () -> getExternalResource(resourcePath));
        } catch (Exception e) {
            // Return empty string if the external resource could not be fetched
            // successfully
            logger.error("Could not load external resource", e);
            return "";
        }
    }

    private String getExternalResource(String resourcePath) {
        WebTarget webTarget = client.target(getUri()).path(resourcePath);
        Response response = webTarget.request().get();
        // Check status code and throw exception if it is not a 200 in order to bypass
        // the resource cache
        if (response.getStatus() != HttpStatus.OK_200)
            throw new GlobeException("Resource response from external component server failed: " + response);
        return response.readEntity(String.class);
    }

    private String loadInlineResources(List<HashMap> inlineResources) {
        StringBuilder sb = new StringBuilder();
        for (HashMap inlineResource : inlineResources) {
            Object content = inlineResource.get("content");
            if (content != null) {
                sb.append(requireAndCast(content, String.class)).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Ensures that a value is not null and castable to a given type. If not will
     * throw an error
     *
     * @param clazz
     * @return
     */
    private static <T> T requireAndCast(Object o, Class<T> clazz) {

        T answer = null;

        if (o != null) {
            try {
                answer = clazz.cast(o);
            } catch (ClassCastException e) {
                // do nothing, handled later since answer will be null
            }
        }

        if (answer == null) {
            throw new GlobeException("Response from external component server is malformed");
        }

        return answer;
    }

    private static Client createClient() {
        // NOTE we're trusting everyone because @rtruelove said it's okay
        SslContextFactory sslContextFactory = new SslContextFactory(true);
        SSLContext sslContext = null;
        try {
            sslContextFactory.start();
            sslContext = sslContextFactory.getSslContext();
        } catch (Exception e) {
            throw new GlobeException("Failed to get SSLContext", e);
        }
        Client client = ClientBuilder.newBuilder().sslContext(sslContext).hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String requestedHost, SSLSession remoteServerSession) {
                return requestedHost.equalsIgnoreCase(remoteServerSession.getPeerHost());
            }
        }).build();
        return client;
    }

    private static class CacheKey implements Serializable, RedisCacheKey {

        private static final long serialVersionUID = 1L;

        private final String serviceName;
        private final String path;

        public CacheKey(String serviceName, String path) {
            this.serviceName = serviceName;
            this.path = path;
        }

        @Override
        public String getUniqueKey() {
            return this.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
            result = prime * result + ((path == null) ? 0 : path.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CacheKey other = (CacheKey) obj;
            if (serviceName == null) {
                if (other.serviceName != null)
                    return false;
            } else if (!serviceName.equals(other.serviceName))
                return false;
            if (path == null) {
                if (other.path != null)
                    return false;
            } else if (!path.equals(other.path))
                return false;
            return true;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CacheKey{");
            sb.append("serviceName=").append(serviceName);
            sb.append(", path=").append(path);
            sb.append('}');
            return sb.toString();
        }
    }

}
