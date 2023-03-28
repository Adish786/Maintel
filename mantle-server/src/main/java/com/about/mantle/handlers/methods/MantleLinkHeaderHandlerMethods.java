package com.about.mantle.handlers.methods;

import com.about.globe.core.definition.resource.ResolvedResource;
import com.about.globe.core.definition.template.RenderManifest;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.http.ua.DeviceCategory;
import com.about.mantle.model.tasks.ResourceTasks;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Handle the link header logic used by MantleRequestHandlerMethods
 *
 * It also has a hook so that verticals can update font link headers from proctor via
 * buildFontLinkHeadersFromProctor
 */
public class MantleLinkHeaderHandlerMethods {

    private final ResourceTasks resourceTasks;
    private static final List<String> OPTIN_ONETRUST_TEMPLATES = List.of("gdpr");

    public MantleLinkHeaderHandlerMethods (ResourceTasks resourceTasks){
        this.resourceTasks = resourceTasks;
    }

    public final void addLinkHeaders (RequestContext requestContext, HttpServletResponse response,
                     RenderManifest renderManifest){

        if (requestContext.isResourceConcat()) {
            List<String> links = new ArrayList<>();
            String host = "//" + requestContext.getServerName();
            if (requestContext.getServerPort() != 80 && requestContext.getServerPort() != 443) {
                host = host + ":" + requestContext.getServerPort();
            }

            if (requestContext.getUserAgent().getDeviceCategory().equals(DeviceCategory.PERSONAL_COMPUTER)) {
                // mobile and tablet devices benefit from having their stylesheets inlined
                List<ResolvedResource> preloadStyles = resourceTasks.stylesheets(requestContext, false, "top", null,
                        renderManifest);
                preloadStyles.removeIf(style -> style == null);
                for (ResolvedResource stylesheet : preloadStyles) {
                    String link = "<" + host + "/static/" + stylesheet.getModule().getProjectInfo().getVersion()
                            + stylesheet.getResource().getPath() + ">; rel=preload; as=style; nopush";
                    links.add(link);
                }
            }

            buildVerticalLinkHeaders(requestContext, host, links);

            String[] groupNames = { "prioritized", "top", "bottom" }; // TODO -- get this from somewhere better!
            List<ResolvedResource> preloadScripts = new ArrayList<ResolvedResource>();
            for (String group : groupNames) {
                preloadScripts.addAll(
                        resourceTasks.scripts(requestContext, false, group, Arrays.asList(groupNames), renderManifest));
            }
            preloadScripts.removeIf(script -> script == null);
            for (ResolvedResource script : preloadScripts) {
                String link = "<" + host + "/static/" + script.getModule().getProjectInfo().getVersion()
                        + script.getResource().getPath() + ">; rel=preload; as=script; nopush; group="
                        + script.getResourceHandle().getGroup();
                links.add(link);
            }

            String primaryImageLinkHeaders = getPrimaryImageLinkHeaders(renderManifest);

            if(StringUtils.isNotBlank(primaryImageLinkHeaders)) {
                links.add(primaryImageLinkHeaders);
            }

            String cmpLibraryLinkHeader = getCMPLibraryLinkHeader(renderManifest);
            if (StringUtils.isNotBlank(cmpLibraryLinkHeader)) {
                // Preload OneTrust SDK entry point script if the CMP is enabled on the page
                links.add(cmpLibraryLinkHeader);
            }

            response.addHeader("Link", StringUtils.join(links, ", "));
        }

        try {
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Meant to be overidden by vertical by default does nothing
     */
    protected void buildVerticalLinkHeaders(RequestContext requestContext, String host, List<String> links){
        for (String requestContextPreloadLink : requestContext.getLinkHeaders()) {
            links.add(requestContextPreloadLink.replace("{HOST}", host));
        }
    }

    private String getCMPLibraryLinkHeader(RenderManifest renderManifest) {
        String uri = evaluateModel(renderManifest, "oneTrustEntryScript");
        String templateName = evaluateModel(renderManifest, "oneTrustTemplateName");
        
        if (StringUtils.isBlank(uri) || StringUtils.isBlank(templateName)) {
            return null;
        }

        return OPTIN_ONETRUST_TEMPLATES.contains(templateName) ? ("<" + uri + ">; rel=preload; as=script; nopush") : null;
    }

    private String evaluateModel(RenderManifest renderManifest, String modelName) {
        final String[] evaluatedModel = new String[] {null};
        Predicate<RenderManifest> predicate = rm -> {
            return StringUtils.isBlank(evaluatedModel[0]);
        };
        Consumer<RenderManifest> consumer = rm -> {
            evaluatedModel[0] = (String) rm.getModel().get(modelName);
        };
        renderManifest.acceptManifests(predicate, consumer);

        return evaluatedModel[0];
    }

    /**
     * This returns the link header entry for primary image.
     * We want to preload primary as soon as we can, so add it in link headers here.
     * However, srcset is being used in image tag which lets browser decide which image to pick
     * up depending on screen size at run time. This method  adds in srcset and other related
     * attributes to final return value for link header.
     * Ref: for reloading responsive images -
     * https://web.dev/preload-responsive-images/
     * @param renderManifest
     * @return
     */
    private String getPrimaryImageLinkHeaders(RenderManifest renderManifest) {
        String answer = "";
        final Map<String, String> primaryImageSources = new LinkedHashMap<>();
        Consumer<RenderManifest> consumer = rm -> {
            @SuppressWarnings("unchecked")
            Map<String, String> sources = (Map<String, String>) rm.getModel().get("providedImageSources");
            if(sources != null) {
                primaryImageSources.putAll(sources);
            }
        };

        renderManifest.acceptManifests((rm) -> true, consumer);

        if(!primaryImageSources.isEmpty()) {
            StringBuilder finalImgLink= new StringBuilder("");

            String imageSrcsetVal = primaryImageSources.get("srcset");
            String imageUriReference = null;
            if(StringUtils.isNotBlank(imageSrcsetVal)) {
                imageUriReference = StringUtils.strip(StringUtils.split(imageSrcsetVal)[0], "\"");
            }else {
                String imageSrcVal = primaryImageSources.get("src");

                if(StringUtils.isNotBlank(imageSrcVal)) {
                    // currently browsers do not support preloading videos see: https://developer.mozilla.org/en-US/docs/Web/HTML/Link_types/preload#:~:text=video%3A%20Video%20file,implemented%20by%20browsers
                    // specifically look for the gifv filter in case a gif is being used without the filter
                    if (imageSrcVal.contains("gifv(")) {
                        return StringUtils.EMPTY;
                    }

                    imageUriReference = StringUtils.strip(imageSrcVal, "\"");
                }
            }

            if(StringUtils.isNotBlank(imageUriReference)) {
                finalImgLink.append("<").append(imageUriReference).append(">").append("; ");
                finalImgLink.append("href=").append(imageUriReference).append("; ");
                finalImgLink.append("rel=preload; as=image; nopush");
            }

            answer = finalImgLink.toString();
        }
        return answer;
    }
}
