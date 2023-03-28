package com.about.mantle.model.tasks;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import com.about.globe.core.exception.GlobeTaskHaltException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.hippodrome.metrics.annotation.TimedComponent;
import com.about.mantle.model.PageRequest;
import com.about.mantle.model.extended.docv2.CategoryLinkEx;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.services.NavigationLinkService;

@Tasks
public class NavigationLinkTask {

	private final NavigationLinkService navigationLinkService;

	public NavigationLinkTask(NavigationLinkService navigationLinkService) {
		this.navigationLinkService = navigationLinkService;
	}

	@Task(name = "categoryNavigation")
	public SliceableListEx<CategoryLinkEx> getCategoryNavigation(@RequestContextTaskParameter RequestContext requestContext,
			@TaskParameter(name = "pageNum") Integer pageNum, @TaskParameter(name = "itemsPerPage") Integer itemsPerPage) {
		try {
			return getCategoryNavigation(requestContext.getUrlData().with().environment(null).port(null).query(null).build().getCanonicalUrl(), pageNum, itemsPerPage);
		} catch (UnsupportedEncodingException | URISyntaxException e) {
			throw new GlobeTaskHaltException("failed to build url", e);
		}
	}

	@Task(name = "categoryNavigation")
	@TimedComponent(category = "task")
	public SliceableListEx<CategoryLinkEx> getCategoryNavigation(@TaskParameter(name = "url") String url, @TaskParameter(
			name = "pageNum") Integer pageNum, @TaskParameter(name = "itemsPerPage") Integer itemsPerPage) {

		PageRequest pageRequest = PageRequest.fromPageNumberAndSize(pageNum, itemsPerPage);

		return navigationLinkService.getNavigationLinks(url, pageRequest);
	}
}
