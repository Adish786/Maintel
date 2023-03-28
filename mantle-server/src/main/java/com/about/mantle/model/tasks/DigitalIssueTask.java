package com.about.mantle.model.tasks;

import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.url.VerticalUrlData;
import com.about.mantle.model.json.JsonTask;

import java.util.ArrayList;
import java.util.HashMap;

@Tasks
public class DigitalIssueTask {

    private final BusinessOwnedVerticalDataTask bovdTask;
	private final JsonTask jsonTask;
	private final String appName;

	public DigitalIssueTask(BusinessOwnedVerticalDataTask bovdTask, JsonTask jsonTask, String appName) {
		this.bovdTask = bovdTask;
		this.jsonTask = jsonTask;
		this.appName = appName;
	}

	/**
	 * @see #getDigitalIssueSectionConfigs(Integer, Long)
	 * @return
	 */
    @Task(name = "digitalIssueSectionConfigs")
    public Object getDigitalIssueSectionConfigs(
            @RequestContextTaskParameter RequestContext requestContext,
            @TaskParameter(name = "index") Integer index) {
		Long docId = getDocId(requestContext);

		return getDigitalIssueSectionConfigs(index, docId);
    }

    /**
	 * Returns the digital issue configs for a given journey section index,
	 * based on the current URL. See getDigitalIssueRootConfigs for more details.
     * 
     * @param index the index of the journey section to get the configs for, starting at 0
	 * @param docId the docId of the digital issue
     * 
     * @return the digital issue configs for a section of the journey
     */
    @Task(name = "digitalIssueSectionConfigs")
    public Object getDigitalIssueSectionConfigs(
            @TaskParameter(name = "index") Integer index,
			@TaskParameter(name = "docId") Long docId) {
        HashMap<String, Object> digitalIssueRootConfigs = getDigitalIssueRootConfigs(docId);

        return ((ArrayList<Object>) digitalIssueRootConfigs.get("modules")).get(index);
    }

	/**
	 * @see #getDigitalIssueRootConfigs(Long)
	 * @return
	 */
    @Task(name = "digitalIssueRootConfigs")
    public HashMap<String, Object> getDigitalIssueRootConfigs(
			@RequestContextTaskParameter RequestContext requestContext) {
        Long docId = getDocId(requestContext);

        return getDigitalIssueRootConfigs(docId);
    }

    /**
     * Returns a map of all of the digital issue configs, stored in the BOVD, for a given landing page's docId.
	 * See https://dotdash.atlassian.net/wiki/spaces/GLBE/pages/3246227465/Mantle+Digital+Issue+Implementation
     * for overview.
	 * See di-module.xml for frontend implementation.
	 * 
	 * Requires:
	 * - BOVD path: {app name}/digital-issues/{docid of landing page}.json
	 * - JSON format below. The length of "modules" should equal the number of sections
	 *   in the journey: 
	 * {
	 *     "landing-page-config-example": "landing-page-value-example",
	 *     "landing-page-config-example": "landing-page-value-example",
	 *     "modules": [
	 *         {
	 *             "module-config-example": "module-value-example",
	 *             "module-config-example": "module-value-example"
	 *         },
	 *         {
	 *             "module-config-example": "module-value-example",
	 *             "module-config-example": "module-value-example"
	 *         }
	 *     ]
	 * }
     *
	 * @param docId the docId of the digital issue
     * 
     * @return all of the digital issue configs for the given docId
     */
    @Task(name = "digitalIssueRootConfigs")
    public HashMap<String, Object> getDigitalIssueRootConfigs(
			@TaskParameter(name = "docId") Long docId) {
        String digitalIssueRootConfigsPath = appName + "/digital-issues/" + docId + ".json";
        String digitalIssueRootConfigsBovd = new String(bovdTask.bovdResource(digitalIssueRootConfigsPath));
        HashMap<String, Object> digitalIssueRootConfigs = jsonTask.json(digitalIssueRootConfigsBovd);

        return digitalIssueRootConfigs;
    }

    /**
     * Returns the docId of the current URL
     *
     * @param requestContext
     *
     * @return the docId of the current URL
     */
	private static Long getDocId(RequestContext requestContext) {
		return ((VerticalUrlData) requestContext.getUrlData()).getDocId();
	}
}
