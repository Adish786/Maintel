package com.about.mantle.model.tasks;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.extended.docv2.BaseDocumentEx;

@Tasks
public interface TemplateNameResolveTask {
    /**
     * Fetches the document (from cache if appropriate) based on the requestContext and returns the vertical specific
     * template name associated with the document.
     * @param requestContext
     * @return
     */
    @Task(name = "getDefaultPresentationTemplateName")
    @TimedComponent(category = "task")
    String getTemplateName(@RequestContextTaskParameter RequestContext requestContext);

    /**
     * Returns the vertical specific template name associated with the document or null if the template can not be
     * resolved.
     * @param document
     * @return
     */
    String getTemplateName(BaseDocumentEx document);
    
    /**
     * Returns the vertical specific amp template name associated with the document.
     */
    @Deprecated
    String getAmpTemplateName(BaseDocumentEx document, String templateName);

    /**
     * Same as {@link #getTemplateName(RequestContext)}, however can appropriately handle URLs that are likely not
     * Dotdash standard (aka legacy docs)
     * @param requestContext
     * @return
     */
    @Task(name = "getDefaultPresentationTemplateName")
    @TimedComponent(category = "task")
    String getTemplateName(@RequestContextTaskParameter RequestContext requestContext,
                                  @TaskParameter(name = "isLikelyLegacyDoc") Boolean isLikelyLegacyDoc);
}
