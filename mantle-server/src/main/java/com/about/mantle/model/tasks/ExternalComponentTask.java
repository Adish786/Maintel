package com.about.mantle.model.tasks;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.url.UrlData;
import com.about.mantle.model.ExternalComponent;
import com.about.mantle.model.services.ExternalComponentService;

@Tasks
public class ExternalComponentTask {

    private static final Logger logger = LoggerFactory.getLogger(ExternalComponentTask.class);

    private final Map<String, ExternalComponentService> externalServicesMap;
    private final ThreadPoolExecutor threadPoolExecutor;

    public ExternalComponentTask(Map<String, ExternalComponentService> externalServicesMap,
            ThreadPoolExecutor threadPoolExecutor) {
        this.externalServicesMap = externalServicesMap;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Task(name = "externalScBlockComponent")
    public Future<ExternalComponent> externalScBlockComponent(
            @RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "serviceId") String serviceId,
            @TaskParameter(name = "componentId") String componentId,
            @TaskParameter(name = "uuid") String uuid) {
        return getExternalComponent(serviceId, componentId, uuid, requestContext.getUrlData(), false, requestContext);
    }

    @Task(name = "externalScBlockComponent")
    public Future<ExternalComponent> externalScBlockComponent(
            @RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "serviceId") String serviceId,
            @TaskParameter(name = "componentId") String componentId,
            @TaskParameter(name = "uuid") String uuid,
            @TaskParameter(name = "path") String path) {
        UrlData requestUrl;
        try {
            requestUrl = requestContext.getUrlData().with().path(path).build();
        } catch (URISyntaxException e) {
            logger.error("invalid path [{}] for request url", path);
            return null;
        }
        return getExternalComponent(serviceId, componentId, uuid, requestUrl, false, requestContext);
    }

    /**
     * Asynchronously computes an external component `componentId` from service with
     * ID `serviceId`. Services are registered with the
     * {@link ExternalComponentServiceRegistry} class.
     *
     * @param requestContext
     * @param serviceId
     * @param componentId
     * @return
     */
    @Task(name = "externalComponent")
    public Future<ExternalComponent> externalComponent(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "serviceId") String serviceId,
            @TaskParameter(name = "componentId") String componentId) {
        return externalComponent(requestContext, serviceId, componentId, false);
    }

    /**
     * Same as {@link #externalComponent(RequestContext, String, String)} except
     * allows spoofing the path of the
     * request for testing.
     */
    @Task(name = "externalComponent")
    public Future<ExternalComponent> externalComponent(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "serviceId") String serviceId,
            @TaskParameter(name = "componentId") String componentId,
            @TaskParameter(name = "path") String path) {
        return externalComponent(requestContext, serviceId, componentId, path, false);
    }

    /**
     * Same as {@link #externalComponent(RequestContext, String, String, String)}
     * except the component can be optional
     * and will not throw an error if an empty component is returned.
     */
    @Task(name = "externalComponent")
    public Future<ExternalComponent> externalComponent(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "serviceId") String serviceId,
            @TaskParameter(name = "componentId") String componentId,
            @TaskParameter(name = "path") String path,
            @TaskParameter(name = "optional") Boolean optional) {
        UrlData requestUrl;
        try {
            requestUrl = requestContext.getUrlData().with().path(path).build();
        } catch (URISyntaxException e) {
            logger.error("invalid path [{}] for request url", path);
            return null;
        }
        return getExternalComponent(serviceId, componentId, null, requestUrl, optional, requestContext);
    }

    /**
     * Same as {@link #externalComponent(RequestContext, String, String, String)}
     * except a uuid for a specific block can be passed in
     */
    @Task(name = "externalComponent")
    public Future<ExternalComponent> externalComponent(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "serviceId") String serviceId,
            @TaskParameter(name = "componentId") String componentId,
            @TaskParameter(name = "path") String path,
            @TaskParameter(name = "uuid") String uuid) {
        UrlData requestUrl;
        try {
            requestUrl = requestContext.getUrlData().with().path(path).build();
        } catch (URISyntaxException e) {
            logger.error("invalid path [{}] for request url", path);
            return null;
        }
        return getExternalComponent(serviceId, componentId, uuid, requestUrl, false, requestContext);
    }
    /**
     * Same as {@link #externalComponent(RequestContext, String, String)} except the
     * component can be optional
     * and will not throw an error if an empty component is returned.
     */
    @Task(name = "externalComponent")
    public Future<ExternalComponent> externalComponent(@RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "serviceId") String serviceId,
            @TaskParameter(name = "componentId") String componentId,
            @TaskParameter(name = "optional") Boolean optional) {
        return getExternalComponent(serviceId, componentId, null, requestContext.getUrlData(), optional,
                requestContext);
    }

    private Future<ExternalComponent> getExternalComponent(String serviceId, String componentId, String uuid,
            UrlData requestUrl,
            Boolean optional, RequestContext requestContext) {
        final ExternalComponentService service = this.externalServicesMap.get(serviceId);
        if (service == null) {
            logger.error("serviceId [{}] is not a registered service", serviceId);
            return null;
        }
        logger.debug("Getting external component [{}] from service [{}] for requested url [{}]", componentId, serviceId,
                requestUrl.getCanonicalUrl());
        return threadPoolExecutor
                .submit(() -> service.getExternalComponent(componentId, requestUrl, uuid, optional, requestContext));
    }

}
